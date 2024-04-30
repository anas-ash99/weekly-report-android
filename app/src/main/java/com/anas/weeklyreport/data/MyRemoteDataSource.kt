package com.anas.weeklyreport.data


import android.util.Log
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.model.DocumentBody
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.remote.ReportApi
import okhttp3.ResponseBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class MyRemoteDataSource @Inject constructor(
    private val reportApi: ReportApi
) : RemoteDataSource {
    override suspend fun saveReport(report: Report): ResultShort<Report> {
        return try {
            report.userId = AppData.loggedInUser?.id!!
            val response = reportApi.createNewReport(report, "Bearer ${AppData.userToken}")
            if (response.data != null){
                ResultShort.Success(response.data!!)
            }else{
                ResultShort.Error(Exception(response.message))
            }
        }catch (e: SocketTimeoutException){
            Log.e("save report", e.message, e)
            ResultShort.NetworkError(e)
        }
        catch (e:UnknownHostException){
            Log.e("save report", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e:Exception){
            Log.e("save report", e.message, e)
            ResultShort.Error(e)
        }
    }

    override suspend fun updateReport(report: Report): ResultShort<Boolean> {
        return try {
            val response = reportApi.updateReport(report, "Bearer ${AppData.userToken}")

            if (response.data == true){
                ResultShort.Success(true)
            }else{
                ResultShort.Error(Exception(response.message))
            }
        }catch (e:UnknownHostException){
            Log.e("update report", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e: SocketTimeoutException){
            Log.e("update report", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e:Exception){
            Log.e("update report", e.message, e)
            ResultShort.Error(e)
        }
    }

    override suspend fun getReportsByUserId(): ResultShort<List<Report>> {

        return try {
            val userId = AppData.loggedInUser?.id
            println(AppData.userToken)
            val response = reportApi.getReportByUserId(userId!!, "Bearer ${AppData.userToken}")
            if (response.data != null){
                ResultShort.Success(response.data!!)
            }else{
                ResultShort.Error(Exception(response.message!!))
            }
        }catch (e:UnknownHostException){
            Log.e("getAllReports", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e: SocketTimeoutException){
            Log.e("getAllReports", e.message, e)
            ResultShort.NetworkError(e)
        }
        catch (e:Exception){
            Log.e("getAllReports", e.message, e)

            ResultShort.Error(e)
        }

    }
    override suspend fun deleteReport(id: String): ResultShort<Boolean> {
        return try {
            val response = reportApi.deleteReport(id, "Bearer ${AppData.userToken}")
            if (response.data == null || response.data == false) {
                ResultShort.Error(Exception(response.message))
            }else{
                ResultShort.Success(true)
            }
        }catch (e:UnknownHostException){
            Log.e("delete report", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e: SocketTimeoutException){
            Log.e("delete report", e.message, e)
            ResultShort.NetworkError(e)
        }catch (e:Exception){
            Log.e("delete report", e.message, e)
            ResultShort.Error(e)
        }
    }

    override suspend fun getDocument(report: Report): ResponseBody {
        return reportApi.getDocument(DocumentBody(AppData.loggedInUser!!, report))
    }

}


