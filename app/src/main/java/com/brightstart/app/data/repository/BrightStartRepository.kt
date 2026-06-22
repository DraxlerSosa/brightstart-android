package com.brightstart.app.data.repository

import com.brightstart.app.data.api.RetrofitClient
import com.brightstart.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrightStartRepository {
    private val api = RetrofitClient.instance

    private suspend fun <T> safe(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try { Result.success(block()) } catch (e: Exception) { Result.failure(e) }
        }

    suspend fun login(email: String, password: String)   = safe { api.login(LoginRequest(email, password)) }
    suspend fun register(fn: String, em: String, un: String, pw: String, role: String) =
        safe { api.register(RegisterRequest(fn, em, un, pw, role)) }

    suspend fun getDashboardStats() = safe { api.getDashboardStats() }

    suspend fun getStudents(search: String = "")            = safe { api.getStudents(search) }
    suspend fun addStudent(r: StudentRequest)               = safe { api.addStudent(r) }
    suspend fun updateStudent(id: Int, r: StudentRequest)   = safe { api.updateStudent(id, r) }
    suspend fun deleteStudent(id: Int)                      = safe { api.deleteStudent(id) }

    suspend fun getVolunteers(search: String = "")          = safe { api.getVolunteers(search) }
    suspend fun addVolunteer(r: VolunteerRequest)           = safe { api.addVolunteer(r) }
    suspend fun updateVolunteer(id: Int, r: VolunteerRequest) = safe { api.updateVolunteer(id, r) }
    suspend fun deleteVolunteer(id: Int)                    = safe { api.deleteVolunteer(id) }

    suspend fun getPrograms()                               = safe { api.getPrograms() }
    suspend fun addProgram(r: ProgramRequest)               = safe { api.addProgram(r) }
    suspend fun updateProgram(id: Int, r: ProgramRequest)   = safe { api.updateProgram(id, r) }
    suspend fun deleteProgram(id: Int)                      = safe { api.deleteProgram(id) }

    suspend fun getSessions()                               = safe { api.getSessions() }
    suspend fun addSession(r: SessionRequest)               = safe { api.addSession(r) }
    suspend fun updateSession(id: Int, r: SessionRequest)   = safe { api.updateSession(id, r) }
    suspend fun deleteSession(id: Int)                      = safe { api.deleteSession(id) }

    suspend fun getEnrolments()                             = safe { api.getEnrolments() }
    suspend fun addEnrolment(r: EnrolmentRequest)           = safe { api.addEnrolment(r) }
    suspend fun updateEnrolment(id: Int, r: EnrolmentRequest) = safe { api.updateEnrolment(id, r) }
    suspend fun deleteEnrolment(id: Int)                    = safe { api.deleteEnrolment(id) }

    suspend fun getAttendance()                             = safe { api.getAttendance() }
    suspend fun addAttendance(r: AttendanceRequest)         = safe { api.addAttendance(r) }
    suspend fun updateAttendance(id: Int, r: AttendanceRequest) = safe { api.updateAttendance(id, r) }
    suspend fun deleteAttendance(id: Int)                   = safe { api.deleteAttendance(id) }

    suspend fun getPerformance()                            = safe { api.getPerformance() }
    suspend fun addPerformance(r: PerformanceRequest)       = safe { api.addPerformance(r) }
    suspend fun updatePerformance(id: Int, r: PerformanceRequest) = safe { api.updatePerformance(id, r) }
    suspend fun deletePerformance(id: Int)                  = safe { api.deletePerformance(id) }

    suspend fun getDonations()                              = safe { api.getDonations() }
    suspend fun addDonation(r: DonationRequest)             = safe { api.addDonation(r) }
    suspend fun updateDonation(id: Int, r: DonationRequest) = safe { api.updateDonation(id, r) }
    suspend fun deleteDonation(id: Int)                     = safe { api.deleteDonation(id) }

    suspend fun getAdminUsers()                             = safe { api.getAdminUsers() }
    suspend fun approveUser(id: Int)  = safe { api.approveUser(mapOf("admin_id" to id)) }
    suspend fun rejectUser(id: Int)   = safe { api.rejectUser(mapOf("admin_id" to id)) }
    suspend fun updateUserRole(id: Int, role: String) =
        safe { api.updateUserRole(mapOf("admin_id" to id.toString(), "role" to role)) }
}

