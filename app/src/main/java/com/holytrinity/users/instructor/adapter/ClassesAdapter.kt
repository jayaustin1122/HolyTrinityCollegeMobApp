package com.holytrinity.users.instructor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemClassListBinding
import com.holytrinity.model.Class

class ClassesAdapter(private var classesList: List<Class>) : RecyclerView.Adapter<ClassesAdapter.ClassViewHolder>() {

    // ViewHolder class using View Binding
    inner class ClassViewHolder(private val binding: ItemClassListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(classItem: Class) {
            binding.codeTextView.text = classItem.code
            binding.nameTextView.text = classItem.name
            binding.sectionTextView.text = classItem.section
            binding.unitTextView.text = classItem.units.toString()
            binding.scheduleTextView.text = classItem.schedule
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classesList[position])
    }

    override fun getItemCount(): Int = classesList.size

    // Method to update the data
    fun updateData(newClassesList: List<Class>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }
}