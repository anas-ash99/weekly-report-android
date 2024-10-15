package com.anas.weeklyreport.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.anas.weeklyreport.AppData.allReports
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.model.openAi.ChatGBTMessage
import com.anas.weeklyreport.model.openAi.CompletionRequest
import com.anas.weeklyreport.BuildConfig
import com.anas.weeklyreport.DataSyncService
import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.data.DataSource
import com.anas.weeklyreport.data.RemoteDataSource
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.remote.OpenAiApi
import com.anas.weeklyreport.shared.NetworkException
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val openaiApi: OpenAiApi,
    private val context: Context,
    private val localDataSource: DataSource,
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreferences: SharedPreferences
):ReportRepository{

    private val openAiApiKey = BuildConfig.OPEN_AI_KEY

    override suspend fun getAllReports(): Flow<Result<Nothing?>> = flow {
        emit(Result.Loading)
        try {
            allReports.value = ArrayList(localDataSource.getAllReports())
            if (loggedInUser?.id?.isNotBlank()!!){
                when(val result = remoteDataSource.getReportsByUserId()){
                    is ResultShort.Error -> emit(Result.Error(result.exception))
                    is ResultShort.Success -> {
                        allReports.value = ArrayList(allReports.value.filter { it.userId.isBlank() || !it.isSynced })
                        // filter out the reports that does exist in not synced allReports
                        allReports.value.addAll(result.data.filter { it1 -> allReports.value.firstOrNull{it2 -> it2.id == it1.id} == null })

                        emit(Result.Success(null))
                        val intent = Intent(context, DataSyncService::class.java)
                        context.startService(intent)
                    }
                    is ResultShort.NetworkError -> {
                        emit(Result.Success(null)) // get report from local db in case of an error
                        emit(Result.Error(NetworkException(result.exception.message!!)))
                    }
                }
            }else{
                emit(Result.Success(null))
            }
        }
        catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(Result.Error(e))
        }
    }


    override suspend fun createReport(report: Report): Flow<Result<Report>> = flow {
        emit(Result.Loading)
        try {
            report.createdAt = LocalDateTime.now().toString()
            report.id = UUID.randomUUID().toString()
            if (loggedInUser?.id?.isNotBlank()!!){
                when(val result = remoteDataSource.saveReport(report)) {
                    is ResultShort.Error -> emit(Result.Error(result.exception))
                    is ResultShort.Success -> {
                        allReports.value.add(0, report)
                        localDataSource.saveReport(report)
                        emit(Result.Success(result.data))
                    }

                    is ResultShort.NetworkError -> {
                        // when a new report save it to local db and sync it later when connoted to internet
                        report.isSynced = false
                        report.userId = ""
                        localDataSource.saveReport(report)
                        allReports.value.add(0, report)
                        emit(Result.Success(report))
                        emit(Result.Error(NetworkException(result.exception.message!!)))
                    }
                }
            }else{
                report.isSynced = false
                localDataSource.saveReport(report)
                allReports.value.add(0, report)
                emit(Result.Success(report))
            }

        }
        catch (e:Exception){
            emit(Result.Error(e))
            Log.e("save report", e.message, e)
        }

    }

    override suspend fun updateReport(report: Report): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            if (loggedInUser?.id?.isNotBlank()!!){
                when(val result = remoteDataSource.updateReport(report)) {
                    is ResultShort.Error -> emit(Result.Error(result.exception))

                    is ResultShort.Success -> {
                        // update report in app data
                        val index = allReports.value.indexOfFirst { it.id == report.id }
                        allReports.value[index] = report
                        emit(Result.Success(true))
                        localDataSource.saveReport(report)
                    }

                    is ResultShort.NetworkError -> {
                        // update report in app data
                        report.isSynced = false
                        val index = allReports.value.indexOfFirst { it.id == report.id }
                        allReports.value[index] = report
                        localDataSource.saveReport(report)
                        emit(Result.Error(NetworkException(result.exception.message!!)))
                    }
                }
            }else{
                report.isSynced = false
                localDataSource.saveReport(report)
                // update report in app data
                val index = allReports.value.indexOfFirst { it.id == report.id }
                allReports.value[index] = report
                emit(Result.Success(true))
            }
        }
        catch (e:Exception){
            emit(Result.Error(e))
            Log.e("save report", e.message, e)
        }
    }

    override suspend fun deleteReport(id: String): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            if (loggedInUser?.id?.isNotBlank()!!){
                when(val result = remoteDataSource.deleteReport(id)){
                    is ResultShort.Success -> {
                        localDataSource.deleteReport(id)
                        allReports.value.removeIf { it.id == id }
                        emit(Result.Success(true))
                    }
                    is ResultShort.Error -> emit(Result.Error(result.exception))
                    is ResultShort.NetworkError ->{ // mark the report as deleted and not synced
                        val report = allReports.value.find { it.id == id }!!
                        report.isDeleted = true
                        report.isSynced = false
                        localDataSource.saveReport(report)
                        allReports.value.removeIf { it.id == id }
                        emit(Result.Error(NetworkException(result.exception.message!!)))
                    }
                }
            }else{
                localDataSource.deleteReport(id)
                allReports.value.removeIf { it.id == id }
                emit(Result.Success(true))
            }
        }catch (e:Exception){
            Log.e("delete report", e.message, e)
            emit(Result.Error(e))
        }
    }


    override suspend fun getDocument(report: Report): Flow<Result<Uri>> = flow {
        emit(Result.Loading)
        try {
            val fileBytes = remoteDataSource.getDocument(report).bytes()
            val contentUri = saveFileToMediaStore("${report.reportNumber}.Ausbildungsnachweis.docx", fileBytes)
            emit(Result.Success(contentUri!!))

        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(Result.Error(e))
        }
    }

    override suspend fun getOpenAiChatResponse(prompt: String): Flow<Result<Report>> = flow {
        emit(Result.Loading)
        try {
            val list = arrayListOf<ChatGBTMessage>()
            val prompt1 = " I do a german ausbildung as ${loggedInUser?.ausbildungDepartment} I'll describe my tasks for each day of the week in german. Please generate a JSON object in the following format, ensuring no day has more than 8 hours of work logged:\n" +
                   "make sure the task descriptions are in german. but the day name in english as it is in the below json\n" +
                   "\"weekdayDescription\": [\n" +
                   "    \n" +
                   "    { \"day\": \"monday\",\n" +
                   "      \"descriptions\": [\n" +
                   "         {\"description\": \"task 1 \", \"hours\": \"4\"},\n" +
                   "         {\"description\": \"task 2 \", \"hours\": \"4\"}\n" +
                   "        ]\n" +
                   "    },\n" +
                   "    { \"day\": \"tuesday\",\n" +
                   "     \"descriptions\": [\n" +
                   "          {\"description\": \"task 1 \", \"hours\": \"2\"},\n" +
                   "          {\"description\": \"task 2 \", \"hours\": \"3\"}\n" +
                   "        ]\n" +
                   "    },\n" +
                   "    { \"day\": \"wednesday\",\n" +
                   "     \"descriptions\": [\n" +
                   "         {\"description\": \"task 1 \", \"hours\": \"1\"},\n" +
                   "         {\"description\": \"task 2 \", \"hours\": \"7\"}\n" +
                   "        ]\n" +
                   "    },\n" +
                   "    { \"day\": \"thursday\", \n" +
                   "     \"descriptions\": [\n" +
                   "       {\"description\": \"task 1 \", \"hours\": \"3\"},\n" +
                   "         {\"description\": \"task 2 \", \"hours\": \"4\"}\n" +
                   "        ]\n" +
                   "     },\n" +
                   "    { \"day\": \"friday\", \n" +
                   "     \"descriptions\": [\n" +
                   "        {\"description\": \"task 1 \", \"hours\": \"4\"},\n" +
                   "         {\"description\": \"task 2 \", \"hours\": \"4\"}\n" +
                   "        ]\n" +
                   "     }\n" +
                   "  ]\n" +
                   "\n" +
                   prompt

            list.add(ChatGBTMessage(role = "system", content = "You are a helpful assistant designed to output JSON."))
            list.add(ChatGBTMessage(role = "user", content = prompt1))
            val request = CompletionRequest(
                model = "gpt-3.5-turbo",
                messages = list
            )
            val res = openaiApi.generateCompletion(apiKey = "Bearer $openAiApiKey", request = request)
            val aiResponse = res.choices[0].message.content.trim()

            val gson = Gson()
            val jsonObject = gson.fromJson(aiResponse, Report::class.java)

            emit(Result.Success(jsonObject))
        }catch (e:Exception){
            Log.e("Error sentChatRequest ", e.message, e)
            emit(Result.Error(e))
        }
    }

    override suspend fun deleteAllLocalReports() {
        localDataSource.deleteAllReports()
    }

    override fun saveDataToLocalCache(value: String, key:String): Boolean{
        return try {
            sharedPreferences.edit().putString(key, value).apply()
           true
        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            false
        }
    }

    override fun getDataFromLocalCache(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    private fun saveFileToMediaStore(filename: String, fileBytes: ByteArray):Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(fileBytes)
            }
        }
        return uri
    }
}