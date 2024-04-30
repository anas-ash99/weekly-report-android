package com.anas.weeklyreport

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.anas.weeklyreport.data.DataSource
import com.anas.weeklyreport.data.RemoteDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DataSyncService:Service() {

    @Inject lateinit var localDataSource: DataSource
    @Inject lateinit var remoteDataSource: RemoteDataSource
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            syncData()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun syncData(){
        AppData.allReports.value.forEach { report ->  // sync with local and remote db
            if (report.userId.isBlank()|| !report.isSynced ){
                if (report.isDeleted){
                    remoteDataSource.deleteReport(report.id)
                    localDataSource.deleteReport(report.id)
                }
                if (report.userId.isNotBlank()){
                    report.userId = AppData.loggedInUser?.id!!
                    remoteDataSource.updateReport(report)
                }else{
                    remoteDataSource.saveReport(report)
                }
            }
            localDataSource.saveReport(report)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}