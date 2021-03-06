package com.vicksoson.timecountdown

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vicksoson.timecountdown.adapter.ScheduleAdapter
import com.vicksoson.timecountdown.databinding.ActivityMainBinding
import com.vicksoson.timecountdown.databinding.MenuOptionBinding
import com.vicksoson.timecountdown.databinding.SetScheduleTimeBinding
import com.vicksoson.timecountdown.databinding.SetTimmerBinding
import com.vicksoson.timecountdown.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: MenuOptionBinding
    private lateinit var scheduleBinding: SetScheduleTimeBinding
    private lateinit var setTimeViewBinding: SetTimmerBinding

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


        //set time alert builder
        val builder = AlertDialog.Builder(this)
        val setTimeViewBinding = SetTimmerBinding.inflate(layoutInflater)
        builder.setView(setTimeViewBinding.root)

        val alertDialog = builder.create()


        //initialize set time textView
        val setSeconds = setTimeViewBinding.secondsText
        val setMinutes = setTimeViewBinding.minuteText


        //menu alert builder
        val menuBuilder = AlertDialog.Builder(this)
        menuBinding = MenuOptionBinding.inflate(layoutInflater)
        menuBuilder.setView(menuBinding.root)
        val alertMenu = menuBuilder.create()

        val soundSwitch = menuBinding.soundSwitch

        //set schedule alert dialog
        val scheduleBuilder = AlertDialog.Builder(this)
        scheduleBinding = SetScheduleTimeBinding.inflate(layoutInflater)
        scheduleBuilder.setView(scheduleBinding.root)
        val alertSchedule = scheduleBuilder.create()


        binding.quickCountdown.setOnClickListener {
            mainViewModel.resetValues()
            alertDialog.show()

        }

        mainViewModel.isRunning.observe(this, { isRunning ->
            //Timer controls
            binding.timeControl.setOnClickListener {
                if (isRunning == true) {
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
                soundSwitch.isChecked = false
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_off_24)
            } else {
                soundSwitch.isChecked = true
                binding.alamFloatingActionButton.setImageResource(R.drawable.ic_baseline_notifications_24)
            }
        })

        val adapter = ScheduleAdapter()

//        mainViewModel.isEnabled.observe(this, { state ->
        binding.menuButton.setOnClickListener {
            alertMenu.show()
            soundSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isChecked) {
                    mainViewModel.setEnable(isChecked)
                } else {
                    mainViewModel.setEnable(isChecked)
                }
            }


        }

        val recycler = menuBinding.recyclerView
        recycler.layoutManager = LinearLayoutManager(this)

        mainViewModel.schedules.observe(this, {
            adapter.setUpSchedules(it)
        })
        recycler.adapter = adapter


        mainViewModel.isRunning.observe(this, { isRunning ->
            //set start button on click listener
            setTimeViewBinding.startBt.setOnClickListener {
                if (setMinutes.text.toString().toInt().plus(setSeconds.text.toString().toInt()) > 0){
                    if (isRunning == true) {
                        mainViewModel.pauseTimer()
                        mainViewModel.setRunning(false)
                        mainViewModel.updateTime()
                        mainViewModel.time.observe(this, { time ->
                            binding.timer.textSize = 120F
                            mainViewModel.startTimer(time, applicationContext)
                            mainViewModel.paused(false)
                        })
                        mainViewModel.setTaskText("Quick countdown")
                    } else if (isRunning == false || isRunning == null) {
                        mainViewModel.updateTime()
                        mainViewModel.time.observe(this, { time ->
                            if(time > 0){
                                binding.timer.textSize = 120F
                                mainViewModel.startTimer(time, applicationContext)
                                mainViewModel.paused(false)
                            }
                        })
                        mainViewModel.setTaskText("Quick countdown")
                    }
                    //dismiss dialog
                    alertDialog.dismiss()
                }else{
                    Toast.makeText(this, "must be at least 1sec", Toast.LENGTH_SHORT).show()
                }

            }

        })
        mainViewModel.isRunning.observe(this, {
            if (it == true) {
                //make quickCountdown button invisible at start time
                binding.quickCountdown.visibility = INVISIBLE
                binding.timeControl.setImageResource(R.drawable.ic_baseline_pause_24)
                binding.timeControl.visibility = VISIBLE
                binding.timer.textSize = mainViewModel.runSize
            } else {
                binding.timeControl.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                binding.quickCountdown.visibility = VISIBLE
                binding.timeControl.visibility = VISIBLE
            }
        })

        //initialize cancel button
        val cancel = setTimeViewBinding.cancel
        //set cancel button on click listener
        cancel.setOnClickListener {
            //dismiss dialog
            alertDialog.dismiss()
        }
        //increase seconds on click
        setTimeViewBinding.sIncrement.setOnClickListener {
            mainViewModel.sIncrement()
            mainViewModel.seconds.observe(this, {
                setSeconds.text = it.toString()
            })

        }
        //decrease seconds when clicked
        setTimeViewBinding.sDecrement.setOnClickListener {
            mainViewModel.sDecrement()
            mainViewModel.seconds.observe(this, {
                setSeconds.text = it.toString()
            })
        }

        //increase minute on click
        setTimeViewBinding.mIncrement.setOnClickListener {
            mainViewModel.mIncrement()
            mainViewModel.minutes.observe(this, {
                setMinutes.text = it.toString()
            })

        }
        //decrease minutes when clicked
        setTimeViewBinding.mDecrement.setOnClickListener {
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
                binding.timer.textSize = mainViewModel.finishSize
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


//        val owner: LifecycleOwner = this

        menuBinding.addBt.setOnClickListener {
            alertSchedule.show()
        }


        //text watcher
        val watcher: TextWatcher = object : TextWatcher {

            val taskName = scheduleBinding.taskName.text
            val mins = scheduleBinding.minsText.text
            val secs = scheduleBinding.secsText.text

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //check if they are empty, make button not clickable
                if (!(taskName.toString().isEmpty() || mins.toString().isEmpty() || secs.toString()
                        .isEmpty())
                ) {
                    //enable button
                    scheduleBinding.setBt.isEnabled = true


                    //add to list
                    scheduleBinding.setBt.setOnClickListener {

                        Log.d("Main", "$mins, $secs")
                        //add to schedule list
                        mainViewModel.addSchedule(
                            taskName.toString(), mins.toString().toInt(), secs.toString().toInt()
                        )

                        mins?.clear()
                        secs?.clear()
                        taskName?.clear()
                        scheduleBinding.taskName.requestFocus()

                        //dismiss alert
                        alertSchedule.dismiss()
                    }

                } else {
                    scheduleBinding.setBt.isEnabled = false
                }

            }

            override fun afterTextChanged(s: Editable?) {}

        }

        //add watchers to edit texts
        scheduleBinding.taskName.addTextChangedListener(watcher)
        scheduleBinding.minsText.addTextChangedListener(watcher)
        scheduleBinding.secsText.addTextChangedListener(watcher)

        //set task text
        mainViewModel.taskText.observe(this,{
            binding.taskDisplayName.text = it
        })


        //set task on click listener
            adapter.setOnItemClickListener {
                mainViewModel.schedules.observe(this, {
                    mainViewModel.startScheduleTime(it[adapter.serial].taskTime, applicationContext)
                    mainViewModel.setTaskText(it[adapter.serial].taskName)
                })
                //dismiss dialog
                alertMenu.dismiss()
            }


    }
}
