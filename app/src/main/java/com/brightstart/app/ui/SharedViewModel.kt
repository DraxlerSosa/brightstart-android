package com.brightstart.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brightstart.app.data.models.*
import com.brightstart.app.data.repository.BrightStartRepository
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val repo = BrightStartRepository()

    private val _loading        = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error          = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _stats          = MutableLiveData<StatsResponse?>()
    val stats: LiveData<StatsResponse?> = _stats

    private val _students       = MutableLiveData<List<Student>>(emptyList())
    val students: LiveData<List<Student>> = _students

    private val _studentAction  = MutableLiveData<Result<String>>()
    val studentAction: LiveData<Result<String>> = _studentAction

    private val _volunteers     = MutableLiveData<List<Volunteer>>(emptyList())
    val volunteers: LiveData<List<Volunteer>> = _volunteers

    private val _volunteerAction = MutableLiveData<Result<String>>()
    val volunteerAction: LiveData<Result<String>> = _volunteerAction

    private val _programs       = MutableLiveData<List<Program>>(emptyList())
    val programs: LiveData<List<Program>> = _programs

    private val _programAction  = MutableLiveData<Result<String>>()
    val programAction: LiveData<Result<String>> = _programAction

    private val _sessions       = MutableLiveData<List<Session>>(emptyList())
    val sessions: LiveData<List<Session>> = _sessions

    private val _sessionAction  = MutableLiveData<Result<String>>()
    val sessionAction: LiveData<Result<String>> = _sessionAction

    private val _enrolments     = MutableLiveData<List<Enrolment>>(emptyList())
    val enrolments: LiveData<List<Enrolment>> = _enrolments

    private val _enrolmentAction = MutableLiveData<Result<String>>()
    val enrolmentAction: LiveData<Result<String>> = _enrolmentAction

    private val _attendanceList = MutableLiveData<List<Attendance>>(emptyList())
    val attendanceList: LiveData<List<Attendance>> = _attendanceList

    private val _attendanceAction = MutableLiveData<Result<String>>()
    val attendanceAction: LiveData<Result<String>> = _attendanceAction

    private val _performanceList = MutableLiveData<List<Performance>>(emptyList())
    val performanceList: LiveData<List<Performance>> = _performanceList

    private val _performanceAction = MutableLiveData<Result<String>>()
    val performanceAction: LiveData<Result<String>> = _performanceAction

    private val _donations      = MutableLiveData<List<Donation>>(emptyList())
    val donations: LiveData<List<Donation>> = _donations

    private val _donationAction = MutableLiveData<Result<String>>()
    val donationAction: LiveData<Result<String>> = _donationAction

    private val _adminUsers     = MutableLiveData<List<AdminUser>>(emptyList())
    val adminUsers: LiveData<List<AdminUser>> = _adminUsers

    private val _adminAction    = MutableLiveData<Result<String>>()
    val adminAction: LiveData<Result<String>> = _adminAction
    private val _registerResult = MutableLiveData<Result<String>?>()
    val registerResult: LiveData<Result<String>?> = _registerResult

    fun loadDashboard() = viewModelScope.launch {
        _loading.value = true
        repo.getDashboardStats().fold(
            onSuccess = { _stats.value = it.body() },
            onFailure = { _error.value = it.message }
        )
        _loading.value = false
    }

    fun loadStudents(search: String = "") = viewModelScope.launch {
        _loading.value = true
        repo.getStudents(search).fold(
            onSuccess = { _students.value = it.body()?.data ?: emptyList() },
            onFailure = { _error.value = it.message }
        )
        _loading.value = false
    }

    fun register(fullName: String, email: String, username: String, password: String, role: String) {
        _loading.value = true
        viewModelScope.launch {
            repo.register(fullName, email, username, password, role).fold(
                onSuccess = { r ->
                    if (r.body()?.success == true) _registerResult.value = Result.success(r.body()?.message ?: "Registered")
                    else _registerResult.value = Result.failure(Exception(r.body()?.message ?: "Failed"))
                },
                onFailure = { _registerResult.value = Result.failure(it) }
            )
            _loading.value = false
        }
    }

    fun addStudent(r: StudentRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addStudent(r).fold(
            onSuccess = { res -> if (res.body()?.success == true) { _studentAction.value = Result.success("Student added"); loadStudents() } else _studentAction.value = Result.failure(Exception(res.body()?.message)) },
            onFailure = { _studentAction.value = Result.failure(it) }
        )
        _loading.value = false
    }

    fun updateStudent(id: Int, r: StudentRequest) = viewModelScope.launch {
        _loading.value = true
        repo.updateStudent(id, r).fold(
            onSuccess = { res -> if (res.body()?.success == true) { _studentAction.value = Result.success("Student updated"); loadStudents() } else _studentAction.value = Result.failure(Exception(res.body()?.message)) },
            onFailure = { _studentAction.value = Result.failure(it) }
        )
        _loading.value = false
    }

    fun deleteStudent(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteStudent(id).fold(
            onSuccess = { res -> if (res.body()?.success == true) { _studentAction.value = Result.success("Student deleted"); loadStudents() } else _studentAction.value = Result.failure(Exception(res.body()?.message)) },
            onFailure = { _studentAction.value = Result.failure(it) }
        )
        _loading.value = false
    }

    fun loadVolunteers(search: String = "") = viewModelScope.launch {
        _loading.value = true
        repo.getVolunteers(search).fold(onSuccess = { _volunteers.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addVolunteer(r: VolunteerRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addVolunteer(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _volunteerAction.value = Result.success("Volunteer added"); loadVolunteers() } else _volunteerAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _volunteerAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun updateVolunteer(id: Int, r: VolunteerRequest) = viewModelScope.launch {
        _loading.value = true
        repo.updateVolunteer(id, r).fold(onSuccess = { res -> if (res.body()?.success == true) { _volunteerAction.value = Result.success("Volunteer updated"); loadVolunteers() } else _volunteerAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _volunteerAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteVolunteer(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteVolunteer(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _volunteerAction.value = Result.success("Volunteer deleted"); loadVolunteers() } else _volunteerAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _volunteerAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadPrograms() = viewModelScope.launch {
        _loading.value = true
        repo.getPrograms().fold(onSuccess = { _programs.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addProgram(r: ProgramRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addProgram(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _programAction.value = Result.success("Program added"); loadPrograms() } else _programAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _programAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun updateProgram(id: Int, r: ProgramRequest) = viewModelScope.launch {
        _loading.value = true
        repo.updateProgram(id, r).fold(onSuccess = { res -> if (res.body()?.success == true) { _programAction.value = Result.success("Program updated"); loadPrograms() } else _programAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _programAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteProgram(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteProgram(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _programAction.value = Result.success("Program deleted"); loadPrograms() } else _programAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _programAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadSessions() = viewModelScope.launch {
        _loading.value = true
        repo.getSessions().fold(onSuccess = { _sessions.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addSession(r: SessionRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addSession(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _sessionAction.value = Result.success("Session added"); loadSessions() } else _sessionAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _sessionAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun updateSession(id: Int, r: SessionRequest) = viewModelScope.launch {
        _loading.value = true
        repo.updateSession(id, r).fold(onSuccess = { res -> if (res.body()?.success == true) { _sessionAction.value = Result.success("Session updated"); loadSessions() } else _sessionAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _sessionAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteSession(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteSession(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _sessionAction.value = Result.success("Session deleted"); loadSessions() } else _sessionAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _sessionAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadEnrolments() = viewModelScope.launch {
        _loading.value = true
        repo.getEnrolments().fold(onSuccess = { _enrolments.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addEnrolment(r: EnrolmentRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addEnrolment(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _enrolmentAction.value = Result.success("Enrolment added"); loadEnrolments() } else _enrolmentAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _enrolmentAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteEnrolment(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteEnrolment(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _enrolmentAction.value = Result.success("Enrolment deleted"); loadEnrolments() } else _enrolmentAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _enrolmentAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadAttendance() = viewModelScope.launch {
        _loading.value = true
        repo.getAttendance().fold(onSuccess = { _attendanceList.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addAttendance(r: AttendanceRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addAttendance(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _attendanceAction.value = Result.success("Attendance recorded"); loadAttendance() } else _attendanceAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _attendanceAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteAttendance(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteAttendance(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _attendanceAction.value = Result.success("Attendance deleted"); loadAttendance() } else _attendanceAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _attendanceAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadPerformance() = viewModelScope.launch {
        _loading.value = true
        repo.getPerformance().fold(onSuccess = { _performanceList.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addPerformance(r: PerformanceRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addPerformance(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _performanceAction.value = Result.success("Performance added"); loadPerformance() } else _performanceAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _performanceAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deletePerformance(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deletePerformance(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _performanceAction.value = Result.success("Performance deleted"); loadPerformance() } else _performanceAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _performanceAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadDonations() = viewModelScope.launch {
        _loading.value = true
        repo.getDonations().fold(onSuccess = { _donations.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun addDonation(r: DonationRequest) = viewModelScope.launch {
        _loading.value = true
        repo.addDonation(r).fold(onSuccess = { res -> if (res.body()?.success == true) { _donationAction.value = Result.success("Donation added"); loadDonations() } else _donationAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _donationAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun updateDonation(id: Int, r: DonationRequest) = viewModelScope.launch {
        _loading.value = true
        repo.updateDonation(id, r).fold(onSuccess = { res -> if (res.body()?.success == true) { _donationAction.value = Result.success("Donation updated"); loadDonations() } else _donationAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _donationAction.value = Result.failure(it) })
        _loading.value = false
    }
    fun deleteDonation(id: Int) = viewModelScope.launch {
        _loading.value = true
        repo.deleteDonation(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _donationAction.value = Result.success("Donation deleted"); loadDonations() } else _donationAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _donationAction.value = Result.failure(it) })
        _loading.value = false
    }

    fun loadAdminUsers() = viewModelScope.launch {
        _loading.value = true
        repo.getAdminUsers().fold(onSuccess = { _adminUsers.value = it.body()?.data ?: emptyList() }, onFailure = { _error.value = it.message })
        _loading.value = false
    }
    fun approveUser(id: Int) = viewModelScope.launch {
        repo.approveUser(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _adminAction.value = Result.success("User approved"); loadAdminUsers() } else _adminAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _adminAction.value = Result.failure(it) })
    }
    fun rejectUser(id: Int) = viewModelScope.launch {
        repo.rejectUser(id).fold(onSuccess = { res -> if (res.body()?.success == true) { _adminAction.value = Result.success("User rejected"); loadAdminUsers() } else _adminAction.value = Result.failure(Exception(res.body()?.message)) }, onFailure = { _adminAction.value = Result.failure(it) })
    }
}

