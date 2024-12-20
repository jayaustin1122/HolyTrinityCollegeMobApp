package com.holytrinity.users.registrar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.Courses

class CoursesAdapter(
    private val courses: MutableList<Courses>,
    private val deleteListener: (Courses) -> Unit,
    private val editListener: (Courses) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView) // To be hidden
        val descriptionTitleTextView: TextView = itemView.findViewById(R.id.descriptionTitleTextView)
        val amountTitleTextView: TextView = itemView.findViewById(R.id.amountTitleTextView) // To be hidden
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)
        val editChip: Chip = itemView.findViewById(R.id.editChip)

        init {
            deleteChip.setOnClickListener {
                val course = courses[adapterPosition]
                deleteListener(course)
            }
            editChip.setOnClickListener {
                val course = courses[adapterPosition]
                Log.d("CoursesAdapter", "Editing: $course")
                editListener(course)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discount_fee, parent, false) // Reusing existing layout
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]

        // Set the main title to Course Name
        holder.nameTextView.text = course.name

        // Set the course code
        holder.codeTextView.text = course.code

        // Set the course description
        holder.descriptionTextView.text = course.description

        // Hide the amount fields as they are not needed for Courses
        holder.amountTextView.visibility = View.GONE
        holder.amountTitleTextView.visibility = View.GONE

        // Optionally, you can change the description title if needed
        holder.descriptionTitleTextView.text = "Description"
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    fun deleteItem(course: Courses) {
        val position = courses.indexOf(course)
        if (position != -1) {
            courses.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateCourses(newCourses: List<Courses>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }
}
