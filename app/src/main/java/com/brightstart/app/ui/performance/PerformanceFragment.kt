package com.brightstart.app.ui.performance

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
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

class PerformanceFragment : Fragment() {
    private var _b: FragmentPerformanceBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: PerformanceAdapter
    private var studentsList = listOf<Student>()
    private var programsList = listOf<Program>()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentPerformanceBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PerformanceAdapter(onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog() }
        b.swipeRefresh.setOnRefreshListener { loadAll() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.performanceList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.students.observe(viewLifecycleOwner) { studentsList = it }
        vm.programs.observe(viewLifecycleOwner) { programsList = it }
        vm.performanceAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        loadAll()
    }

    private fun loadAll() { vm.loadPerformance(); vm.loadStudents(); vm.loadPrograms() }

    private fun showDialog() {
        val db = DialogAddPerformanceBinding.inflate(layoutInflater)
        val sa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, studentsList.map { "${it.firstName} ${it.lastName}" })
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStudent.adapter = sa
        val pa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, programsList.map { it.programName })
        pa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerProgram.adapter = pa
        val ga = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("A","B","C","D","E","F"))
        ga.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerGrade.adapter = ga
        db.etDate.isFocusable = false
        db.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d -> db.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        AlertDialog.Builder(requireContext()).setTitle("Add Performance").setView(db.root)
            .setPositiveButton("Save") { _, _ ->
                if (studentsList.isEmpty() || programsList.isEmpty()) { toast("Load data first"); return@setPositiveButton }
                val req = PerformanceRequest(studentsList[db.spinnerStudent.selectedItemPosition].studentId,
                    programsList[db.spinnerProgram.selectedItemPosition].programId,
                    db.etDate.text.toString().trim(), db.etScore.text.toString().toDoubleOrNull() ?: 0.0,
                    db.spinnerGrade.selectedItem.toString(), db.etComments.text.toString().trim())
                vm.addPerformance(req)
            }.setNegativeButton("Cancel", null).show()
    }

    private fun confirmDelete(p: Performance) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Performance").setMessage("Delete record for ${p.studentName}?")
            .setPositiveButton("Delete") { _, _ -> vm.deletePerformance(p.performanceId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class PerformanceAdapter(val onDelete: (Performance) -> Unit) :
    ListAdapter<Performance, PerformanceAdapter.VH>(object : DiffUtil.ItemCallback<Performance>() {
        override fun areItemsTheSame(a: Performance, b: Performance) = a.performanceId == b.performanceId
        override fun areContentsTheSame(a: Performance, b: Performance) = a == b
    }) {
    inner class VH(val b: ItemPerformanceBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemPerformanceBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val perf = getItem(pos)
        h.b.tvStudent.text = perf.studentName.ifEmpty { "Student #${perf.studentId}" }
        h.b.tvProgram.text = perf.programName.ifEmpty { "Program #${perf.programId}" }
        h.b.tvDate.text = perf.assessmentDate; h.b.tvScore.text = "Score: ${perf.score}%"
        h.b.tvGrade.text = perf.grade; h.b.tvComments.text = perf.comments
        h.b.btnDelete.setOnClickListener { onDelete(perf) }
    }
}
