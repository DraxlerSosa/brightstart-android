package com.brightstart.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brightstart.app.data.models.Donation
import com.brightstart.app.data.models.Session
import com.brightstart.app.data.models.Student
import com.brightstart.app.databinding.ItemRecentDonationBinding
import com.brightstart.app.databinding.ItemRecentSessionBinding
import com.brightstart.app.databinding.ItemRecentStudentBinding
import com.brightstart.app.utils.toRandFormat

class RecentStudentsAdapter(private val items: List<Student>) :
    RecyclerView.Adapter<RecentStudentsAdapter.VH>() {
    inner class VH(val b: ItemRecentStudentBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(
        ItemRecentStudentBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = items[pos]
        h.b.tvName.text = "${s.firstName} ${s.lastName}"
        h.b.tvArea.text = s.communityArea
        h.b.tvDate.text = s.enrolmentDate
    }
}

class RecentDonationsAdapter(private val items: List<Donation>) :
    RecyclerView.Adapter<RecentDonationsAdapter.VH>() {
    inner class VH(val b: ItemRecentDonationBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(
        ItemRecentDonationBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val d = items[pos]
        h.b.tvDonor.text  = d.donorName
        h.b.tvAmount.text = d.donationAmount.toRandFormat()
        h.b.tvType.text   = d.donationType
    }
}

class UpcomingSessionsAdapter(private val items: List<Session>) :
    RecyclerView.Adapter<UpcomingSessionsAdapter.VH>() {
    inner class VH(val b: ItemRecentSessionBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(
        ItemRecentSessionBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = items[pos]
        h.b.tvProgram.text   = s.programName
        h.b.tvVolunteer.text = s.volunteerName
        h.b.tvDate.text      = s.sessionDate
        h.b.tvVenue.text     = s.venue
    }
}

