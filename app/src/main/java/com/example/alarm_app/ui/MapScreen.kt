package com.example.alarm_app.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.alarm_app.data.DatabaseModule
import com.example.alarm_app.data.MapData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(modifier: Modifier = Modifier, context: Context) {
    val geofencingClient = remember { LocationServices.getGeofencingClient(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var geofenceLatLng by remember { mutableStateOf<LatLng?>(null) }
    var geofenceRadius by remember { mutableStateOf(100f) }
    var radiusInput by remember { mutableStateOf(TextFieldValue(geofenceRadius.toInt().toString())) }
    var locationName by remember { mutableStateOf(TextFieldValue("")) }
    var isFirstLocationUpdate by remember { mutableStateOf(true) }

    val savedGeofences = remember { mutableStateListOf<MapData>() }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.060574607585295, 14.512268608273097), 15f)
    }

    LaunchedEffect(Unit) {
        fetchSavedGeofences(context, savedGeofences)
    }

    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val newLocation = LatLng(location.latitude, location.longitude)
                    currentLocation = newLocation
                    Log.d("LocationUpdate", "Current location: $newLocation")

                    if (isFirstLocationUpdate) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 15f)
                        isFirstLocationUpdate = false
                    }
                }
            }
        }, context.mainLooper)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                geofenceLatLng = latLng
                createGeofence(geofencingClient, context, latLng, geofenceRadius)
            }
        ) {
            currentLocation?.let {
                Marker(state = rememberMarkerState(position = it), title = "Current Location")
            }

            geofenceLatLng?.let {
                Circle(
                    center = it,
                    fillColor = Color(0x22FF0000),
                    strokeColor = Color(0xFFFF0000),
                    radius = geofenceRadius.toDouble()
                )
            }

            savedGeofences.forEach { geofence ->
                Circle(
                    center = LatLng(geofence.latitude, geofence.longitude),
                    fillColor = Color(0x2200FF00),
                    strokeColor = Color(0xFF00FF00),
                    radius = geofence.radius.toDouble()
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            TextField(
                value = radiusInput,
                onValueChange = {
                    radiusInput = it
                    val newRadius = it.text.toFloatOrNull()
                    if (newRadius != null) {
                        geofenceRadius = newRadius
                    }
                },
                label = { Text("Enter Radius (meters)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            TextField(
                value = locationName,
                onValueChange = {
                    locationName = it
                },
                label = { Text("Enter Location Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    geofenceLatLng?.let { latLng ->
                        saveCoordinatesToDatabase(context, latLng, geofenceRadius, locationName.text) {
                            savedGeofences.add(it)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Coordinates")
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun createGeofence(
    geofencingClient: GeofencingClient,
    context: Context,
    latLng: LatLng,
    radius: Float
) {
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = "GEOFENCE_TRANSITION_ACTION"
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    geofencingClient.removeGeofences(geofencePendingIntent).addOnCompleteListener { task ->
        if (task.isSuccessful) {

            val geofence = Geofence.Builder()
                .setRequestId("GEOFENCE_ID_${System.currentTimeMillis()}")
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(30000)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener {
                    Log.d("GeofenceDebug", "Geofence created successfully at Lat=${latLng.latitude}, Lng=${latLng.longitude}, Radius=$radius")
                }
                .addOnFailureListener { e ->
                    Log.e("GeofenceDebug", "Failed to create geofence: ${e.message}")
                }
        } else {
            Log.e("GeofenceDebug", "Failed to remove old geofences: ${task.exception?.message}")
        }
    }
}

fun fetchSavedGeofences(context: Context, savedGeofences: MutableList<MapData>) {
    CoroutineScope(Dispatchers.IO).launch {
        val db = DatabaseModule.getDatabase(context)
        val checkedGeofences = db.alarmDataDao().getCheckedGeofences()

        savedGeofences.addAll(checkedGeofences)

        checkedGeofences.forEach { geofence ->
            Log.d("GeofenceDebug", "Checked geofence: ${geofence.locationName}, Lat: ${geofence.latitude}, Lng: ${geofence.longitude}")
        }
    }
}

fun saveCoordinatesToDatabase(
    context: Context,
    latLng: LatLng,
    radius: Float,
    locationName: String,
    onSaved: (MapData) -> Unit
) {
    val db = DatabaseModule.getDatabase(context)
    val mapData = MapData(
        locationName = locationName,
        latitude = latLng.latitude,
        longitude = latLng.longitude,
        radius = radius
    )

    CoroutineScope(Dispatchers.IO).launch {
        db.mapDataDao().insertMapData(mapData)
        Log.d("GeofenceDebug", "Saved to database: $mapData")
        onSaved(mapData)
    }
}
