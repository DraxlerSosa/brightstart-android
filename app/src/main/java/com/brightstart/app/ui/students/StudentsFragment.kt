package com.brightstart.app.ui.students

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.brightstart.app.utils.Validators
import com.brightstart.app.utils.showValidated
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brightstart.app.data.models.Student
import com.brightstart.app.data.models.StudentRequest
import com.brightstart.app.databinding.DialogAddStudentBinding
import com.brightstart.app.databinding.FragmentStudentsBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.toast
import java.util.Calendar

class StudentsFragment : Fragment() {
    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: StudentsAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentStudentsBinding.inflate(i, c, false); return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StudentsAdapter(onEdit = { showDialog(it) }, onDelete = { confirmDelete(it) })
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener { showDialog(null) }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean { viewModel.loadStudents(q ?: ""); return true }
        })
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadStudents() }
        viewModel.loading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
        viewModel.students.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.studentAction.observe(viewLifecycleOwner) {
            it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") })
        }
        viewModel.loadStudents()
    }

    private fun showDialog(student: Student?) {
        val db = DialogAddStudentBinding.inflate(layoutInflater)
        val genderAdapter = ArrayAdapter.createFromResource(requireContext(), com.brightstart.app.R.array.genders, android.R.layout.simple_spinner_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        db.spinnerGender.adapter = genderAdapter
        student?.let {
            db.etFirstName.setText(it.firstName); db.etLastName.setText(it.lastName)
            db.etDob.setText(it.dateOfBirth); db.etCommunity.setText(it.communityArea)
            db.etContact.setText(it.contactNumber); db.etEnrolmentDate.setText(it.enrolmentDate)
            val genders = resources.getStringArray(com.brightstart.app.R.array.genders)
            val idx = genders.indexOf(it.gender); if (idx >= 0) db.spinnerGender.setSelection(idx)
        }
        listOf(db.etDob, db.etEnrolmentDate).forEach { et ->
            et.isFocusable = false
            et.setOnClickListener {
                val c = Calendar.getInstance()
                DatePickerDialog(requireContext(), { _, y, m, d -> et.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
        AlertDialog.Builder(requireContext())
            .setTitle(if (student == null) "Add Student" else "Edit Student")
            .setView(db.root)
            .showValidated(
                if (student == null) "Add" else "Update",
                validate = {
                    Validators.firstError(
                        Validators.required(db.etFirstName.text.toString(), "First name", 60),
                        Validators.required(db.etLastName.text.toString(), "Last name", 60),
                        Validators.pastDate(db.etDob.text.toString(), "Date of birth"),
                        Validators.required(db.etCommunity.text.toString(), "Community area"),
                        Validators.phone(db.etContact.text.toString()),
                        Validators.date(db.etEnrolmentDate.text.toString(), "Enrolment date")
                    )
                }
            ) {
                val req = StudentRequest(db.etFirstName.text.toString().trim(), db.etLastName.text.toString().trim(),
                    db.etDob.text.toString().trim(), db.spinnerGender.selectedItem.toString(),
                    db.etCommunity.text.toString().trim(), db.etContact.text.toString().trim(),
                    db.etEnrolmentDate.text.toString().trim())
                if (student == null) viewModel.addStudent(req) else viewModel.updateStudent(student.studentId, req)
            }
    }

    private fun confirmDelete(s: Student) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Student")
            .setMessage("Delete ${s.fullName}?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteStudent(s.studentId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
