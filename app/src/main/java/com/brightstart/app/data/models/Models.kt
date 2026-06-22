package com.brightstart.app.data.models

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data")    val data: T? = null
)

data class StatsResponse(
    @SerializedName("success")               val success: Boolean,
    @SerializedName("students")              val students: Int = 0,
    @SerializedName("volunteers")            val volunteers: Int = 0,
    @SerializedName("programs")              val programs: Int = 0,
    @SerializedName("sessions")              val sessions: Int = 0,
    @SerializedName("enrolments")            val enrolments: Int = 0,
    @SerializedName("donations")             val donations: Int = 0,
    @SerializedName("total_donations_amount") val totalDonationsAmount: Double = 0.0,
    @SerializedName("recent_students")       val recentStudents: List<Student>? = null,
    @SerializedName("recent_donations")      val recentDonations: List<Donation>? = null,
    @SerializedName("upcoming_sessions")     val upcomingSessions: List<Session>? = null
)

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email")     val email: String,
    @SerializedName("username")  val username: String,
    @SerializedName("password")  val password: String,
    @SerializedName("role")      val role: String
)

data class AdminUser(
    @SerializedName("AdminID")   val adminId: Int = 0,
    @SerializedName("FullName")  val fullName: String = "",
    @SerializedName("Email")     val email: String = "",
    @SerializedName("Username")  val username: String = "",
    @SerializedName("Role")      val role: String = "",
    @SerializedName("Verified")  val verified: Int = 0,
    @SerializedName("CreatedAt") val createdAt: String = ""
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: AdminUser? = null
)

data class Student(
    @SerializedName("StudentID")     val studentId: Int = 0,
    @SerializedName("FirstName")     val firstName: String = "",
    @SerializedName("LastName")      val lastName: String = "",
    @SerializedName("DateOfBirth")   val dateOfBirth: String = "",
    @SerializedName("Gender")        val gender: String = "",
    @SerializedName("CommunityArea") val communityArea: String = "",
    @SerializedName("ContactNumber") val contactNumber: String = "",
    @SerializedName("EnrolmentDate") val enrolmentDate: String = ""
) { val fullName get() = "$firstName $lastName" }

data class StudentRequest(
    @SerializedName("first_name")     val firstName: String,
    @SerializedName("last_name")      val lastName: String,
    @SerializedName("date_of_birth")  val dateOfBirth: String,
    @SerializedName("gender")         val gender: String,
    @SerializedName("community_area") val communityArea: String,
    @SerializedName("contact_number") val contactNumber: String,
    @SerializedName("enrolment_date") val enrolmentDate: String
)

data class Volunteer(
    @SerializedName("VolunteerID")       val volunteerId: Int = 0,
    @SerializedName("FullName")          val fullName: String = "",
    @SerializedName("ContactEmail")      val contactEmail: String = "",
    @SerializedName("ContactNumber")     val contactNumber: String = "",
    @SerializedName("SubjectSpeciality") val subjectSpeciality: String = "",
    @SerializedName("JoinDate")          val joinDate: String = "",
    @SerializedName("Status")            val status: String = "Active"
)

data class VolunteerRequest(
    @SerializedName("full_name")          val fullName: String,
    @SerializedName("contact_email")      val contactEmail: String,
    @SerializedName("contact_number")     val contactNumber: String,
    @SerializedName("subject_speciality") val subjectSpeciality: String,
    @SerializedName("join_date")          val joinDate: String,
    @SerializedName("status")             val status: String
)

data class Program(
    @SerializedName("ProgramID")   val programId: Int = 0,
    @SerializedName("ProgramName") val programName: String = "",
    @SerializedName("Description") val description: String = "",
    @SerializedName("StartDate")   val startDate: String = "",
    @SerializedName("EndDate")     val endDate: String = "",
    @SerializedName("Status")      val status: String = "Active"
)

data class ProgramRequest(
    @SerializedName("program_name") val programName: String,
    @SerializedName("description")  val description: String,
    @SerializedName("start_date")   val startDate: String,
    @SerializedName("end_date")     val endDate: String,
    @SerializedName("status")       val status: String
)

data class Session(
    @SerializedName("SessionID")   val sessionId: Int = 0,
    @SerializedName("ProgramID")   val programId: Int = 0,
    @SerializedName("VolunteerID") val volunteerId: Int = 0,
    @SerializedName("SessionDate") val sessionDate: String = "",
    @SerializedName("StartTime")   val startTime: String = "",
    @SerializedName("EndTime")     val endTime: String = "",
    @SerializedName("Venue")       val venue: String = "",
    @SerializedName("ProgramName") val programName: String = "",
    @SerializedName("VolName")     val volunteerName: String = ""
)

data class SessionRequest(
    @SerializedName("program_id")   val programId: Int,
    @SerializedName("volunteer_id") val volunteerId: Int,
    @SerializedName("session_date") val sessionDate: String,
    @SerializedName("start_time")   val startTime: String,
    @SerializedName("end_time")     val endTime: String,
    @SerializedName("venue")        val venue: String
)

data class Enrolment(
    @SerializedName("EnrolmentID")   val enrolmentId: Int = 0,
    @SerializedName("StudentID")     val studentId: Int = 0,
    @SerializedName("ProgramID")     val programId: Int = 0,
    @SerializedName("EnrolmentDate") val enrolmentDate: String = "",
    @SerializedName("Status")        val status: String = "Active",
    @SerializedName("StudentName")   val studentName: String = "",
    @SerializedName("ProgramName")   val programName: String = ""
)

data class EnrolmentRequest(
    @SerializedName("student_id")     val studentId: Int,
    @SerializedName("program_id")     val programId: Int,
    @SerializedName("enrolment_date") val enrolmentDate: String,
    @SerializedName("status")         val status: String
)

data class Attendance(
    @SerializedName("AttendanceID")     val attendanceId: Int = 0,
    @SerializedName("SessionID")        val sessionId: Int = 0,
    @SerializedName("StudentID")        val studentId: Int = 0,
    @SerializedName("AttendanceStatus") val attendanceStatus: String = "Present",
    @SerializedName("Notes")            val notes: String = "",
    @SerializedName("StudentName")      val studentName: String = "",
    @SerializedName("ProgramName")      val programName: String = "",
    @SerializedName("SessionDate")      val sessionDate: String = ""
)

data class AttendanceRequest(
    @SerializedName("session_id")        val sessionId: Int,
    @SerializedName("student_id")        val studentId: Int,
    @SerializedName("attendance_status") val attendanceStatus: String,
    @SerializedName("notes")             val notes: String
)

data class Performance(
    @SerializedName("PerformanceID")  val performanceId: Int = 0,
    @SerializedName("StudentID")      val studentId: Int = 0,
    @SerializedName("ProgramID")      val programId: Int = 0,
    @SerializedName("AssessmentDate") val assessmentDate: String = "",
    @SerializedName("Score")          val score: Double = 0.0,
    @SerializedName("Grade")          val grade: String = "",
    @SerializedName("Comments")       val comments: String = "",
    @SerializedName("StudentName")    val studentName: String = "",
    @SerializedName("ProgramName")    val programName: String = ""
)

data class PerformanceRequest(
    @SerializedName("student_id")      val studentId: Int,
    @SerializedName("program_id")      val programId: Int,
    @SerializedName("assessment_date") val assessmentDate: String,
    @SerializedName("score")           val score: Double,
    @SerializedName("grade")           val grade: String,
    @SerializedName("comments")        val comments: String
)

data class Donation(
    @SerializedName("DonationID")     val donationId: Int = 0,
    @SerializedName("DonorName")      val donorName: String = "",
    @SerializedName("DonorContact")   val donorContact: String = "",
    @SerializedName("DonationAmount") val donationAmount: Double = 0.0,
    @SerializedName("DonationDate")   val donationDate: String = "",
    @SerializedName("DonationType")   val donationType: String = "",
    @SerializedName("Notes")          val notes: String = ""
)

data class DonationRequest(
    @SerializedName("donor_name")      val donorName: String,
    @SerializedName("donor_contact")   val donorContact: String,
    @SerializedName("donation_amount") val donationAmount: Double,
    @SerializedName("donation_date")   val donationDate: String,
    @SerializedName("donation_type")   val donationType: String,
    @SerializedName("notes")           val notes: String
)

