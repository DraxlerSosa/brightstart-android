package com.brightstart.app.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brightstart.app.R
import com.brightstart.app.data.repository.BrightStartRepository
import com.brightstart.app.databinding.FragmentDashboardBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.SessionManager
import com.brightstart.app.utils.toRandFormat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            if (!sessionManager.isLoggedIn.first()) {
                findNavController().navigate(R.id.loginFragment); return@launch
            }
            binding.tvWelcome.text = "Welcome back, ${sessionManager.userName.first()} "
            // The server enforces roles; hide the admin tools from everyone else
            binding.cardAdminUsers.visibility =
                if (sessionManager.userRole.first() == "Admin") View.VISIBLE else View.GONE
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadDashboard() }
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                BrightStartRepository().logout()   // invalidates the token on the server
                sessionManager.clearSession()
                findNavController().navigate(R.id.homeFragment)
            }
        }

        binding.cardStudents.setOnClickListener    { findNavController().navigate(R.id.studentsFragment) }
        binding.cardVolunteers.setOnClickListener  { findNavController().navigate(R.id.volunteersFragment) }
        binding.cardPrograms.setOnClickListener    { findNavController().navigate(R.id.programsFragment) }
        binding.cardSessions.setOnClickListener    { findNavController().navigate(R.id.sessionsFragment) }
        binding.cardEnrolments.setOnClickListener  { findNavController().navigate(R.id.enrolmentsFragment) }
        binding.cardDonations.setOnClickListener   { findNavController().navigate(R.id.donationsFragment) }
        binding.cardAttendance.setOnClickListener  { findNavController().navigate(R.id.attendanceFragment) }
        binding.cardPerformance.setOnClickListener { findNavController().navigate(R.id.performanceFragment) }
        binding.cardReports.setOnClickListener     { findNavController().navigate(R.id.reportsFragment) }
        binding.cardAdminUsers.setOnClickListener  { findNavController().navigate(R.id.adminUsersFragment) }
        binding.cardSettings.setOnClickListener    { findNavController().navigate(R.id.settingsFragment) }
        binding.cardHelp.setOnClickListener        { findNavController().navigate(R.id.helpFragment) }

        viewModel.loading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            stats ?: return@observe
            binding.tvStudentCount.text   = stats.students.toString()
            binding.tvVolunteerCount.text = stats.volunteers.toString()
            binding.tvProgramCount.text   = stats.programs.toString()
            binding.tvSessionCount.text   = stats.sessions.toString()
            binding.tvEnrolmentCount.text = stats.enrolments.toString()
            binding.tvDonationCount.text  = stats.donations.toString()
            binding.tvTotalDonations.text = stats.totalDonationsAmount.toRandFormat()
            setupChart(stats.students, stats.volunteers, stats.programs, stats.sessions, stats.enrolments, stats.donations)
            binding.rvRecentStudents.adapter  = RecentStudentsAdapter(stats.recentStudents ?: emptyList())
            binding.rvRecentDonations.adapter = RecentDonationsAdapter(stats.recentDonations ?: emptyList())
            binding.rvUpcomingSessions.adapter = UpcomingSessionsAdapter(stats.upcomingSessions ?: emptyList())
        }
        viewModel.loadDashboard()
    }

    private fun setupChart(s: Int, v: Int, p: Int, se: Int, e: Int, d: Int) {
        val lime = Color.parseColor("#b8e04a")
        val entries = listOf(BarEntry(0f,s.toFloat()),BarEntry(1f,v.toFloat()),BarEntry(2f,p.toFloat()),BarEntry(3f,se.toFloat()),BarEntry(4f,e.toFloat()),BarEntry(5f,d.toFloat()))
        val ds = BarDataSet(entries, "Overview").apply { colors = List(6){lime}; valueTextColor = Color.WHITE; valueTextSize = 10f }
        binding.barChart.apply {
            data = BarData(ds)
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Students","Volunteers","Programs","Sessions","Enrolments","Donations"))
            xAxis.textColor = Color.WHITE; axisLeft.textColor = Color.WHITE; axisRight.isEnabled = false
            legend.textColor = Color.WHITE; description.isEnabled = false
            setBackgroundColor(Color.TRANSPARENT); animateY(800); invalidate()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

