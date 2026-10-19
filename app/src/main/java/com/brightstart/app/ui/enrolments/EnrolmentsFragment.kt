package com.brightstart.app.ui.enrolments

import android.app.DatePickerDialog
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
import java.util.Calendar

class EnrolmentsFragment : Fragment() {
    private var _b: FragmentEnrolmentsBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: EnrolmentsAdapter
    private var studentsList = listOf<Student>()
    private var programsList = listOf<Program>()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentEnrolmentsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = EnrolmentsAdapter(onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog() }
        b.swipeRefresh.setOnRefreshListener { loadAll() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.enrolments.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.students.observe(viewLifecycleOwner) { studentsList = it }
        vm.programs.observe(viewLifecycleOwner) { programsList = it }
        vm.enrolmentAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        loadAll()
    }

    private fun loadAll() { vm.loadEnrolments(); vm.loadStudents(); vm.loadPrograms() }

    private fun showDialog() {
        val db = DialogAddEnrolmentBinding.inflate(layoutInflater)
        val sa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, studentsList.map { "${it.firstName} ${it.lastName}" })
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStudent.adapter = sa
        val pa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, programsList.map { it.programName })
        pa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerProgram.adapter = pa
        val statuses = arrayOf("Active", "Completed", "Withdrawn")
        val sta = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        sta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStatus.adapter = sta
        db.etDate.isFocusable = false
        db.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d -> db.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        AlertDialog.Builder(requireContext()).setTitle("Add Enrolment").setView(db.root)
            .showValidated(
                "Enrol",
                validate = {
                    Validators.firstError(
                        if (studentsList.isEmpty() || programsList.isEmpty()) "Students and programs have not loaded yet" else null,
                        Validators.date(db.etDate.text.toString(), "Enrolment date")
                    )
                }
            ) {
                val req = EnrolmentRequest(studentsList[db.spinnerStudent.selectedItemPosition].studentId,
                    programsList[db.spinnerProgram.selectedItemPosition].programId,
                    db.etDate.text.toString().trim(), db.spinnerStatus.selectedItem.toString())
                vm.addEnrolment(req)
            }
    }

    private fun confirmDelete(e: Enrolment) {
        AlertDialog.Builder(requireContext()).setTitle("Remove Enrolment")
            .setMessage("Remove ${e.studentName} from ${e.programName}?")
            .setPositiveButton("Remove") { _, _ -> vm.deleteEnrolment(e.enrolmentId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class EnrolmentsAdapter(val onDelete: (Enrolment) -> Unit) :
    ListAdapter<Enrolment, EnrolmentsAdapter.VH>(object : DiffUtil.ItemCallback<Enrolment>() {
        override fun areItemsTheSame(a: Enrolment, b: Enrolment) = a.enrolmentId == b.enrolmentId
        override fun areContentsTheSame(a: Enrolment, b: Enrolment) = a == b
    }) {
    inner class VH(val b: ItemEnrolmentBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemEnrolmentBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val e = getItem(pos)
        h.b.tvStudent.text = e.studentName.ifEmpty { "Student #${e.studentId}" }
        h.b.tvProgram.text = e.programName.ifEmpty { "Program #${e.programId}" }
        h.b.tvDate.text = "Enrolled: ${e.enrolmentDate}"; h.b.tvStatus.text = e.status
        h.b.btnDelete.setOnClickListener { onDelete(e) }
    }
}

