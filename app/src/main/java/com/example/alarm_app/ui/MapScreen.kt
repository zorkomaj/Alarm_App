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

@Composable
fun MapScreen(modifier: Modifier = Modifier, context: Context) {
    val geofencingClient = remember { LocationServices.getGeofencingClient(context) }

    var geofenceLatLng by remember { mutableStateOf(LatLng(46.060574607585295, 14.512268608273097)) } // Default location
    var geofenceRadius by remember { mutableStateOf(100f) }
    var radiusInput by remember { mutableStateOf(TextFieldValue(geofenceRadius.toInt().toString())) }
    var alarmName by remember { mutableStateOf(TextFieldValue("")) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(geofenceLatLng, 15f)
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
            Marker(state = rememberMarkerState(position = geofenceLatLng))

            Circle(
                center = geofenceLatLng,
                fillColor = Color(0x22FF0000),
                strokeColor = Color(0xFFFF0000),
                radius = geofenceRadius.toDouble()
            )
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
                value = alarmName,
                onValueChange = {
                    alarmName = it
                },
                label = { Text("Enter Alarm Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    saveCoordinatesToDatabase(context, geofenceLatLng, geofenceRadius, alarmName.text)
                    Log.d("GeofenceDebug", "Saved: AlarmName=${alarmName.text}, Lat=${geofenceLatLng.latitude}, Lng=${geofenceLatLng.longitude}, Radius=$geofenceRadius")
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
    val geofence = Geofence.Builder()
        .setRequestId("GEOFENCE_ID")
        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .build()

    val geofencingRequest = GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofence(geofence)
        .build()

    val geofencePendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, GeofenceBroadcastReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
        .addOnSuccessListener {
            Log.d("GeofenceDebug", "Geofence created successfully at Lat=${latLng.latitude}, Lng=${latLng.longitude}, Radius=$radius")
        }
        .addOnFailureListener { e ->
            Log.e("GeofenceDebug", "Failed to create geofence: ${e.message}")
        }
}

fun saveCoordinatesToDatabase(context: Context, latLng: LatLng, radius: Float, alarmName: String) {
    val db = DatabaseModule.getDatabase(context)
    val mapData = MapData(
        alarmName = alarmName,
        latitude = latLng.latitude,
        longitude = latLng.longitude,
        radius = radius
    )

    CoroutineScope(Dispatchers.IO).launch {
        db.mapDataDao().insertMapData(mapData)
        Log.d("GeofenceDebug", "Saved to database: $mapData")
    }
}