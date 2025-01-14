package com.example.alarm_app.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, context: Context) {
    var uploadStatus by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val success = saveFileAsAlarmSound(context, uri)
            uploadStatus = if (success) "Alarm sound updated successfully!" else "Failed to update alarm sound."
        } else {
            uploadStatus = "No file selected."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { filePickerLauncher.launch("audio/*") }) {
            Text("Upload Alarm Sound", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = uploadStatus, fontSize = 16.sp)
    }
}

private fun saveFileAsAlarmSound(context: Context, uri: Uri): Boolean {
    return try {
        val alarmsoundFile = File(context.filesDir, "alarmsound.mp3")

        if (alarmsoundFile.exists()) {
            alarmsoundFile.delete()
        }

        val inputStream = context.contentResolver.openInputStream(uri) ?: return false
        FileOutputStream(alarmsoundFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
