package com.example.kurs


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SecondWindow : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddDataScreen()
        }
    }
}

@Composable
fun AddDataScreen() {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(LocalDate.now()) } // Используем LocalDate вместо LocalTime
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Добавить данные", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        DatePickerField(selectedDate = date) { newDate ->  // Обновляем DatePicker
            date = newDate
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val data = mapOf(
                            "name" to name.text,
                            "Description" to description.text,
                            "Time" to date.toString() // Преобразование LocalDate в строку для столбца Time
                        )

                        supabase.from("Table").insert(data) // Запрос выполняется, ошибки обрабатываются в catch

                        successMessage = "Данные успешно добавлены!"
                        name = TextFieldValue("")
                        description = TextFieldValue("")

                    } catch (e: Exception) {
                        successMessage = "Ошибка при добавлении данных: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить данные")
        }

        Spacer(modifier = Modifier.height(8.dp))

        successMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun DatePickerField(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val formattedDate = remember(selectedDate) { selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    OutlinedButton(onClick = {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                onDateSelected(LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)) // LocalDate использует 1-based месяц
            },
            year,
            month,
            day
        ).show()
    }, modifier = Modifier.fillMaxWidth()) {
        Text("Выбрать дату: $formattedDate")
    }
}
