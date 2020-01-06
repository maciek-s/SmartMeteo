package com.masiad.smartmeteo

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.ui.sensors_list.SensorsListViewModel
import com.masiad.smartmeteo.utils.InternetConnection
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sensorListViewModel: SensorsListViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable Firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        sensorListViewModel =
            ViewModelProviders.of(this).get(SensorsListViewModel::class.java)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            showAddSensorModal()

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_sensors_list, R.id.nav_info
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
        builder.setView(layout)
            // Add action buttons
            .setPositiveButton(
                R.string.add
            ) { dialog, id ->
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
            ) { dialog, id ->
                dialog.cancel()
            }
        builder.show()
    }

    private fun showErrorSnackBar(message: String) {
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
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                    val idx = sensorListViewModel.insert(
                        Sensor(
                            sensorName = sensorName, serialNumber = serialNumber?.toUpperCase(
                                Locale.ROOT
                            )
                        )
                    )
                    Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                        .navigate(R.id.nav_sensors_list)
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

    public fun showFloatingActionButton() {
        fab.show()
    }

    public fun hideFloatingActionButton() {
        fab.hide()
    }
}
