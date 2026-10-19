package com.brightstart.app.data

import com.brightstart.app.data.models.ApiResponse
import com.brightstart.app.data.models.LoginResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelParsingTest {
    private val gson = Gson()

    @Test fun login_response_exposes_token_and_user() {
        val json = """
            {"success":true,"message":"Login successful","token":"t0k3n",
             "user":{"AdminID":1,"FullName":"Dennis Moima","Email":"admin@brightstart.org",
                     "Username":"dennis.admin","Role":"Admin","Verified":1,"CreatedAt":"2026-10-18 08:00:00"}}
        """.trimIndent()
        val parsed = gson.fromJson(json, LoginResponse::class.java)
        assertTrue(parsed.success)
        assertEquals("t0k3n", parsed.token)
        assertNotNull(parsed.user)
        assertEquals("Admin", parsed.user!!.role)
        assertEquals("dennis.admin", parsed.user!!.username)
    }

    @Test fun failed_login_has_message_and_no_token() {
        val parsed = gson.fromJson("""{"success":false,"message":"Invalid email or password"}""", LoginResponse::class.java)
        assertFalse(parsed.success)
        assertEquals("Invalid email or password", parsed.message)
        assertNull(parsed.token)
        assertNull(parsed.user)
    }

    @Test fun generic_error_response_parses_without_data() {
        val parsed = gson.fromJson(
            """{"success":false,"message":"You do not have permission to do that"}""",
            ApiResponse::class.java
        )
        assertFalse(parsed.success)
        assertNull(parsed.data)
    }
}
