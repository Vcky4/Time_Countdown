package com.example.timecountdown

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.timecountdown.databinding.ActivityMainBinding
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity() {

    private var START_MILLI_SECONDS = 60000L

    private lateinit var countdown_timer: CountDownTimer
    private var isRunning: Boolean = false
    var time_in_milli_seconds = 0L
    private lateinit var binding: ActivityMainBinding


    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initiate admob
        MobileAds.initialize(this) {}
        //set ads view
        val mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        //set fullscreen callback
        InterstitialAd.load(this,"ca-app-pub-2805616918393635/4878840900",
            adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })


        //alert builder
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
            if (isRunning) {
                countdown_timer.cancel()
                isRunning = false

                time_in_milli_seconds = setTime.text.toString().toLong() * 60000L
                startTimer(time_in_milli_seconds, this)
            } else {
                resetTimer()
                time_in_milli_seconds = setTime.text.toString().toLong() * 60000L
                startTimer(time_in_milli_seconds, this)
            }
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

    private fun startTimer(time_in_seconds: Long, activity: Activity) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.timer.text = "TIME'S UP"
                val color = ContextCompat.getColor(applicationContext, R.color.red)
                binding.timer.setTextColor(color)

                //show ads
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(activity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
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