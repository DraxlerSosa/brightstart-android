package com.brightstart.app.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ValidatorsTest {

    // ---- email ----
    @Test fun email_accepts_valid_addresses() {
        assertNull(Validators.email("admin@brightstart.org"))
        assertNull(Validators.email("  thandi.nkosi@gmail.com "))
    }

    @Test fun email_rejects_invalid_addresses() {
        assertNotNull(Validators.email(""))
        assertNotNull(Validators.email("not-an-email"))
        assertNotNull(Validators.email("missing@tld"))
        assertNotNull(Validators.email("@brightstart.org"))
    }

    // ---- password (must match the server rule) ----
    @Test fun password_requires_length_letter_and_number() {
        assertNull(Validators.password("BrightStart@2026"))
        assertNull(Validators.password("abcdefg1"))
        assertEquals("Password is required", Validators.password(""))
        assertEquals("Password must be at least 8 characters", Validators.password("abc123"))
        assertEquals("Password must contain a letter and a number", Validators.password("abcdefgh"))
        assertEquals("Password must contain a letter and a number", Validators.password("12345678"))
    }

    @Test fun passwords_must_match() {
        assertNull(Validators.passwordsMatch("abc12345", "abc12345"))
        assertEquals("Passwords do not match", Validators.passwordsMatch("abc12345", "abc12346"))
    }

    // ---- username / names ----
    @Test fun username_rules() {
        assertNull(Validators.username("dennis.admin"))
        assertNotNull(Validators.username("ab"))
        assertNotNull(Validators.username("has space"))
        assertNotNull(Validators.username(""))
    }

    @Test fun full_name_rules() {
        assertNull(Validators.fullName("Lerato Molefe"))
        assertNotNull(Validators.fullName(" "))
        assertNotNull(Validators.fullName("A"))
    }

    @Test fun required_checks_blank_and_length() {
        assertNull(Validators.required("Mamelodi", "Community area"))
        assertEquals("Venue is required", Validators.required("   ", "Venue"))
        assertNotNull(Validators.required("x".repeat(101), "Venue"))
    }

    // ---- phone ----
    @Test fun phone_accepts_south_african_numbers() {
        assertNull(Validators.phone("0821234567"))
        assertNull(Validators.phone("082 123 4567"))
        assertNull(Validators.phone("+27821234567"))
    }

    @Test fun phone_rejects_bad_numbers() {
        assertNotNull(Validators.phone(""))
        assertNotNull(Validators.phone("12345"))
        assertNotNull(Validators.phone("08212345678"))
        assertNotNull(Validators.phone("abcdefghij"))
    }

    @Test fun email_or_phone_accepts_either() {
        assertNull(Validators.emailOrPhone("info@mogaletrust.org.za"))
        assertNull(Validators.emailOrPhone("0821234567"))
        assertNotNull(Validators.emailOrPhone("hello"))
        assertNotNull(Validators.emailOrPhone(""))
    }

    // ---- dates and times ----
    @Test fun date_must_be_real_calendar_date() {
        assertNull(Validators.date("2026-02-28"))
        assertNotNull(Validators.date("2026-02-30"))
        assertNotNull(Validators.date("28/02/2026"))
        assertNotNull(Validators.date(""))
    }

    @Test fun past_date_must_be_before_today() {
        val today = LocalDate.of(2026, 10, 18)
        assertNull(Validators.pastDate("2011-03-14", "Date of birth", today))
        assertNotNull(Validators.pastDate("2026-10-18", "Date of birth", today))
        assertNotNull(Validators.pastDate("2030-01-01", "Date of birth", today))
    }

    @Test fun date_order_end_cannot_precede_start() {
        assertNull(Validators.dateOrder("2026-02-02", "2026-11-27"))
        assertNull(Validators.dateOrder("2026-02-02", "2026-02-02"))
        assertNotNull(Validators.dateOrder("2026-11-27", "2026-02-02"))
        assertNotNull(Validators.dateOrder("", "2026-02-02"))
    }

    @Test fun time_and_time_order() {
        assertNull(Validators.time("15:00", "Start time"))
        assertNotNull(Validators.time("25:00", "Start time"))
        assertNotNull(Validators.time("9:00", "Start time"))
        assertNull(Validators.timeOrder("15:00", "17:00"))
        assertEquals("End time must be after start time", Validators.timeOrder("17:00", "15:00"))
        assertEquals("End time must be after start time", Validators.timeOrder("15:00", "15:00"))
    }

    // ---- numbers ----
    @Test fun amount_must_be_positive_number() {
        assertNull(Validators.amount("5000"))
        assertNull(Validators.amount("150.50"))
        assertEquals("Amount is required", Validators.amount(""))
        assertEquals("Enter a valid amount", Validators.amount("abc"))
        assertEquals("Amount must be greater than zero", Validators.amount("0"))
        assertEquals("Amount must be greater than zero", Validators.amount("-20"))
        assertEquals("Enter a valid amount", Validators.amount("NaN"))
    }

    @Test fun score_must_be_between_0_and_100() {
        assertNull(Validators.score("0"))
        assertNull(Validators.score("78.5"))
        assertNull(Validators.score("100"))
        assertNotNull(Validators.score("100.1"))
        assertNotNull(Validators.score("-1"))
        assertNotNull(Validators.score("abc"))
        assertNotNull(Validators.score(""))
    }

    // ---- helper ----
    @Test fun first_error_returns_first_failure_or_null() {
        assertNull(Validators.firstError(null, null))
        assertEquals("b", Validators.firstError(null, "b", "c"))
    }
}
