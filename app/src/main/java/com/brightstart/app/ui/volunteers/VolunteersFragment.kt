package com.brightstart.app.ui.volunteers

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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

class VolunteersFragment : Fragment() {
    private var _b: FragmentVolunteersBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: VolunteersAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentVolunteersBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = VolunteersAdapter(onEdit = { showDialog(it) }, onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog(null) }
        b.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean { vm.loadVolunteers(q ?: ""); return true }
        })
        b.swipeRefresh.setOnRefreshListener { vm.loadVolunteers() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.volunteers.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.volunteerAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        vm.loadVolunteers()
    }

    private fun showDialog(vol: Volunteer?) {
        val db = DialogAddVolunteerBinding.inflate(layoutInflater)
        val statuses = arrayOf("Active", "Inactive")
        val sa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        db.spinnerStatus.adapter = sa
        vol?.let {
            db.etFullName.setText(it.fullName); db.etEmail.setText(it.contactEmail)
            db.etPhone.setText(it.contactNumber); db.etSpeciality.setText(it.subjectSpeciality)
            db.etJoinDate.setText(it.joinDate)
            db.spinnerStatus.setSelection(statuses.indexOf(it.status).coerceAtLeast(0))
        }
        db.etJoinDate.isFocusable = false
        db.etJoinDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                db.etJoinDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        AlertDialog.Builder(requireContext())
            .setTitle(if (vol == null) "Add Volunteer" else "Edit Volunteer").setView(db.root)
            .setPositiveButton(if (vol == null) "Add" else "Update") { _, _ ->
                val req = VolunteerRequest(db.etFullName.text.toString().trim(), db.etEmail.text.toString().trim(),
                    db.etPhone.text.toString().trim(), db.etSpeciality.text.toString().trim(),
                    db.etJoinDate.text.toString().trim(), db.spinnerStatus.selectedItem.toString())
                if (vol == null) vm.addVolunteer(req) else vm.updateVolunteer(vol.volunteerId, req)
            }.setNegativeButton("Cancel", null).show()
    }

    private fun confirmDelete(v: Volunteer) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Volunteer").setMessage("Delete ${v.fullName}?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteVolunteer(v.volunteerId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class VolunteersAdapter(val onEdit: (Volunteer) -> Unit, val onDelete: (Volunteer) -> Unit) :
    ListAdapter<Volunteer, VolunteersAdapter.VH>(object : DiffUtil.ItemCallback<Volunteer>() {
        override fun areItemsTheSame(a: Volunteer, b: Volunteer) = a.volunteerId == b.volunteerId
        override fun areContentsTheSame(a: Volunteer, b: Volunteer) = a == b
    }) {
    inner class VH(val b: ItemVolunteerBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemVolunteerBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val v = getItem(pos)
        h.b.tvName.text = v.fullName; h.b.tvEmail.text = v.contactEmail
        h.b.tvPhone.text = v.contactNumber; h.b.tvSpeciality.text = v.subjectSpeciality
        h.b.tvStatus.text = v.status; h.b.tvJoinDate.text = "Joined: ${v.joinDate}"
        h.b.tvInitials.text = v.fullName.split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }
        h.b.btnEdit.setOnClickListener { onEdit(v) }; h.b.btnDelete.setOnClickListener { onDelete(v) }
    }
}

