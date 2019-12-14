package com.masiad.smartmeteo.ui.sensors_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorsListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is sensors list Fragment"
    }
    val text: LiveData<String> = _text
}