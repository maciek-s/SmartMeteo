package com.masiad.smartmeteo.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for [InfoFragment]
 */
class InfoViewModel : ViewModel() {
    companion object {
        val TAG: String = InfoViewModel::class.java.simpleName
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is info Fragment"
    }
    val text: LiveData<String> = _text
}