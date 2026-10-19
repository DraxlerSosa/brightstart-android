package com.brightstart.app.data

import com.brightstart.app.data.api.ApiErrorInterceptor
import com.brightstart.app.data.api.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InterceptorsTest {

    private lateinit var server: MockWebServer
    private var unauthorizedCalls = 0

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        unauthorizedCalls = 0
    }

    @After fun tearDown() { server.shutdown() }

    private fun buildClient(token: String? = "abc123") = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { token })
        .addInterceptor(ApiErrorInterceptor { unauthorizedCalls++ })
        .build()

    private fun get(path: String, client: OkHttpClient = buildClient()) =
        client.newCall(Request.Builder().url(server.url(path)).build()).execute()

    // ---- AuthInterceptor ----
    @Test fun adds_bearer_token_to_protected_endpoints() {
        server.enqueue(MockResponse().setBody("{}"))
        get("/api/get_students.php").close()
        assertEquals("Bearer abc123", server.takeRequest().getHeader("Authorization"))
    }

    @Test fun does_not_send_token_to_public_endpoints() {
        server.enqueue(MockResponse().setBody("{}"))
        get("/api/login.php").close()
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test fun sends_no_header_when_there_is_no_token() {
        server.enqueue(MockResponse().setBody("{}"))
        get("/api/get_students.php", buildClient(token = null)).close()
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    // ---- ApiErrorInterceptor ----
    @Test fun successful_responses_pass_through_untouched() {
        server.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody("""{"success":true}"""))
        val response = get("/api/get_students.php")
        assertEquals(200, response.code)
        assertEquals("""{"success":true}""", response.body!!.string())
        assertEquals(0, unauthorizedCalls)
    }

    @Test fun json_error_bodies_are_kept_so_the_message_can_be_shown() {
        server.enqueue(
            MockResponse().setResponseCode(400).setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Email or username already exists"}""")
        )
        val response = get("/api/register.php")
        assertEquals(200, response.code)
        assertTrue(response.body!!.string().contains("Email or username already exists"))
    }

    @Test fun unauthorized_on_protected_endpoint_signals_expired_session() {
        server.enqueue(
            MockResponse().setResponseCode(401).setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Session expired. Please log in again."}""")
        )
        get("/api/get_students.php").close()
        assertEquals(1, unauthorizedCalls)
    }

    @Test fun wrong_password_on_login_does_not_signal_expired_session() {
        server.enqueue(
            MockResponse().setResponseCode(401).setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Invalid email or password"}""")
        )
        val body = get("/api/login.php").body!!.string()
        assertEquals(0, unauthorizedCalls)
        assertTrue(body.contains("Invalid email or password"))
    }

    @Test fun non_json_server_errors_become_a_friendly_json_message() {
        server.enqueue(MockResponse().setResponseCode(502).setHeader("Content-Type", "text/html").setBody("<html>Bad gateway</html>"))
        val response = get("/api/get_students.php")
        val body = response.body!!.string()
        assertEquals(200, response.code)
        assertTrue(body.contains("\"success\":false"))
        assertFalse(body.contains("<html>"))
        assertTrue(body.contains("unavailable"))
    }
}
