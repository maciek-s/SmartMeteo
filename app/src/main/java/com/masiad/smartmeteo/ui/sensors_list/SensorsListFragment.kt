package com.masiad.smartmeteo.ui.sensors_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masiad.smartmeteo.R

class SensorsListFragment : Fragment() {

    private lateinit var sensorListViewModel: SensorsListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SensorsListAdapter

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

        adapter = SensorsListAdapter(requireContext())
        // Sensor list observe
        sensorListViewModel.allSensors.observe(viewLifecycleOwner, Observer { sensors ->
            sensors?.let { adapter.setSensors(it) }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))

    }
}
