package com.masiad.smartmeteo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import com.chibatching.kotpref.Kotpref
import com.google.firebase.database.FirebaseDatabase
import com.masiad.smartmeteo.utils.AppPreferences

/**
 * Smart Mete [Application] class
 */
class SmartMeteoApplication : Application() {
    private var isActivityRestarted = false

    companion object {
        val TAG: String = SmartMeteoApplication::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()

        // Enable Firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Init Kotpref
        Kotpref.init(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(p0: Activity) {
                Log.i(TAG, "Activity paused")
            }

            override fun onActivityStarted(p0: Activity) {
                Log.i(TAG, "Activity started")

                // Load favourite if set
                if (AppPreferences.favouriteSensorId != -1 && !isActivityRestarted) {
                    Navigation.findNavController(p0, R.id.nav_host_fragment)
                        .navigate(R.id.nav_sensor)
                }
            }

            override fun onActivityDestroyed(p0: Activity) {
                Log.i(TAG, "Activity destroyed")
                isActivityRestarted = true
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                Log.i(TAG, "Activity save instance")
            }

            override fun onActivityStopped(p0: Activity) {
                Log.i(TAG, "Activity stopped")
            }

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                Log.i(TAG, "Activity created")
            }

            override fun onActivityResumed(p0: Activity) {
                Log.i(TAG, "Activity resumed")
            }

        })
    }
}