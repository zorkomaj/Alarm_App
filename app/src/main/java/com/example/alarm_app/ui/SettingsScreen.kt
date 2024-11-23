package com.example.alarm_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val settingsList = listOf("Setting 1", "Setting 2", "Setting 3")
    val checkedStates = remember { mutableStateListOf(true, true, true) }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(settingsList) { setting ->
                val index = settingsList.indexOf(setting)
                SettingRow(
                    settingName = setting,
                    checked = checkedStates[index],
                    onCheckedChange = {
                        checkedStates[index] = it
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {  },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Import")
            }
            Button(
                onClick = {  },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Export")
            }
        }
    }
}

@Composable
fun SettingRow(settingName: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp, 16.dp, 16.dp, 16.dp)
            .background(Color(0xd1d1d1ff), shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = settingName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 50.dp)
        )
    }
}
