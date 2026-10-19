package com.brightstart.app.ui.attendance

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.brightstart.app.utils.Validators
import com.brightstart.app.utils.showValidated
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightstart.app.data.models.*
import com.brightstart.app.databinding.*
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.toast

class AttendanceFragment : Fragment() {
    private var _b: FragmentAttendanceBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: AttendanceAdapter
    private var sessionsList = listOf<Session>()
    private var studentsList = listOf<Student>()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentAttendanceBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AttendanceAdapter(onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog() }
        b.swipeRefresh.setOnRefreshListener { loadAll() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.attendanceList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.sessions.observe(viewLifecycleOwner) { sessionsList = it }
        vm.students.observe(viewLifecycleOwner) { studentsList = it }
        vm.attendanceAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        loadAll()
    }

    private fun loadAll() { vm.loadAttendance(); vm.loadSessions(); vm.loadStudents() }

    private fun showDialog() {
        val db = DialogAddAttendanceBinding.inflate(layoutInflater)
        val sea = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            sessionsList.map { "${it.sessionDate} – ${it.programName.ifEmpty { "#${it.programId}" }}" })
        sea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerSession.adapter = sea
        val sa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, studentsList.map { "${it.firstName} ${it.lastName}" })
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStudent.adapter = sa
        val sta = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("Present", "Absent", "Excused", "Late"))
        sta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStatus.adapter = sta
        AlertDialog.Builder(requireContext()).setTitle("Record Attendance").setView(db.root)
            .showValidated(
                "Save",
                validate = {
                    Validators.firstError(
                        if (sessionsList.isEmpty() || studentsList.isEmpty()) "Sessions and students have not loaded yet" else null,
                        if (db.etNotes.text.toString().length > 255) "Notes must be 255 characters or fewer" else null
                    )
                }
            ) {
                val req = AttendanceRequest(sessionsList[db.spinnerSession.selectedItemPosition].sessionId,
                    studentsList[db.spinnerStudent.selectedItemPosition].studentId,
                    db.spinnerStatus.selectedItem.toString(), db.etNotes.text.toString().trim())
                vm.addAttendance(req)
            }
    }

    private fun confirmDelete(a: Attendance) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Attendance").setMessage("Delete record for ${a.studentName}?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteAttendance(a.attendanceId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class AttendanceAdapter(val onDelete: (Attendance) -> Unit) :
    ListAdapter<Attendance, AttendanceAdapter.VH>(object : DiffUtil.ItemCallback<Attendance>() {
        override fun areItemsTheSame(a: Attendance, b: Attendance) = a.attendanceId == b.attendanceId
        override fun areContentsTheSame(a: Attendance, b: Attendance) = a == b
    }) {
    inner class VH(val b: ItemAttendanceBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemAttendanceBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val a = getItem(pos)
        h.b.tvStudent.text = a.studentName.ifEmpty { "Student #${a.studentId}" }
        h.b.tvProgram.text = a.programName.ifEmpty { "Session #${a.sessionId}" }
        h.b.tvDate.text = a.sessionDate; h.b.tvStatus.text = a.attendanceStatus
        val color = when (a.attendanceStatus) { "Present" -> 0xFF4CAF50.toInt(); "Absent" -> 0xFFF44336.toInt(); else -> 0xFFFF9800.toInt() }
        h.b.tvStatus.setTextColor(color)
        h.b.btnDelete.setOnClickListener { onDelete(a) }
    }
}

