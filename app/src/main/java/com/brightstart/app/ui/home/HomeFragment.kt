package com.brightstart.app.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.brightstart.app.R
import com.brightstart.app.databinding.FragmentHomeBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private val slideHandler = Handler(Looper.getMainLooper())
    private val slideRunnable = Runnable { advanceSlide() }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        setupSlider()
        setupButtons()
        viewModel.loadDashboard()
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            stats?.let {
                binding.tvStudentCount.text  = it.students.toString()
                binding.tvVolunteerCount.text = it.volunteers.toString()
                binding.tvProgramCount.text  = it.programs.toString()
            }
        }
    }

    private fun setupSlider() {
        val slides = listOf(
            HeroSlide(R.drawable.hero_slide_1, "Empowering Every Learner",
                "Supporting disadvantaged communities across South Africa."),
            HeroSlide(R.drawable.hero_slide_2, "Dedicated Volunteers",
                "Passionate volunteers teaching maths, science and life skills."),
            HeroSlide(R.drawable.hero_slide_3, "Learning for the Future",
                "Coding and technology clubs that prepare students for tomorrow.")
        )
        val adapter = HeroSliderAdapter(slides)
        binding.heroPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.heroPager)
        binding.heroPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { resetTimer() }
        })
        binding.btnPrev.setOnClickListener {
            val cur = binding.heroPager.currentItem
            binding.heroPager.currentItem = if (cur > 0) cur - 1 else slides.size - 1
        }
        binding.btnNext.setOnClickListener { advanceSlide() }
        startTimer()
    }

    private fun startTimer()  { slideHandler.postDelayed(slideRunnable, 4000L) }
    private fun resetTimer()  { slideHandler.removeCallbacks(slideRunnable); startTimer() }
    private fun advanceSlide() {
        val count = binding.heroPager.adapter?.itemCount ?: return
        binding.heroPager.currentItem = (binding.heroPager.currentItem + 1) % count
        resetTimer()
    }

    private fun setupButtons() {
        lifecycleScope.launch {
            if (sessionManager.isLoggedIn.first()) {
                binding.btnLogin.text = "Dashboard"
                binding.btnRegister.visibility = View.GONE
                binding.btnLogin.setOnClickListener { findNavController().navigate(R.id.dashboardFragment) }
            } else {
                binding.btnLogin.setOnClickListener { findNavController().navigate(R.id.loginFragment) }
                binding.btnRegister.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
            }
        }
        binding.btnLearnMore.setOnClickListener {
            binding.scrollView.post { binding.scrollView.smoothScrollTo(0, binding.aboutSection.top) }
        }
        binding.btnDonateNow.setOnClickListener {
            binding.scrollView.post { binding.scrollView.smoothScrollTo(0, binding.aboutSection.top) }
        }
    }

    override fun onResume()  { super.onResume(); startTimer() }
    override fun onPause()   { super.onPause(); slideHandler.removeCallbacks(slideRunnable) }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

