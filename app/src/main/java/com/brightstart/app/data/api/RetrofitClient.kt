package com.brightstart.app.data.api

import com.brightstart.app.BrightStartApp
import com.brightstart.app.BuildConfig
import com.brightstart.app.utils.SessionEvents
import com.brightstart.app.utils.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val sessionManager by lazy { SessionManager(BrightStartApp.instance) }

    private val okHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            // Added first so every request (except login/register) carries the token
            .addInterceptor(AuthInterceptor { runBlocking { sessionManager.token() } })

        // Only log in debug builds, and never request bodies (they contain passwords)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
                redactHeader("Authorization")
            })
        }

        builder
            .addInterceptor(ApiErrorInterceptor {
                runBlocking { sessionManager.clearSession() }
                SessionEvents.notifyExpired()
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val instance: BrightStartApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrightStartApiService::class.java)
    }
}
