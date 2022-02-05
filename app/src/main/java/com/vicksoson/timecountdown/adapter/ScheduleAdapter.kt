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

        val task = binding.scheduleIt
    }

    fun setUpSchedules(schedules: List<ScheduleItems>){
        if(schedule.isEmpty()){
            this.schedule.addAll(schedules)
        }else if (schedule.size < schedules.size){
            this.schedule.add(schedules.last())
        }
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

        holder.task.setOnClickListener {
            post(position)
            onItemClickListener?.let { it(schedule) }
        }
    }

    private var onItemClickListener: ((ScheduleItems) -> Unit)? = null
    fun setOnItemClickListener(listener: (ScheduleItems) -> Unit) {
        onItemClickListener = listener
    }
    fun post (position: Int) {
        serial = position
    }

    var serial: Int = 0


    override fun getItemCount(): Int {
        return schedule.size
    }
}