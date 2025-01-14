package com.example.alarm_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.alarm_app.data.alarmList

data class AlarmList(val name: String, val location: String,
                        val radius: Int, val repetitions: String,
                        val checked: Boolean)


@Composable
fun AlarmsScreen(modifier: Modifier = Modifier) {
    //Button(onClick = {}) { }
    LazyColumn (
        modifier = modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        //horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(alarmList) { currentAlarm ->
            Alarm (
                name = currentAlarm.name,
                location = currentAlarm.location,
                radius = currentAlarm.radius,
                repetitions = currentAlarm.repetitions,
                checked = currentAlarm.checked,
            )
            //Spacer(modifier = Modifier.height(5.dp))
        }

    }
}


@Composable
fun Alarm(modifier: Modifier = Modifier,
          name: String,
          location: String,
          radius: Int,
          repetitions: String,
          checked: Boolean) {
    var checked by remember { mutableStateOf(true) }

    Row (
        Modifier
            //.padding(20.dp)
            .padding(20.dp, 20.dp, 20.dp, 0.dp)
            .background(color = Color(0xd1d1d1ff), shape = RoundedCornerShape(20.dp))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column (
            Modifier.padding(10.dp)

        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Text(text = location, fontSize = 20.sp)
            Text(text = "$radius m", fontSize = 20.sp)
            Text(text = repetitions, fontSize = 20.sp)
        }
        Switch(
            modifier = Modifier.padding(start = 50.dp),
            checked = checked,
            onCheckedChange = {
                checked = it
            }
        )

    }

}