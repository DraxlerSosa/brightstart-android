package com.brightstart.app.ui.students

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightstart.app.data.models.Student
import com.brightstart.app.databinding.ItemStudentBinding

class StudentsAdapter(
    private val onEdit: (Student) -> Unit,
    private val onDelete: (Student) -> Unit
) : ListAdapter<Student, StudentsAdapter.VH>(DIFF) {
    inner class VH(val b: ItemStudentBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(
        ItemStudentBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = getItem(pos)
        h.b.tvName.text    = "${s.firstName} ${s.lastName}"
        h.b.tvArea.text    = "${s.communityArea}"
        h.b.tvGender.text  = s.gender
        h.b.tvContact.text = "${s.contactNumber}"
        h.b.tvDob.text     = "DOB: ${s.dateOfBirth}"
        h.b.tvEnrolmentDate.text = "Enrolled: ${s.enrolmentDate}"
        h.b.tvInitials.text = "${s.firstName.firstOrNull() ?: ""}${s.lastName.firstOrNull() ?: ""}"
        h.b.btnEdit.setOnClickListener { onEdit(s) }
        h.b.btnDelete.setOnClickListener { onDelete(s) }
    }
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(a: Student, b: Student) = a.studentId == b.studentId
            override fun areContentsTheSame(a: Student, b: Student) = a == b
        }
    }
}

