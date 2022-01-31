package com.vicksoson.timecountdown.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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


    fun startTimer(time_in_seconds: Long) {
        countdownTimer = object : CountDownTimer(time_in_seconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                setRunning(false)
                _isFinished.value = true
            }

            override fun onTick(p0: Long) {
                _currentTime.value = p0
            }

        }
        countdownTimer.start()
        setRunning(true)
        _isFinished.value = false


    }
    fun pauseTimer() {
        countdownTimer.cancel()
        setRunning(false)
    }
    fun resumeTime(){
        currentTime.value?.let { startTimer(it) }
    }

}