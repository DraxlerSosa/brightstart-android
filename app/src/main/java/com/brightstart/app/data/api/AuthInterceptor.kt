package com.brightstart.app.data.api

import okhttp3.Interceptor
import okhttp3.Response

/** Endpoints that must be callable without a token. */
internal val PUBLIC_ENDPOINTS = setOf("login.php", "register.php", "google_login.php")

/** Adds "Authorization: Bearer <token>" to every request except the public ones. */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val last = request.url.pathSegments.lastOrNull().orEmpty()
        if (last in PUBLIC_ENDPOINTS) return chain.proceed(request)
        val token = tokenProvider()
        val withAuth = if (token.isNullOrBlank()) request
        else request.newBuilder().header("Authorization", "Bearer $token").build()
        return chain.proceed(withAuth)
    }
}
