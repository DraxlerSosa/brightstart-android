package com.brightstart.app.data.api

import com.brightstart.app.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface BrightStartApiService {

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Unit>>

    @POST("google_login.php")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @POST("logout.php")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("get_profile.php")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("update_profile.php")
    suspend fun updateProfile(@Body request: ProfileRequest): Response<ProfileResponse>

    @POST("change_password.php")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    @GET("dashboard_stats.php")
    suspend fun getDashboardStats(): Response<StatsResponse>

    @GET("get_students.php")
    suspend fun getStudents(@Query("search") search: String = ""): Response<ApiResponse<List<Student>>>

    @POST("add_student.php")
    suspend fun addStudent(@Body request: StudentRequest): Response<ApiResponse<Unit>>

    @PUT("update_student.php")
    suspend fun updateStudent(@Query("id") id: Int, @Body request: StudentRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_student.php")
    suspend fun deleteStudent(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_volunteers.php")
    suspend fun getVolunteers(@Query("search") search: String = ""): Response<ApiResponse<List<Volunteer>>>

    @POST("add_volunteer.php")
    suspend fun addVolunteer(@Body request: VolunteerRequest): Response<ApiResponse<Unit>>

    @PUT("update_volunteer.php")
    suspend fun updateVolunteer(@Query("id") id: Int, @Body request: VolunteerRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_volunteer.php")
    suspend fun deleteVolunteer(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_programs.php")
    suspend fun getPrograms(): Response<ApiResponse<List<Program>>>

    @POST("add_program.php")
    suspend fun addProgram(@Body request: ProgramRequest): Response<ApiResponse<Unit>>

    @PUT("update_program.php")
    suspend fun updateProgram(@Query("id") id: Int, @Body request: ProgramRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_program.php")
    suspend fun deleteProgram(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_sessions.php")
    suspend fun getSessions(): Response<ApiResponse<List<Session>>>

    @POST("add_session.php")
    suspend fun addSession(@Body request: SessionRequest): Response<ApiResponse<Unit>>

    @PUT("update_session.php")
    suspend fun updateSession(@Query("id") id: Int, @Body request: SessionRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_session.php")
    suspend fun deleteSession(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_enrolments.php")
    suspend fun getEnrolments(): Response<ApiResponse<List<Enrolment>>>

    @POST("add_enrolment.php")
    suspend fun addEnrolment(@Body request: EnrolmentRequest): Response<ApiResponse<Unit>>

    @PUT("update_enrolment.php")
    suspend fun updateEnrolment(@Query("id") id: Int, @Body request: EnrolmentRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_enrolment.php")
    suspend fun deleteEnrolment(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_attendance.php")
    suspend fun getAttendance(): Response<ApiResponse<List<Attendance>>>

    @POST("add_attendance.php")
    suspend fun addAttendance(@Body request: AttendanceRequest): Response<ApiResponse<Unit>>

    @PUT("update_attendance.php")
    suspend fun updateAttendance(@Query("id") id: Int, @Body request: AttendanceRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_attendance.php")
    suspend fun deleteAttendance(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_performance.php")
    suspend fun getPerformance(): Response<ApiResponse<List<Performance>>>

    @POST("add_performance.php")
    suspend fun addPerformance(@Body request: PerformanceRequest): Response<ApiResponse<Unit>>

    @PUT("update_performance.php")
    suspend fun updatePerformance(@Query("id") id: Int, @Body request: PerformanceRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_performance.php")
    suspend fun deletePerformance(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_donations.php")
    suspend fun getDonations(): Response<ApiResponse<List<Donation>>>

    @POST("add_donation.php")
    suspend fun addDonation(@Body request: DonationRequest): Response<ApiResponse<Unit>>

    @PUT("update_donation.php")
    suspend fun updateDonation(@Query("id") id: Int, @Body request: DonationRequest): Response<ApiResponse<Unit>>

    @DELETE("delete_donation.php")
    suspend fun deleteDonation(@Query("id") id: Int): Response<ApiResponse<Unit>>

    @GET("get_admin_users.php")
    suspend fun getAdminUsers(): Response<ApiResponse<List<AdminUser>>>

    @POST("approve_user.php")
    suspend fun approveUser(@Body body: Map<String, Int>): Response<ApiResponse<Unit>>

    @POST("reject_user.php")
    suspend fun rejectUser(@Body body: Map<String, Int>): Response<ApiResponse<Unit>>

    @PUT("update_user_role.php")
    suspend fun updateUserRole(@Body body: Map<String, String>): Response<ApiResponse<Unit>>
}

