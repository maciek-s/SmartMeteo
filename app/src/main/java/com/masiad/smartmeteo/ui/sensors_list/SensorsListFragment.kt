package com.masiad.smartmeteo.ui.sensors_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.masiad.smartmeteo.R

class SensorsListFragment : Fragment() {

    private lateinit var sensorListViewModel: SensorsListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        sensorListViewModel =
                ViewModelProviders.of(this).get(SensorsListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_sensors_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_sensorsList)
        sensorListViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
