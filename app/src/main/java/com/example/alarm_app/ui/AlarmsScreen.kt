package com.example.alarm_app.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import com.example.alarm_app.data.DatabaseModule
import com.example.alarm_app.data.MapData
import com.example.alarm_app.data.alarmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class AlarmList(val name: String, val location: String,
                        val radius: Int, val repetitions: String,
                        val checked: Boolean)


@Composable
fun AlarmsScreen(context: Context, modifier: Modifier = Modifier) {
    val openAddAlarmDialog = remember { mutableStateOf(false) }

    //Button(onClick = {}) { }
    Box(modifier = modifier.fillMaxSize(),
        ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            //verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(alarmList) { currentAlarm ->
                Alarm(
                    name = currentAlarm.name,
                    location = currentAlarm.location,
                    radius = currentAlarm.radius,
                    repetitions = currentAlarm.repetitions,
                    checked = currentAlarm.checked,
                )
                //Spacer(modifier = Modifier.height(5.dp))
            }

        }


        Button(onClick = {
            openAddAlarmDialog.value = !openAddAlarmDialog.value}, modifier =
        Modifier
            .align(Alignment.BottomEnd)
            .padding(0.dp, 0.dp, 20.dp, 140.dp)
            .width(70.dp)
            .height(70.dp)) {
            Text(text = "+", fontSize = 30.sp, textAlign = TextAlign.Center)

        }

        when {
            openAddAlarmDialog.value -> {
                AddAlarmDialog(
                    context = context,
                    onDismissRequest = { openAddAlarmDialog.value = false },
                    onConfirmation = {
                        openAddAlarmDialog.value = false
                        println("Confirmation registered") // Add logic here to handle confirmation.
                    },
                )
            }
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