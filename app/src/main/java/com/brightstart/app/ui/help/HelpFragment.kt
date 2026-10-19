package com.brightstart.app.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brightstart.app.databinding.FragmentHelpBinding
import com.brightstart.app.databinding.ItemFaqBinding

/** In-app help: expandable answers to the questions people ask most. */
class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentHelpBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FAQS.forEach { (question, answer) ->
            val item = ItemFaqBinding.inflate(layoutInflater, binding.faqContainer, false)
            item.tvQuestion.text = "＋  $question"
            item.tvAnswer.text = answer
            item.root.setOnClickListener {
                val open = item.tvAnswer.visibility == View.VISIBLE
                item.tvAnswer.visibility = if (open) View.GONE else View.VISIBLE
                item.tvQuestion.text = (if (open) "＋  " else "－  ") + question
            }
            binding.faqContainer.addView(item.root)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        val FAQS = listOf(
            "How do I create an account?" to
                "Tap Register on the home screen, fill in your name, email, a username and a password (at least 8 characters with a letter and a number) and choose your role. A BrightStart administrator must approve your account before you can log in.",
            "How do I log in?" to
                "Use your email and password, or tap Continue with Google to sign in with a Google account. If your account is still waiting for approval you will see a message saying so.",
            "Why can't I log in?" to
                "Check your email and password, and make sure you are online. Accounts created with Register can only log in after an administrator approves them. If you forgot your password, ask an administrator for help.",
            "How do I add, edit or delete a record?" to
                "Open the section from the Dashboard (Students, Volunteers, Programs and so on). Tap + to add a record, the pencil to edit it, or the bin to delete it. Forms check your input and tell you what to fix before anything is saved.",
            "How do I record attendance?" to
                "Dashboard, then Attendance, then +. Pick the session and the student, choose Present, Absent, Excused or Late, add a note if needed and tap Save.",
            "How do I record a student's performance?" to
                "Dashboard, then Performance, then +. Choose the student and program, pick the assessment date, enter a score from 0 to 100, choose the grade and add comments.",
            "What can each role do?" to
                "Everyone who is signed in can view and add records. Only Admins and Coordinators can delete records. Only Admins can approve new users and change roles.",
            "How do I change my name, username or password?" to
                "Dashboard, then Settings. Update your profile and tap Save profile, or fill in the Change password section. Changing your password signs you out of other devices.",
            "The list is empty or data won't load." to
                "Pull down on a list to refresh it. Make sure your phone has an internet connection. If you are told your session has expired, log in again.",
            "How do I log out?" to
                "Tap Log out on the Dashboard or in Settings. This also ends your session on the server."
        )
    }
}
