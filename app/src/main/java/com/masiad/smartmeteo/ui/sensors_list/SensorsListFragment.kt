package com.masiad.smartmeteo.ui.sensors_list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chibatching.kotpref.blockingBulk
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.utils.AppPreferences

/**
 * Sensor List [Fragment]
 */
class SensorsListFragment : Fragment() {
    companion object {
        val TAG: String = SensorsListFragment::class.java.simpleName
    }

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

        val favouriteSensorId = AppPreferences.favouriteSensorId

        val sensorAdapter = object : SensorsListAdapter(requireContext(), favouriteSensorId) {
            override fun onItemClick(sensorId: Int) {
                Navigation.findNavController(root)
                    .navigate(SensorsListFragmentDirections.actionNavSensorsListToSensorFragment().apply {
                        setSensorId(sensorId)
                    })
            }

            override fun onFavouriteItemClick(sensorId: Int) {
                // save default in shared preferences
                AppPreferences.favouriteSensorId = sensorId
            }

            override fun onLongItemClick(sensorId: Int): Boolean {
                return onLongClick(sensorId)
            }

        }
        adapter = sensorAdapter
        // Sensor list observe
        sensorListViewModel.allSensors.observe(viewLifecycleOwner, Observer { sensors ->
            sensors?.let { adapter.setSensors(it) }
        })

        return root
    }

    private fun onLongClick(sensorId: Int): Boolean {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_delete_sensor)
            .setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                // Remove sensor form database
                sensorListViewModel.deleteById(sensorId)
                adapter.notifyDataSetChanged()
                // Remove from favourite
                if (AppPreferences.favouriteSensorId == sensorId) {
                    AppPreferences.blockingBulk {
                        favouriteSensorId = -1
                    }
                }
                Log.i(TAG, "Delete favourite: " + AppPreferences.favouriteSensorId)
            }
            .setNegativeButton(
                android.R.string.cancel, null
            )
            .show()

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )
        )

    }
}
