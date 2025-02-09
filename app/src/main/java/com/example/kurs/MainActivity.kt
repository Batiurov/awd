package com.example.kurs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

val supabase = createSupabaseClient(
    supabaseUrl = "https://lxwdtlholgduwuyevhsi.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx4d2R0bGhvbGdkdXd1eWV2aHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzcwMjYyMDQsImV4cCI6MjA1MjYwMjIwNH0.xygaSCqFvJiinJaz7-Drt6HzZkPHqfMEYl7z2b4DI58"
) {
    install(Postgrest)
}

@Serializable
data class TableEntry(
    val name: String = "",
    val Description: String = "",
    val Time: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Button(
                        onClick = {
                            val intent = Intent(this@MainActivity, SecondWindow::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Перейти на SecondWindow")
                    }

                    DataScreen()
                }
            }
        }
    }
}

@Composable
fun DataScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    var tableData by remember { mutableStateOf<List<TableEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val response = supabase.from("Table").select().decodeList<TableEntry>()
            tableData = response
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(tableData) { entry ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "Name: ${entry.name}", fontWeight = FontWeight.Bold)
                Text(text = "Description: ${entry.Description}")
                Text(text = "Time: ${entry.Time}")
                Divider()
            }
        }
    }
}
