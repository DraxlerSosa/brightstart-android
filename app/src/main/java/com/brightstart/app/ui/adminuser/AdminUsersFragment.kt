package com.brightstart.app.ui.adminusers

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightstart.app.data.models.AdminUser
import com.brightstart.app.databinding.FragmentAdminusersBinding
import com.brightstart.app.databinding.ItemAdminUserBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.toast

class AdminUsersFragment : Fragment() {
    private var _b: FragmentAdminusersBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()
    private lateinit var adapter: AdminUsersAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentAdminusersBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdminUsersAdapter(
            onApprove = { u -> AlertDialog.Builder(requireContext()).setTitle("Approve User").setMessage("Approve ${u.fullName}?")
                .setPositiveButton("Approve") { _, _ -> vm.approveUser(u.adminId) }.setNegativeButton("Cancel", null).show() },
            onReject  = { u -> AlertDialog.Builder(requireContext()).setTitle("Reject User").setMessage("Reject ${u.fullName}?")
                .setPositiveButton("Reject")  { _, _ -> vm.rejectUser(u.adminId)  }.setNegativeButton("Cancel", null).show() }
        )
        b.recyclerView.adapter = adapter
        b.swipeRefresh.setOnRefreshListener { vm.loadAdminUsers() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.adminUsers.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.adminAction.observe(viewLifecycleOwner) { it.fold({ msg -> toast(msg) }, { e -> toast(e.message ?: "Error") }) }
        vm.loadAdminUsers()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class AdminUsersAdapter(val onApprove: (AdminUser) -> Unit, val onReject: (AdminUser) -> Unit) :
    ListAdapter<AdminUser, AdminUsersAdapter.VH>(object : DiffUtil.ItemCallback<AdminUser>() {
        override fun areItemsTheSame(a: AdminUser, b: AdminUser) = a.adminId == b.adminId
        override fun areContentsTheSame(a: AdminUser, b: AdminUser) = a == b
    }) {
    inner class VH(val b: ItemAdminUserBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemAdminUserBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val u = getItem(pos)
        h.b.tvName.text = u.fullName; h.b.tvEmail.text = u.email
        h.b.tvUsername.text = "@${u.username}"; h.b.tvRole.text = u.role
        h.b.tvVerified.text = if (u.verified == 1) "✅ Verified" else "⏳ Pending"
        h.b.tvVerified.setTextColor(if (u.verified == 1) 0xFF4CAF50.toInt() else 0xFFFF9800.toInt())
        h.b.tvCreated.text = "Joined: ${u.createdAt}"
        h.b.btnApprove.visibility = if (u.verified == 0) View.VISIBLE else View.GONE
        h.b.btnReject.visibility  = if (u.verified == 0) View.VISIBLE else View.GONE
        h.b.btnApprove.setOnClickListener { onApprove(u) }
        h.b.btnReject.setOnClickListener  { onReject(u)  }
    }
}