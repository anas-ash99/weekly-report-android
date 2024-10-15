package com.anas.weeklyreport.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.anas.weeklyreport.data.DataSource
import com.anas.weeklyreport.data.MyRemoteDataSource
import com.anas.weeklyreport.data.RemoteDataSource
import com.anas.weeklyreport.data.RoomDataSource
import com.anas.weeklyreport.data.repository.EmailRepository
import com.anas.weeklyreport.data.repository.EmailRepositoryImpl
import com.anas.weeklyreport.data.repository.ReportRepository
import com.anas.weeklyreport.data.repository.ReportRepositoryImpl
import com.anas.weeklyreport.data.repository.UserRepoImpl
import com.anas.weeklyreport.data.repository.UserRepository
import com.anas.weeklyreport.database.AppDatabase
import com.anas.weeklyreport.database.ReportDao
import com.anas.weeklyreport.remote.EmailAPI
import com.anas.weeklyreport.remote.OpenAiApi
import com.anas.weeklyreport.remote.ReportApi
import com.anas.weeklyreport.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    @Named("MyServerRetrofit")
    fun provideMyServerRetrofit():Retrofit{
        val okHttpClient =OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val baseUrlRemote ="https://weekly-report-server-a280ea8d46ca.herokuapp.com/"
        val baseUrlLocal = "http://10.0.2.2:8080"
        val baseUrlLocalRealDevice = "http://192.168.0.82:8080" // get the ipv4 address by running ipconfig in the command line
        return Retrofit.Builder()
            .baseUrl(baseUrlLocalRealDevice)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    @Provides
    @Singleton
    fun provideReportApi(@Named("MyServerRetrofit") retrofit: Retrofit):ReportApi{
        return retrofit.create(ReportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(@Named("MyServerRetrofit") retrofit: Retrofit):UserApi{
        return retrofit.create(UserApi::class.java)
    }
    @Provides
    @Singleton
    fun provideEmailApi(@Named("MyServerRetrofit") retrofit: Retrofit):EmailAPI{
        return retrofit.create(EmailAPI::class.java)
    }
    @Provides
    @Singleton
    @Named("OpenAiRetrofit")
    fun provideOpenaiRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context, userApi: UserApi, sharedPreferences: SharedPreferences):UserRepository{
        return UserRepoImpl(context, userApi, sharedPreferences)
    }
    @Provides
    @Singleton
    fun provideEmailRepository(emailAPI: EmailAPI):EmailRepository{
        return EmailRepositoryImpl(emailAPI)
    }
    @Provides
    @Singleton
    fun provideOpenaiApi():OpenAiApi{
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC  // Log request line and headers
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(OpenAiApi::class.java)
    }


    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context):SharedPreferences{
        return  context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context:Context):AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideReportDao(db:AppDatabase):ReportDao{
        return db.reportDao()
    }
    @Provides
    @Singleton
    fun provideRemoteDataSource( reportApi: ReportApi):RemoteDataSource{
        return MyRemoteDataSource(reportApi)
    }
    @Provides
    @Singleton
    fun provideLocalDataSource( reportDao: ReportDao): DataSource {
        return RoomDataSource(reportDao)
    }
    @Provides
    @Singleton
    fun provideServerRepo(@ApplicationContext context: Context,openAiApi:OpenAiApi,sharedPreferences:SharedPreferences, localDataSource: DataSource, remoteDataSource: RemoteDataSource ):ReportRepository{
        return ReportRepositoryImpl(openAiApi,  context, localDataSource, remoteDataSource, sharedPreferences )
    }

}