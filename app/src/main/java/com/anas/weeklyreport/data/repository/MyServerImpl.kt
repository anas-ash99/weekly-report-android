package com.anas.weeklyreport.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.anas.weeklyreport.model.openAi.ChatGBTMessage
import com.anas.weeklyreport.model.openAi.CompletionRequest
import com.anas.weeklyreport.BuildConfig
import com.anas.weeklyreport.data.DataState
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.remote.OpenAiApi
import com.anas.weeklyreport.remote.ReportApi
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject



class MyServerImpl @Inject constructor(
    private val reportApi: ReportApi,
    private val openaiApi: OpenAiApi,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
):MyServerRepo{

    private val openAiApiKey = BuildConfig.OPEN_AI_KEY
    override suspend fun getAllReports(): Flow<DataState<ArrayList<Report>>> = flow {
        emit(DataState.Loading)
        try {
            val response = reportApi.getAllReports()
            if (response.data != null){
                emit(DataState.Success(response.data!!))

            }else{
                emit(DataState.Error(Exception(response.message)))
            }

        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override suspend fun saveReport(report: Report): Flow<DataState<Report>> = flow {
        emit(DataState.Loading)
        try {
            val response = reportApi.createNewReport(report)
            if (response.data != null){
                emit(DataState.Success(response.data!!))
            }else{
                emit(DataState.Error(Exception(response.message)))
            }
        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteReport(id: String): Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading)
        try {
            val response = reportApi.deleteReport(id)
            if (response.data != null && response.data != false){

                emit(DataState.Success(true))
            }else{
                emit(DataState.Error(Exception(response.message)))
            }

        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override suspend fun updateReport(report: Report): Flow<DataState<Report>> = flow {
        emit(DataState.Loading)
        try {

        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override suspend fun getDocument(reportId: String, fileName: String): Flow<DataState<Uri>> = flow {
        emit(DataState.Loading)
        try {

            val fileBytes = reportApi.getDocument(reportId).bytes()

            val contentUri = saveFileToMediaStore(context, "$fileName.docx", fileBytes)
            emit(DataState.Success(contentUri!!))

        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override suspend fun getOpenAiChatResponse(prompt: String): Flow<DataState<Report>> = flow {
        emit(DataState.Loading)

        try {
            val list = arrayListOf<ChatGBTMessage>()
           val prompt1 = "I'll describe my tasks for each day of the week in german. Please generate a JSON object in the following format, ensuring no day has more than 8 hours of work logged:\n" +
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

            emit(DataState.Success(jsonObject))
        }catch (e:Exception){
            Log.e("Error sentChatRequest ", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override fun saveDataToLocalCache(value: String, key:String): Flow<DataState<Boolean>> = flow {
        emit(DataState.Loading)
        try {

            sharedPreferences.edit().putString(key, value).apply()
            emit(DataState.Success(true))
        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            emit(DataState.Error(e))
        }
    }

    override fun getDataFromLocalCache(key: String): String?{

        try {

            val data = sharedPreferences.getString(key, null)
            if (data != null){
                return data
            }else{
                throw NotFoundException("Cant find data with key: $key")
            }
        }catch (e:Exception){
            Log.e("getAllReports", e.message, e)
            return null
        }
    }

    private fun saveFileToMediaStore(context: Context, filename: String, fileBytes: ByteArray):Uri? {
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