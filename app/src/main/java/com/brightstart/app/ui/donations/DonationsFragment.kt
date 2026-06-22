package com.brightstart.app.ui.donations

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
import com.brightstart.app.utils.toRandFormat
import java.util.Calendar

class DonationsFragment : Fragment() {
    private var _b: FragmentDonationsBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: DonationsAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDonationsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DonationsAdapter(onEdit = { showDialog(it) }, onDelete = { confirmDelete(it) })
        b.recyclerView.adapter = adapter
        b.fab.setOnClickListener { showDialog(null) }
        b.swipeRefresh.setOnRefreshListener { vm.loadDonations() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.donations.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            b.tvTotal.text = "Total: ${list.sumOf { it.donationAmount }.toRandFormat()}"
        }
        vm.donationAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        vm.loadDonations()
    }

    private fun showDialog(don: Donation?) {
        val db = DialogAddDonationBinding.inflate(layoutInflater)
        val types = arrayOf("Bank Transfer", "Cash", "In-Kind", "Cheque", "Online")
        val ta = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        ta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); db.spinnerType.adapter = ta
        don?.let {
            db.etDonorName.setText(it.donorName); db.etDonorContact.setText(it.donorContact)
            db.etAmount.setText(it.donationAmount.toString()); db.etDate.setText(it.donationDate)
            db.etNotes.setText(it.notes)
            db.spinnerType.setSelection(types.indexOf(it.donationType).coerceAtLeast(0))
        }
        db.etDate.isFocusable = false
        db.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d -> db.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        AlertDialog.Builder(requireContext()).setTitle(if (don == null) "Add Donation" else "Edit Donation").setView(db.root)
            .setPositiveButton(if (don == null) "Add" else "Update") { _, _ ->
                val req = DonationRequest(db.etDonorName.text.toString().trim(), db.etDonorContact.text.toString().trim(),
                    db.etAmount.text.toString().toDoubleOrNull() ?: 0.0, db.etDate.text.toString().trim(),
                    db.spinnerType.selectedItem.toString(), db.etNotes.text.toString().trim())
                if (don == null) vm.addDonation(req) else vm.updateDonation(don.donationId, req)
            }.setNegativeButton("Cancel", null).show()
    }

    private fun confirmDelete(d: Donation) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Donation").setMessage("Delete donation from ${d.donorName}?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteDonation(d.donationId) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class DonationsAdapter(val onEdit: (Donation) -> Unit, val onDelete: (Donation) -> Unit) :
    ListAdapter<Donation, DonationsAdapter.VH>(object : DiffUtil.ItemCallback<Donation>() {
        override fun areItemsTheSame(a: Donation, b: Donation) = a.donationId == b.donationId
        override fun areContentsTheSame(a: Donation, b: Donation) = a == b
    }) {
    inner class VH(val b: ItemDonationBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemDonationBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val d = getItem(pos)
        h.b.tvDonor.text = d.donorName; h.b.tvContact.text = d.donorContact
        h.b.tvAmount.text = d.donationAmount.toRandFormat(); h.b.tvType.text = d.donationType
        h.b.tvDate.text = d.donationDate; h.b.tvNotes.text = d.notes
        h.b.btnEdit.setOnClickListener { onEdit(d) }; h.b.btnDelete.setOnClickListener { onDelete(d) }
    }
}