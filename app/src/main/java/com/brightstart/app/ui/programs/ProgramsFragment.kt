package com.brightstart.app.ui.programs

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

class ProgramsFragment : Fragment() {
    private var _b: FragmentProgramsBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: ProgramsAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentProgramsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProgramsAdapter(onEdit = { showDialog(it) }, onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog(null) }
        b.swipeRefresh.setOnRefreshListener { vm.loadPrograms() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.programs.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.programAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        vm.loadPrograms()
    }

    private fun showDialog(prog: Program?) {
        val db = DialogAddProgramBinding.inflate(layoutInflater)
        val statuses = arrayOf("Active", "Pending", "Closed")
        val sa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerStatus.adapter = sa
        prog?.let {
            db.etName.setText(it.programName); db.etDescription.setText(it.description)
            db.etStartDate.setText(it.startDate); db.etEndDate.setText(it.endDate)
            db.spinnerStatus.setSelection(statuses.indexOf(it.status).coerceAtLeast(0))
        }
        listOf(db.etStartDate, db.etEndDate).forEach { et ->
            et.isFocusable = false
            et.setOnClickListener {
                val c = Calendar.getInstance()
                DatePickerDialog(requireContext(), { _, y, m, d -> et.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
        AlertDialog.Builder(requireContext()).setTitle(if (prog == null) "Add Program" else "Edit Program").setView(db.root)
            .showValidated(
                if (prog == null) "Add" else "Update",
                validate = {
                    Validators.firstError(
                        Validators.required(db.etName.text.toString(), "Program name"),
                        Validators.required(db.etDescription.text.toString(), "Description", 500),
                        Validators.dateOrder(db.etStartDate.text.toString(), db.etEndDate.text.toString())
                    )
                }
            ) {
                val req = ProgramRequest(db.etName.text.toString().trim(), db.etDescription.text.toString().trim(),
                    db.etStartDate.text.toString().trim(), db.etEndDate.text.toString().trim(), db.spinnerStatus.selectedItem.toString())
                if (prog == null) vm.addProgram(req) else vm.updateProgram(prog.programId, req)
            }
    }

    private fun confirmDelete(p: Program) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Program").setMessage("Delete ${p.programName}?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteProgram(p.programId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class ProgramsAdapter(val onEdit: (Program) -> Unit, val onDelete: (Program) -> Unit) :
    ListAdapter<Program, ProgramsAdapter.VH>(object : DiffUtil.ItemCallback<Program>() {
        override fun areItemsTheSame(a: Program, b: Program) = a.programId == b.programId
        override fun areContentsTheSame(a: Program, b: Program) = a == b
    }) {
    inner class VH(val b: ItemProgramBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemProgramBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val prog = getItem(pos)
        h.b.tvName.text = prog.programName; h.b.tvDescription.text = prog.description
        h.b.tvDates.text = "${prog.startDate} → ${prog.endDate}"; h.b.tvStatus.text = prog.status
        h.b.btnEdit.setOnClickListener { onEdit(prog) }; h.b.btnDelete.setOnClickListener { onDelete(prog) }
    }
}

