package com.example.timecountdown

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.timecountdown.databinding.ActivityMainBinding
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private var START_MILLI_SECONDS = 60000L

    private lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val builder = AlertDialog.Builder(this)
        val fabView = layoutInflater.inflate(R.layout.set_timmer, null)
        builder.setView(fabView)

        val alertDialog = builder.create()


//        binding.reset.setOnClickListener {
//            resetTimer()
////        }
        binding.floatingActionButton.setOnClickListener {
            alertDialog.show()
        }
        //initialize set time textView
        val setTime = fabView.findViewById<TextView>(R.id.time_edit_text)

        //set start button on click listener
        fabView.findViewById<Button>(R.id.start_bt).setOnClickListener {

                countdown_timer.cancel()

                time_in_milli_seconds = setTime.text.toString().toLong() * 60000L
                startTimer(time_in_milli_seconds)

            //set color to white
            val color = ContextCompat.getColor(applicationContext, R.color.white)
            binding.timer.setTextColor(color)
            //dismiss dialog
            alertDialog.dismiss()
        }

        //initialize cancel button
        val cancel = fabView.findViewById<ImageView>(R.id.cancel)
        //set cancel button on click listener
        cancel.setOnClickListener {
            //dismiss dialog
            alertDialog.dismiss()
        }
        //increase value when clicked
        var value = 0
        fabView.findViewById<ImageView>(R.id.increment).setOnClickListener {
            value +=1
            setTime.text = value.toString()
        }
        //decrease value when clicked
        fabView.findViewById<ImageView>(R.id.decrement).setOnClickListener {
            if (value > 0 ){
                value -=1
                setTime.text = value.toString()
            }
        }
    }
//    private fun pauseTimer() {
//
//        countdown_timer.cancel()
//        isRunning = false
//    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.timer.text = "TIME'S UP"
                val color = ContextCompat.getColor(applicationContext, R.color.red)
                binding.timer.setTextColor(color)
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

//        isRunning = true
//        binding.button.text = "Pause"
//        binding.reset.visibility = INVISIBLE

    }

    private fun resetTimer() {
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
//        binding.reset.visibility = INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        binding.timer.text = "$minute : $seconds"
        if(seconds < 30){
            val color = ContextCompat.getColor(applicationContext, R.color.red)
            binding.timer.setTextColor(color)
        }
    }
}