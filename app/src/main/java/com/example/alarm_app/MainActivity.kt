package com.example.alarm_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.alarm_app.data.DatabaseModule
import com.example.alarm_app.ui.MapScreen
import com.example.alarm_app.ui.theme.Alarm_AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { entry ->
            val permission = entry.key
            val isGranted = entry.value
            Log.d("Permissions", "$permission granted: $isGranted")
        }

        checkBackgroundLocationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermissions()

        setContent {
            Alarm_AppTheme {
                MainScreen(context = this)
            }
        }
    }

    private fun checkLocationPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            checkBackgroundLocationPermission()
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivityLifecycle", "onStart called")

        val db = DatabaseModule.getDatabase(this@MainActivity)
        GlobalScope.launch(Dispatchers.Default) {
            val savedGeofences = db.alarmDataDao().getCheckedGeofences()

            savedGeofences.forEach { geofence ->
                Log.d("GeofenceDebug", "Loaded Geofence: ${geofence.locationName}, Lat: ${geofence.latitude}, Lng: ${geofence.longitude}, Radius: ${geofence.radius}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivityLifecycle", "onResume called")

        val db = DatabaseModule.getDatabase(this@MainActivity)

        GlobalScope.launch(Dispatchers.Default) {
            var allAlarmsList = db.alarmDataDao().getAllAlarmData()

            Log.d("SavedAlarms", allAlarmsList.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivityLifecycle", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivityLifecycle", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivityLifecycle", "onDestroy called")
    }
}
