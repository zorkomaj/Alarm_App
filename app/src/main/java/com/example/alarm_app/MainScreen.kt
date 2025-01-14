package com.example.alarm_app

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.alarm_app.ui.AlarmsScreen
import com.example.alarm_app.ui.MapScreen
import com.example.alarm_app.ui.SettingsScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier, context: Context) {
    val navItemList = listOf(
        NavItem("Alarms", Icons.Default.Notifications),
        NavItem("Map", Icons.Default.LocationOn),
        NavItem("Settings", Icons.Default.Settings),
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { i, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == i,
                        onClick = {
                            selectedIndex = i
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex, context)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, context: Context) {
    when (selectedIndex) {
        0 -> AlarmsScreen()
        1 -> MapScreen(context = context)
        2 -> SettingsScreen()
    }
}
