package com.anas.weeklyreport.di

import android.content.Context
import android.content.SharedPreferences
import com.anas.weeklyreport.domain.repository.MyServerImpl
import com.anas.weeklyreport.domain.repository.MyServerRepo
import com.anas.weeklyreport.remote.OpenAiApi
import com.anas.weeklyreport.remote.ReportApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesString():String{
        return "hello"
    }

    @Provides
    @Singleton
    @Named("MyServerRetrofit")
    fun provideMyServerRetrofit():Retrofit{
        val baseUrlRemote ="https://weekly-report-server-a280ea8d46ca.herokuapp.com/"
        val baseUrlLocal = "http://10.0.2.2:8080"
        val baseUrlLocalRealDevice = "http://192.168.250.18:8080" // get the ipv4 address by running ipconfig in the command line
        return Retrofit.Builder()
            .baseUrl(baseUrlRemote)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }
    @Provides
    @Singleton
    fun provideReportApi(@Named("MyServerRetrofit") retrofit: Retrofit):ReportApi{
        return retrofit.create(ReportApi::class.java)
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
    fun provideServerRepo(@ApplicationContext context: Context, reportApi: ReportApi, openAiApi:OpenAiApi,sharedPreferences:SharedPreferences  ):MyServerRepo{
        return MyServerImpl(reportApi,openAiApi,  context, sharedPreferences )
    }
    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context):SharedPreferences{
        return  context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    }
}