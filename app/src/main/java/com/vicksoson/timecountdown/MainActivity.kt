package com.vicksoson.timecountdown

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vicksoson.timecountdown.databinding.ActivityMainBinding
import com.vicksoson.timecountdown.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private var startMilliSeconds = 60000L

    private lateinit var countdownTimer: CountDownTimer

    private var timeInMilliSeconds = 0L
    private lateinit var binding: ActivityMainBinding

    private var mInterstitialAd: InterstitialAd? = null
    private var tag = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //initiate admob
        MobileAds.initialize(this) {}
        //set ads view
        val mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        //set fullscreen callback
        InterstitialAd.load(this, "ca-app-pub-2805616918393635/4878840900",
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(tag, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })


        //alert builder
        val builder = AlertDialog.Builder(this)
        val setTimeView = layoutInflater.inflate(R.layout.set_timmer, null)
        builder.setView(setTimeView)

        val alertDialog = builder.create()


//        binding.reset.setOnClickListener {
//            resetTimer()
////        }0L


        //initialize set time textView
        val setSeconds = setTimeView.findViewById<TextView>(R.id.seconds_text)
        val setMinutes = setTimeView.findViewById<TextView>(R.id.minute_text)

        //Timer controls
        binding.timeControl.setOnClickListener {
            mainViewModel.isRunning.observe(this,{ isRunning ->
                if (isRunning) {
//                pauseTimer()
                    //set current
                    mainViewModel.time.observe(this,{
                        mainViewModel.setCurrentTime(it)
                    })
                    //pause time
                    countdownTimer.cancel()
                    mainViewModel.resetValues()
                    mainViewModel.setRunning(false)

                    binding.timeControl.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    binding.quickCountdown.visibility = VISIBLE
                } else {
//                resumeTimer()
                    //resume timer
                    mainViewModel.currentTime.observe(this,{
                        startTimer(it,this, mainViewModel, this)
                    })
                    binding.timeControl.setImageResource(R.drawable.ic_baseline_pause_24)
                    binding.quickCountdown.visibility = INVISIBLE
                }
            })

        }
        binding.quickCountdown.setOnClickListener {
            mainViewModel.resetValues()
            alertDialog.show()


            // Stop playing sound
//            if (sound.isPlaying) {
//                sound.stop()
//            }
        }

        // enable and disable sound
        mainViewModel.isEnabled.observe(this,{ state ->
             binding.alamFloatingActionButton.setOnClickListener {
            if (state) {
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_off_24)
                mainViewModel.setEnable(false)
            } else {
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_24)
                mainViewModel.setEnable(true)
            }
        }
        })



        //set start button on click listener
        setTimeView.findViewById<Button>(R.id.start_bt).setOnClickListener {
            mainViewModel.isRunning.observe(this,{
                if (it) {
                    countdownTimer.cancel()
                    mainViewModel.setRunning(false)
                    mainViewModel.updateTime()
                    mainViewModel.time.observe(this, { time ->
                        startTimer(time, this, mainViewModel, this)
                    })
                } else {
                    mainViewModel.updateTime()
                    resetTimer()
                    mainViewModel.time.observe(this, { time ->
                        startTimer(time, this, mainViewModel, this)
                    })

                }
            })

            //set color to white
            val color = ContextCompat.getColor(applicationContext, R.color.white)
            binding.timer.setTextColor(color)
            //make quickCountdown button invisible at start time
            binding.quickCountdown.visibility = INVISIBLE
            binding.timeControl.setImageResource(R.drawable.ic_baseline_pause_24)
            //dismiss dialog
            alertDialog.dismiss()
        }

        //initialize cancel button
        val cancel = setTimeView.findViewById<ImageView>(R.id.cancel)
        //set cancel button on click listener
        cancel.setOnClickListener {
            //dismiss dialog
            alertDialog.dismiss()
        }
        //increase seconds on click
        setTimeView.findViewById<ImageView>(R.id.s_increment).setOnClickListener {
            mainViewModel.sIncrement()
            mainViewModel.seconds.observe(this, {
                setSeconds.text = it.toString()
            })

        }
        //decrease seconds when clicked
        setTimeView.findViewById<ImageView>(R.id.s_decrement).setOnClickListener {
            mainViewModel.sDecrement()
            mainViewModel.seconds.observe(this, {
                setSeconds.text = it.toString()
            })
        }

        //increase minute on click
        setTimeView.findViewById<ImageView>(R.id.m_increment).setOnClickListener {
            mainViewModel.mIncrement()
            mainViewModel.minutes.observe(this, {
                setMinutes.text = it.toString()
            })

        }
        //decrease minutes when clicked
        setTimeView.findViewById<ImageView>(R.id.m_decrement).setOnClickListener {
            mainViewModel.mDecrement()
            mainViewModel.minutes.observe(this, {
                setMinutes.text = it.toString()
            })
        }






    }

//    private fun pauseTimer() {
//
//        currentTime = timeInMilliSeconds
//        countdownTimer.cancel()
//        isRunning = false
//
//    }

//    private fun resumeTimer() {
//        startTimer(currentTime, this)
//    }

    // Get the device default ringtone
//    private val sound = MediaPlayer.create(this, R.raw.ding_sound )
    private fun startTimer(time_in_seconds: Long, activity: Activity, viewModel: MainViewModel, owner: LifecycleOwner) {
        countdownTimer = object : CountDownTimer(time_in_seconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.timer.textSize = 100F
                binding.timer.text = "TIME'S UP"
                binding.timeControl.visibility = GONE
                binding.quickCountdown.visibility = VISIBLE
                val color = ContextCompat.getColor(applicationContext, R.color.red)
                binding.timer.setTextColor(color)

                // Play the default ringtone
//                if (!sound.isPlaying) {
//                    sound.start()
//                }
                val sound = MediaPlayer.create(applicationContext, R.raw.ding_sound)
                viewModel.isEnabled.observe(owner,{
                    if (it) {
                        sound.start()
                    } else {
                        sound.stop()
                    }
                })



                //show ads
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }

            override fun onTick(p0: Long) {
                timeInMilliSeconds = p0
                updateTextUI()


            }
        }
        binding.timer.textSize = 120F

        countdownTimer.start()
        viewModel.setRunning(true)
//        binding.button.text = "Pause"
//        binding.reset.visibility = INVISIBLE
        binding.timeControl.visibility = VISIBLE

    }

    private fun resetTimer() {
        timeInMilliSeconds = startMilliSeconds
        updateTextUI()
//        binding.reset.visibility = INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun updateTextUI() {
        val minute = (timeInMilliSeconds / 1000) / 60
        val seconds = (timeInMilliSeconds / 1000) % 60



        binding.timer.text = "$minute : $seconds"
        if (seconds < 30 && minute < 1) {
            val color = ContextCompat.getColor(applicationContext, R.color.red)
//            val warning = MediaPlayer.create(this, R.raw.alarm_clock_beep)
            binding.timer.setTextColor(color)
//            if(seconds > 0){
//                warning.start()
//            }
//            if(seconds < 1){
//                warning.stop()
//            }
        }


    }
}
