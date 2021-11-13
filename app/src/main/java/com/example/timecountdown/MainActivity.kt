package com.example.timecountdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.timecountdown.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var START_MILLI_SECONDS = 60000L

    lateinit var countdown_timer: CountDownTimer
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
//        }
        binding.floatingActionButton.setOnClickListener {
            alertDialog.show()
        }
        findViewById<Button>(R.id.start_bt).setOnClickListener {
            if (isRunning) {
                countdown_timer.cancel()
                isRunning = false
            } else {
                val time  = findViewById<TextView>(R.id.time_edit_text).text.toString()
                time_in_milli_seconds = time.toLong() *60000L
                startTimer(time_in_milli_seconds)
            }
            alertDialog.dismiss()
        }
    }
//    private fun pauseTimer() {
//
//        countdown_timer.cancel()
//        isRunning = false
//    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

        isRunning = true
//        binding.button.text = "Pause"
//        binding.reset.visibility = INVISIBLE

    }

    private fun resetTimer() {
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
//        binding.reset.visibility = INVISIBLE
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        binding.timer.text = "$minute:$seconds"
    }
}