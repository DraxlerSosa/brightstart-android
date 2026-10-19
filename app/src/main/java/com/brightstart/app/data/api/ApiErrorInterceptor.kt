package com.brightstart.app.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * The API reports problems as HTTP 4xx/5xx with a JSON body such as
 * {"success":false,"message":"..."}. Retrofit would hide that body, so this
 * interceptor keeps the JSON (so the app can show the server's message) and
 * turns non-JSON failures (e.g. a gateway error page) into a JSON message too.
 * A 401 on a protected endpoint means the login token expired, so [onUnauthorized] fires.
 */
class ApiErrorInterceptor(private val onUnauthorized: () -> Unit) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isSuccessful) return response

        val isPublic = request.url.pathSegments.lastOrNull().orEmpty() in PUBLIC_ENDPOINTS
        if (response.code == 401 && !isPublic) onUnauthorized()

        val body = response.body
        val type = body?.contentType()
        if (body != null && type?.subtype == "json") {
            val text = body.string()
            return response.newBuilder().code(200).message("OK").body(text.toResponseBody(type)).build()
        }

        val message = when (response.code) {
            401 -> "Session expired. Please log in again."
            403 -> "You do not have permission to do that"
            404 -> "The server could not find that request"
            in 500..599 -> "The server is unavailable. Please try again shortly."
            else -> "Request failed (${response.code})"
        }
        response.close()
        val json = "{\"success\":false,\"message\":\"$message\"}"
        return response.newBuilder().code(200).message("OK")
            .body(json.toResponseBody("application/json".toMediaType())).build()
    }
}
