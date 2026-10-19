package com.brightstart.app.utils

import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Input validation shared by every form. Each function returns null when the
 * value is valid, or a short human-readable message when it is not, so the UI
 * can show the message instead of crashing or sending bad data to the API.
 */
object Validators {

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val USERNAME = Regex("^[A-Za-z0-9_.]{3,30}$")
    private val PHONE = Regex("^(\\+27|0)[0-9]{9}$")
    private val TIME = Regex("^([01][0-9]|2[0-3]):[0-5][0-9]$")

    fun required(value: String, label: String, maxLength: Int = 100): String? = when {
        value.isBlank() -> "$label is required"
        value.trim().length > maxLength -> "$label must be $maxLength characters or fewer"
        else -> null
    }

    fun fullName(value: String): String? =
        required(value, "Full name") ?: if (value.trim().length < 2) "Full name is too short" else null

    fun email(value: String): String? = when {
        value.isBlank() -> "Email is required"
        !EMAIL.matches(value.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun username(value: String): String? = when {
        value.isBlank() -> "Username is required"
        !USERNAME.matches(value.trim()) -> "Username must be 3-30 letters, numbers, . or _"
        else -> null
    }

    /** Same rule the server enforces: 8+ characters with at least one letter and one number. */
    fun password(value: String): String? = when {
        value.isEmpty() -> "Password is required"
        value.length < 8 -> "Password must be at least 8 characters"
        !value.any { it.isLetter() } || !value.any { it.isDigit() } ->
            "Password must contain a letter and a number"
        else -> null
    }

    fun passwordsMatch(a: String, b: String): String? =
        if (a != b) "Passwords do not match" else null

    /** South African mobile/landline: 0XXXXXXXXX or +27XXXXXXXXX (spaces and dashes allowed). */
    fun phone(value: String): String? {
        val cleaned = value.replace(" ", "").replace("-", "")
        return when {
            cleaned.isEmpty() -> "Contact number is required"
            !PHONE.matches(cleaned) -> "Enter a valid phone number, e.g. 0821234567"
            else -> null
        }
    }

    /** Accepts an email address or a phone number (used for donor contact details). */
    fun emailOrPhone(value: String): String? =
        if (value.isBlank()) "Contact details are required"
        else if (email(value) == null || phone(value) == null) null
        else "Enter a valid email address or phone number"

    fun date(value: String, label: String = "Date"): String? {
        if (value.isBlank()) return "$label is required"
        return try { LocalDate.parse(value.trim()); null }
        catch (e: DateTimeParseException) { "$label must be a valid date (YYYY-MM-DD)" }
    }

    fun pastDate(value: String, label: String, today: LocalDate = LocalDate.now()): String? =
        date(value, label) ?: if (!LocalDate.parse(value.trim()).isBefore(today)) "$label must be in the past" else null

    fun dateOrder(start: String, end: String, startLabel: String = "Start date", endLabel: String = "End date"): String? =
        date(start, startLabel) ?: date(end, endLabel) ?: if (LocalDate.parse(end.trim()).isBefore(LocalDate.parse(start.trim()))) "$endLabel cannot be before $startLabel" else null

    fun time(value: String, label: String): String? = when {
        value.isBlank() -> "$label is required"
        !TIME.matches(value.trim()) -> "$label must be a valid time (HH:mm)"
        else -> null
    }

    fun timeOrder(start: String, end: String): String? =
        time(start, "Start time") ?: time(end, "End time") ?: if (end.trim() <= start.trim()) "End time must be after start time" else null

    fun amount(value: String): String? {
        val n = value.trim().toDoubleOrNull() ?: return if (value.isBlank()) "Amount is required" else "Enter a valid amount"
        return when {
            n.isNaN() || n.isInfinite() -> "Enter a valid amount"
            n <= 0 -> "Amount must be greater than zero"
            n > 10_000_000 -> "Amount is too large"
            else -> null
        }
    }

    fun score(value: String): String? {
        val n = value.trim().toDoubleOrNull() ?: return if (value.isBlank()) "Score is required" else "Enter a valid score"
        return if (n.isNaN() || n < 0 || n > 100) "Score must be between 0 and 100" else null
    }

    /** Returns the first non-null error from the given checks, or null if all pass. */
    fun firstError(vararg checks: String?): String? = checks.firstOrNull { it != null }
}
