package com.brightstart.app.ui.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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

class SessionsFragment : Fragment() {
    private var _b: FragmentSessionsBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: SessionsAdapter
    private var programsList = listOf<Program>()
    private var volunteersList = listOf<Volunteer>()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSessionsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SessionsAdapter(onEdit = { showDialog(it) }, onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog(null) }
        b.swipeRefresh.setOnRefreshListener { loadAll() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.sessions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.programs.observe(viewLifecycleOwner) { programsList = it }
        vm.volunteers.observe(viewLifecycleOwner) { volunteersList = it }
        vm.sessionAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        loadAll()
    }

    private fun loadAll() { vm.loadSessions(); vm.loadPrograms(); vm.loadVolunteers() }

    private fun showDialog(session: Session?) {
        val db = DialogAddSessionBinding.inflate(layoutInflater)
        val pa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, programsList.map { it.programName })
        pa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerProgram.adapter = pa
        val va = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, volunteersList.map { it.fullName })
        va.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerVolunteer.adapter = va
        session?.let {
            db.etDate.setText(it.sessionDate); db.etVenue.setText(it.venue)
            db.etStartTime.setText(it.startTime); db.etEndTime.setText(it.endTime)
            val pi = programsList.indexOfFirst { p -> p.programId == it.programId }; if (pi >= 0) db.spinnerProgram.setSelection(pi)
            val vi = volunteersList.indexOfFirst { v -> v.volunteerId == it.volunteerId }; if (vi >= 0) db.spinnerVolunteer.setSelection(vi)
        }
        db.etDate.isFocusable = false
        db.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d -> db.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        listOf(db.etStartTime, db.etEndTime).forEach { et ->
            et.isFocusable = false
            et.setOnClickListener {
                val c = Calendar.getInstance()
                TimePickerDialog(requireContext(), { _, h, min -> et.setText(String.format("%02d:%02d", h, min)) },
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
            }
        }
        AlertDialog.Builder(requireContext()).setTitle(if (session == null) "Add Session" else "Edit Session").setView(db.root)
            .setPositiveButton(if (session == null) "Add" else "Update") { _, _ ->
                if (programsList.isEmpty() || volunteersList.isEmpty()) { toast("Load programs and volunteers first"); return@setPositiveButton }
                val req = SessionRequest(programsList[db.spinnerProgram.selectedItemPosition].programId,
                    volunteersList[db.spinnerVolunteer.selectedItemPosition].volunteerId,
                    db.etDate.text.toString().trim(), db.etStartTime.text.toString().trim(),
                    db.etEndTime.text.toString().trim(), db.etVenue.text.toString().trim())
                if (session == null) vm.addSession(req) else vm.updateSession(session.sessionId, req)
            }.setNegativeButton("Cancel", null).show()
    }

    private fun confirmDelete(s: Session) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Session").setMessage("Delete session on ${s.sessionDate}?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteSession(s.sessionId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class SessionsAdapter(val onEdit: (Session) -> Unit, val onDelete: (Session) -> Unit) :
    ListAdapter<Session, SessionsAdapter.VH>(object : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(a: Session, b: Session) = a.sessionId == b.sessionId
        override fun areContentsTheSame(a: Session, b: Session) = a == b
    }) {
    inner class VH(val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemSessionBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = getItem(pos)
        h.b.tvProgram.text = s.programName.ifEmpty { "Program #${s.programId}" }
        h.b.tvVolunteer.text = s.volunteerName.ifEmpty { "Volunteer #${s.volunteerId}" }
        h.b.tvDate.text = s.sessionDate; h.b.tvTime.text = "${s.startTime} – ${s.endTime}"
        h.b.tvVenue.text = "📍 ${s.venue}"
        h.b.btnEdit.setOnClickListener { onEdit(s) }; h.b.btnDelete.setOnClickListener { onDelete(s) }
    }
}

