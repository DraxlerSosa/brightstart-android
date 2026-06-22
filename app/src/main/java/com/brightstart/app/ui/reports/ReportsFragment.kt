package com.brightstart.app.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brightstart.app.databinding.FragmentReportsBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.toRandFormat

class ReportsFragment : Fragment() {
    private var _b: FragmentReportsBinding? = null
    private val b get() = _b!!
    private val vm: SharedViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentReportsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.swipeRefresh.setOnRefreshListener { loadAll() }
        vm.loading.observe(viewLifecycleOwner) { b.swipeRefresh.isRefreshing = it }
        vm.students.observe(viewLifecycleOwner) { list ->
            b.tvTotalStudents.text = list.size.toString()
            b.tvGenderBreakdown.text = list.groupBy { it.gender }.entries.joinToString("  |  ") { "${it.key}: ${it.value.size}" }
            b.tvStudentsByArea.text = list.groupBy { it.communityArea }.entries.joinToString("\n") { "• ${it.key}: ${it.value.size}" }
        }
        vm.volunteers.observe(viewLifecycleOwner) { list ->
            b.tvTotalVolunteers.text = list.size.toString()
            b.tvActiveVolunteers.text = "Active: ${list.count { it.status == "Active" }} / ${list.size}"
            b.tvVolunteerSpecialities.text = list.groupBy { it.subjectSpeciality }.entries.joinToString("\n") { "• ${it.key}: ${it.value.size}" }
        }
        vm.programs.observe(viewLifecycleOwner) { list ->
            b.tvTotalPrograms.text = list.size.toString()
            b.tvActivePrograms.text = "Active: ${list.count { it.status == "Active" }} / ${list.size}"
        }
        vm.donations.observe(viewLifecycleOwner) { list ->
            b.tvTotalDonations.text = list.size.toString()
            b.tvTotalAmount.text = list.sumOf { it.donationAmount }.toRandFormat()
            b.tvDonationsByType.text = list.groupBy { it.donationType }.entries.joinToString("\n") { "• ${it.key}: ${it.value.sumOf { d -> d.donationAmount }.toRandFormat()}" }
        }
        vm.attendanceList.observe(viewLifecycleOwner) { list ->
            val present = list.count { it.attendanceStatus == "Present" }
            val rate = if (list.isNotEmpty()) (present * 100 / list.size) else 0
            b.tvAttendanceRate.text = "$rate% ($present/${list.size} present)"
        }
        vm.performanceList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                b.tvAvgScore.text = String.format("%.1f%%", list.sumOf { it.score } / list.size)
                b.tvGradeBreakdown.text = list.groupBy { it.grade }.entries.sortedBy { it.key }.joinToString("  ") { "${it.key}: ${it.value.size}" }
            } else { b.tvAvgScore.text = "No data"; b.tvGradeBreakdown.text = "No data" }
        }
        loadAll()
    }

    private fun loadAll() {
        vm.loadStudents(); vm.loadVolunteers(); vm.loadPrograms()
        vm.loadDonations(); vm.loadAttendance(); vm.loadPerformance()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}