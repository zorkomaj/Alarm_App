package com.example.alarm_app.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import com.example.alarm_app.data.alarmList
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.alarm_app.data.DatabaseModule
import com.example.alarm_app.data.MapData
import com.example.alarm_app.data.MapDataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddAlarmDialog(
    context: Context,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {

    var dropdownSelectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var alarmName by rememberSaveable { mutableStateOf("") }

    val allLocationsList = remember { mutableStateOf(emptyList<MapData>()) }
    var allLocationsRefresh by remember { mutableIntStateOf(0) }


    val db = DatabaseModule.getDatabase(context)

    GlobalScope.launch(Dispatchers.Default) {
        allLocationsList.value = db.mapDataDao().getAllMapData()
        allLocationsRefresh++
        Log.d("korutina", allLocationsList.value.toString())
    }


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                UserTextField(alarmName, "Alarm name", onValueChange = { alarmName = it })

                Spacer(modifier = Modifier
                    .height(30.dp))


//                if (allLocationsList.value.isEmpty()) {
//                    Log.d("ifelse", "if")
//                    DropdownList(listOf("Data loading"),
//                        dropdownSelectedIndex,
//                        modifier = Modifier,
//                        onItemClick = {dropdownSelectedIndex = it})
//                }
//                else {
//                    var locationNamesList = mutableListOf<String>()
//                    for (item in allLocationsList.value) {
//                        locationNamesList += item.locationName
//                    }
//
//                    Log.d("ifelse", "else")
//                    DropdownList(locationNamesList,
//                        dropdownSelectedIndex,
//                        modifier = Modifier,
//                        onItemClick = {dropdownSelectedIndex = it})
//                }
                when {
                    allLocationsList.value.isNotEmpty() -> {
                        var locationNamesList = mutableListOf<String>()
                        for (item in allLocationsList.value) {
                            locationNamesList += item.locationName
                        }
                        DropdownList(locationNamesList, //listOf("item 1", "item 2", "item 3", "4", "5", "6", "7"),
                            dropdownSelectedIndex,
                            modifier = Modifier,
                            onItemClick = {dropdownSelectedIndex = it})
                    }
                }
//                DropdownList(locationNamesList, //listOf("item 1", "item 2", "item 3", "4", "5", "6", "7"),
//                    dropdownSelectedIndex,
//                    modifier = Modifier,
//                    onItemClick = {dropdownSelectedIndex = it})

                Spacer(modifier = Modifier
                    .height(150.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { Log.d("InformationRecived",
                            dropdownSelectedIndex.toString() + ", " + alarmName)
                            onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

suspend fun getMapData(context: Context): List<MapData> {
    val db = DatabaseModule.getDatabase(context)
    var allLocationsList = emptyList<MapData>()

    Log.d("funkcija", "izvede se")
    GlobalScope.async(Dispatchers.Default) {
        /*val a = db.mapDataDao().getAllMapData()
        Log.d("aaaaaaaaa", a.toString())*/

        allLocationsList = db.mapDataDao().getAllMapData()
    }.await()
    Log.d("funkcija", "konča se")

    return allLocationsList
}



@Composable
fun UserTextField(alarmName: String, label: String, onValueChange: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = alarmName,
        onValueChange = onValueChange,
        label = { Text(label) }
    )
}

@Composable
fun DropdownList(itemList: List<String>, selectedIndex: Int, modifier: Modifier, onItemClick: (Int) -> Unit) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (itemList.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // button
        Box(
            modifier = modifier
                .background(Color.White)
                .clickable { showDropdown = true }
                .width(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = itemList[selectedIndex], modifier = Modifier.padding(3.dp))
        }

        Box() {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    onDismissRequest = { showDropdown = false }
                ) {

                    Column(
                        modifier = modifier
                            .width(200.dp)
                            .heightIn(max = 90.dp)
                            .verticalScroll(state = scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList.onEachIndexed { index, item ->
                            if (index != 0) {
                                Divider(thickness = 1.dp, color = Color.Transparent)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemClick(index)
                                        showDropdown = !showDropdown
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item,)
                            }
                        }

                    }
                }
            }
        }
    }

}

