package com.brightstart.app.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/** Lets the network layer tell the UI that the login token is no longer valid. */
object SessionEvents {
    private val _expired = MutableLiveData<Boolean>()
    val expired: LiveData<Boolean> = _expired

    fun notifyExpired() { _expired.postValue(true) }
    fun consumed() { _expired.postValue(false) }
}
