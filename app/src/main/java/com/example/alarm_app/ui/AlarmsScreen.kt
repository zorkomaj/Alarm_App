package com.example.alarm_app.ui

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.alarm_app.data.AlarmWithLocation
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


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AlarmsScreen(context: Context, modifier: Modifier = Modifier) {
    val openAddAlarmDialog = remember { mutableStateOf(false) }

    val allAlarmsList = remember { mutableStateOf(emptyList<AlarmWithLocation>()) }


    val db = DatabaseModule.getDatabase(context)

    GlobalScope.launch(Dispatchers.Default) {
        allAlarmsList.value = db.alarmDataDao().getAllAlarmData()
        //Log.d("korutina", allAlarmsList.value.toString())
    }


    //Button(onClick = {}) { }
    Box(modifier = modifier.fillMaxSize(),
        ) {
        when {
            allAlarmsList.value.isNotEmpty() -> {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                ) {
                    items(allAlarmsList.value) { currentAlarm ->
                        Alarm(
                            name = currentAlarm.alarmName,
                            location = currentAlarm.locationName,
                            checked = currentAlarm.checked,
                        )
                        //Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }

//        LazyColumn(
//            modifier = modifier.fillMaxSize(),
//            //verticalArrangement = Arrangement.Center,
//            //horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            items(alarmList) { currentAlarm ->
//                Alarm(
//                    name = currentAlarm.name,
//                    location = currentAlarm.location,
//                    radius = currentAlarm.radius,
//                    repetitions = currentAlarm.repetitions,
//                    checked = currentAlarm.checked,
//                )
//                //Spacer(modifier = Modifier.height(5.dp))
//            }
//
//        }

        Button(onClick = {
            openAddAlarmDialog.value = !openAddAlarmDialog.value}, modifier =
        Modifier
            .align(Alignment.BottomEnd)
            .padding(0.dp, 0.dp, 20.dp, 140.dp)
            .width(70.dp)
            .height(70.dp)) {
            Text(text = "+", fontSize = 30.sp, textAlign = TextAlign.Center)

        }

        Button(onClick = {
            val db = DatabaseModule.getDatabase(context)
            GlobalScope.launch(Dispatchers.Default) { allAlarmsList.value = db.alarmDataDao().getAllAlarmData() }
            }, modifier =
        Modifier
            .align(Alignment.BottomStart)
            .padding(20.dp, 0.dp, 0.dp, 140.dp)
            .width(70.dp)
            .height(70.dp)) {
            //Text(text = "+", fontSize = 30.sp, textAlign = TextAlign.Center)
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")

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
          checked: Boolean) {
    var checked by remember { mutableStateOf(checked) }

    Row (
        Modifier
            .padding(20.dp, 20.dp, 20.dp, 0.dp)
            .background(color = Color(0xd1d1d1ff), shape = RoundedCornerShape(20.dp))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column (
            Modifier.padding(10.dp)

        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 30.sp, modifier = Modifier.padding(start = 10.dp))
            Text(text = location, fontSize = 20.sp, modifier = Modifier.padding(start = 10.dp))
        }
        Switch(
            modifier = Modifier.padding(end = 10.dp),
            checked = checked,
            onCheckedChange = {
                checked = it
            }
        )

    }

}