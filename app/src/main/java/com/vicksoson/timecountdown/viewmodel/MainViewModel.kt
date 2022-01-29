package com.vicksoson.timecountdown.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _minutes = MutableLiveData(0)
    private val _seconds = MutableLiveData(0)
    private val _time = MutableLiveData<Long>()
    private val _currentTime = MutableLiveData<Long>()
    private val _isEnable = MutableLiveData<Boolean>()
    private val _isRunning = MutableLiveData<Boolean>()
    var isEnabled: LiveData<Boolean> = _isEnable
    var isRunning: LiveData<Boolean> = _isRunning
    private var m = 0
    private var s = 0
    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val time: LiveData<Long> = _time
    val currentTime: LiveData<Long> = _currentTime

    fun setRunning(state: Boolean){
        _isRunning.value = state
    }

    fun setEnable(state: Boolean){
        _isEnable.value = state
    }

    fun updateTime() {
        _time.value =
            minutes.value?.toLong()?.times(60000L)?.plus((seconds.value?.toLong()?.times(6000L)!!))
    }

    fun setCurrentTime(cTime: Long) {
        _currentTime.value = cTime
    }

    fun resetValues() {
        _minutes.value = 0
        _seconds.value = 0
        m = 0
        s = 0
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

}