package com.vicksoson.timecountdown.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vicksoson.timecountdown.databinding.ScheduleItemBinding
import com.vicksoson.timecountdown.models.ScheduleItems

class ScheduleAdapter: RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private val schedule = mutableListOf<ScheduleItems>()

    inner class ViewHolder( private val binding: ScheduleItemBinding): RecyclerView.ViewHolder(binding.root){

        // function to bind items to views
        @SuppressLint("SetTextI18n")
        fun bindItems(schedules: ScheduleItems){
            val minute = (schedules.taskTime / 1000) / 60
            val seconds = (schedules.taskTime / 1000) % 60

            binding.serial.text = schedules.serial+"."
            binding.taskName.text = schedules.taskName
            binding.taskTime.text = "${minute}m : ${seconds}s"
        }
    }

    fun setUpSchedules(schedule: List<ScheduleItems>){
        this.schedule.addAll(schedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ScheduleItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedule[position]
        schedule.serial = position.plus(1).toString()
        holder.bindItems(schedule)
    }

    override fun getItemCount(): Int {
        return schedule.size
    }
}