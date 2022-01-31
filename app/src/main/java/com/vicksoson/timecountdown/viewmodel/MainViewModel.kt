package com.vicksoson.timecountdown.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.vicksoson.timecountdown.R

class MainViewModel : ViewModel() {

    private lateinit var countdownTimer: CountDownTimer
    private val _minutes = MutableLiveData<Int>()
    private val _seconds = MutableLiveData<Int>()
    private val _time = MutableLiveData<Long>()
    private val _currentTime = MutableLiveData<Long>()
    private val _isEnable = MutableLiveData(true)
    private val _isRunning = MutableLiveData<Boolean>()
    private val _isFinished = MutableLiveData<Boolean>()
    private val _isPaused = MutableLiveData<Boolean>()


    val finished = "TIME'S UP"
    var isEnabled: LiveData<Boolean> = _isEnable
    var isPaused: LiveData<Boolean> = _isPaused
    var isRunning: LiveData<Boolean> = _isRunning
    var isFinished: LiveData<Boolean> = _isFinished
    private var m = 0
    private var s = 0
    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val time: LiveData<Long> = _time
    val currentTime: LiveData<Long> = _currentTime

    fun setRunning(state: Boolean) {
        _isRunning.value = state
    }

    fun setEnable(state: Boolean) {
        _isEnable.value = state
    }

    fun updateTime() {
        _time.value =
            minutes.value?.toLong()?.times(60000L)?.plus((seconds.value?.toLong()?.times(1000L)!!))
    }

    fun setTime(p0: Long) {
        _time.value = p0
    }

    fun paused(state: Boolean){
        _isPaused.value = true
    }

    fun setCurrentTime(cTime: Long) {
        _currentTime.value = cTime
    }

    fun resetValues() {
        _minutes.value = 0
        _seconds.value = 0
        m = 0
        s = 0
        _isRunning.value = false
    }

    fun mIncrement() {
        if (m < 61) {
            m += 1
            _minutes.value = m
        }
    }

    fun mDecrement() {
        if (m > 0) {
            m -= 1
            _minutes.value = m
        }
    }


    fun sIncrement() {
        if (s < 61) {
            s += 1
            _seconds.value = s
        }
    }

    fun sDecrement() {
        if (s > 0) {
            s -= 1
            _seconds.value = s
        }
    }


    fun startTimer(time_in_seconds: Long, context: Context) {
        countdownTimer = object : CountDownTimer(time_in_seconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                _isRunning.value = false
                _isFinished.value = true

                val sound = MediaPlayer.create(context, R.raw.ding_sound)
                if (isEnabled.value == true) {
                    sound.start()
                } else {
                    sound.stop()
                }
//
//                //show ads
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(this)
//                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                }
            }

            override fun onTick(p0: Long) {
                _currentTime.value = p0
            }

        }
        countdownTimer.start()
        _isRunning.value = true
        _isFinished.value = false


    }
    fun pauseTimer() {
        countdownTimer.cancel()
        _isRunning.value = false
        _isPaused.value = true
    }
    fun playSound(context: Context){
        if (isFinished.value == true){
            val sound = MediaPlayer.create(context, R.raw.ding_sound)
            if (isEnabled.value == true) {
                sound.start()
            } else {
                sound.stop()
            }
        }
    }
    fun resumeTime(context: Context){
        currentTime.value?.let { startTimer(it,context) }
        _isPaused.value = false
    }

}