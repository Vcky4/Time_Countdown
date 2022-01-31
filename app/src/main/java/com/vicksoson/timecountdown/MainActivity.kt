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

    private lateinit var binding: ActivityMainBinding

    private var mInterstitialAd: InterstitialAd? = null
    private var tag = "MainActivity"


    @SuppressLint("SetTextI18n")
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


        //initialize set time textView
        val setSeconds = setTimeView.findViewById<TextView>(R.id.seconds_text)
        val setMinutes = setTimeView.findViewById<TextView>(R.id.minute_text)

        binding.quickCountdown.setOnClickListener {
            mainViewModel.resetValues()
            alertDialog.show()

        }

        mainViewModel.isRunning.observe(this, { isRunning ->
            //Timer controls
            binding.timeControl.setOnClickListener {
                if (isRunning) {
                    //pause time
                    mainViewModel.pauseTimer()

                } else {
                    //resume timer
                    mainViewModel.resumeTime(applicationContext)
                }

            }
        })
        mainViewModel.isPaused.observe(this, {
            if (it) {
                binding.timeControl.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                binding.quickCountdown.visibility = VISIBLE
            } else {
                binding.timeControl.setImageResource(R.drawable.ic_baseline_pause_24)
                binding.quickCountdown.visibility = INVISIBLE
            }
        })
        // enable and disable sound
        mainViewModel.isEnabled.observe(this, { state ->
            if (!state) {
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_off_24)
            } else {
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_24)
            }
        })

        mainViewModel.isEnabled.observe(this, { state ->
            binding.alamFloatingActionButton.setOnClickListener {
                if (state) {
                    mainViewModel.setEnable(false)
                } else {
                    mainViewModel.setEnable(true)
                }
            }
        })

        mainViewModel.isRunning.observe(this, { isRunning ->
            //set start button on click listener
            setTimeView.findViewById<Button>(R.id.start_bt).setOnClickListener {
                if (isRunning) {
                    mainViewModel.pauseTimer()
                    mainViewModel.setRunning(false)
                    mainViewModel.updateTime()
                    mainViewModel.time.observe(this, { time ->
                        binding.timer.textSize = 120F
                        mainViewModel.startTimer(time, applicationContext)

                    })
                } else {
                    mainViewModel.updateTime()
                    mainViewModel.time.observe(this, { time ->
                        binding.timer.textSize = 120F
                        mainViewModel.startTimer(time, applicationContext)

                    })

                }
                //dismiss dialog
                alertDialog.dismiss()
            }

        })
        mainViewModel.isRunning.observe(this, {
            if (it) {
                //make quickCountdown button invisible at start time
                binding.quickCountdown.visibility = INVISIBLE
                binding.timeControl.setImageResource(R.drawable.ic_baseline_pause_24)
                binding.timeControl.visibility = VISIBLE
                binding.timer.textSize = 120F
            } else {
                binding.timeControl.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                binding.quickCountdown.visibility = VISIBLE
                binding.timeControl.visibility = VISIBLE
            }
        })

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


        mainViewModel.currentTime.observe(this, {
            val minute = (it / 1000) / 60
            val seconds = (it / 1000) % 60

            binding.timer.text = "$minute : $seconds"
            if (seconds < 30 && minute < 1) {
                val color = ContextCompat.getColor(applicationContext, R.color.red)
                binding.timer.setTextColor(color)
            } else {
                //set color to white
                val color = ContextCompat.getColor(applicationContext, R.color.white)
                binding.timer.setTextColor(color)
            }
        })
        mainViewModel.isFinished.observe(this, {
            if (it) {
                binding.timer.textSize = 100F
                binding.timeControl.visibility = GONE
                binding.timer.text = mainViewModel.finished


                //show ads
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }
        })

    }


}
