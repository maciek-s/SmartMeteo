package com.masiad.smartmeteo

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.ui.sensors_list.SensorsListViewModel
import com.masiad.smartmeteo.utils.AppPreferences
import com.masiad.smartmeteo.utils.InternetConnection
import com.masiad.smartmeteo.utils.UP_SENSOR_SERIAL_NUMBER
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var sensorListViewModel: SensorsListViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var fab: FloatingActionButton

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sensorListViewModel =
            ViewModelProviders.of(this).get(SensorsListViewModel::class.java)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            showAddSensorModal()
        }

        progressBar = findViewById(R.id.progressBar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_sensors_list, R.id.nav_info
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private fun showAddSensorModal() {
        val builder = AlertDialog.Builder(this)

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val layout = layoutInflater.inflate(R.layout.dialog_add_sensor, null)
        if (AppPreferences.isFirstAppStart) {
            layout.findViewById<TextView>(R.id.fieldSerialNumber).text = UP_SENSOR_SERIAL_NUMBER
            AppPreferences.isFirstAppStart = false
        }
        builder.setView(layout)
            // Add action buttons
            .setPositiveButton(
                R.string.add
            ) { _, _ ->
                showProgressBar()
                val sensorName = layout.findViewById<TextView>(R.id.fieldSensorName).text
                val serialNumber = layout.findViewById<TextView>(R.id.fieldSerialNumber).text
                if (isSensorFieldsCorrectly(sensorName, serialNumber)) {
                    // Add sensor
                    addSensorToSensorsList(sensorName.toString(), serialNumber.toString())
                } else {
                    showErrorSnackBar(resources.getString(R.string.incorrect_input_values))
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ ->
                dialog.cancel()
            }
        builder.show()
    }

    private fun showErrorSnackBar(message: String) {
        hideProgressBar()
        Snackbar.make(
            this.findViewById(R.id.fab),
            message,
            Snackbar.LENGTH_LONG
        )
            .setAction("Again") { this.showAddSensorModal() }.show()
    }

    private fun isSensorFieldsCorrectly(
        sensorName: CharSequence?,
        serialNumber: CharSequence?
    ): Boolean {
        return sensorName?.isNotEmpty() ?: false && serialNumber?.isNotEmpty() ?: false

    }

    private fun addSensorToSensorsList(sensorName: String?, serialNumber: String?) {
        if (!InternetConnection.isOnline()) {
            showErrorSnackBar(resources.getString(R.string.check_internet_connection))
            return
        }
        val isNameAlreadyInserted = sensorListViewModel.isNameAlreadyInserted(sensorName!!)
        if (isNameAlreadyInserted) {
            showErrorSnackBar(resources.getString(R.string.sensor_name_already_exists))
            return
        }
        FirebaseDatabase.getInstance()
            .reference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    showErrorSnackBar(resources.getString(R.string.firebase_error))
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.children.firstOrNull { i ->
                            i.key.equals(
                                serialNumber,
                                true
                            )
                        } != null) {
                        // Serial exists add sensor to database
                        sensorListViewModel.insert(
                            Sensor(
                                sensorName = sensorName, serialNumber = serialNumber?.toUpperCase(
                                    Locale.ROOT
                                )
                            )
                        )
                        val navController =
                            Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                        if (navController.currentDestination?.id != R.id.nav_sensors_list) {
                            navController.navigate(R.id.nav_sensors_list)
                        }
                        hideProgressBar()
                        // Move to Sensor Fragment
                    } else {
                        // Show error snackbar
                        showErrorSnackBar(resources.getString(R.string.invalid_serial_number))
                    }
                }

            })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showFloatingActionButton() {
        fab.show()
    }

    fun hideFloatingActionButton() {
        fab.hide()
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}
