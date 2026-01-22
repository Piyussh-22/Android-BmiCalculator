package com.example.bmicalculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun BmiScreen(
    userEmail: String,
    userId: String,
    db: FirebaseFirestore,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Handles proper spacing for system status bar
            .statusBarsPadding()
            // Allows scrolling for smaller screens
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // Header section showing user info and logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.titleMedium
                )
                // Display logged-in user's email
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Logout button
            Button(onClick = onLogoutClick) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main BMI calculator card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BMI Calculator",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // BMI calculation and history section
                BmiCalculator(
                    userId = userId,
                    db = db
                )
            }
        }
    }
}

@Composable
fun BmiCalculator(
    userId: String,
    db: FirebaseFirestore,
    modifier: Modifier = Modifier
) {
    // State for unit selection and input fields
    val weightUnit = remember { mutableStateOf("kg") }
    val heightUnit = remember { mutableStateOf("cm") }
    val weight = remember { mutableStateOf("") }
    val height = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("Male") }

    // State for BMI result and history
    val bmiResult = remember { mutableStateOf("") }
    val bmiHistory = remember { mutableStateOf(listOf<String>()) }

    // Load saved user data from Firestore
    LaunchedEffect(userId) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Restore previously saved values
                    doc.getDouble("weight")?.let { weight.value = it.toString() }
                    doc.getDouble("heightCm")?.let { height.value = it.toString() }
                    doc.getString("gender")?.let { gender.value = it }

                    // Load BMI history only once to avoid overwriting UI state
                    if (bmiHistory.value.isEmpty()) {
                        bmiHistory.value =
                            doc.get("bmiHistory") as? List<String> ?: emptyList()
                    }
                }
            }
    }

    Column(modifier = modifier) {

        Text(
            text = "Enter your details",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Weight input
        OutlinedTextField(
            value = weight.value,
            onValueChange = { weight.value = it },
            label = { Text("Weight") },
            modifier = Modifier.fillMaxWidth()
        )

        // Weight unit selection
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = weightUnit.value == "kg",
                onClick = { weightUnit.value = "kg" }
            )
            Text("kg")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = weightUnit.value == "lbs",
                onClick = { weightUnit.value = "lbs" }
            )
            Text("lbs")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Height input
        OutlinedTextField(
            value = height.value,
            onValueChange = { height.value = it },
            label = { Text("Height") },
            modifier = Modifier.fillMaxWidth()
        )

        // Height unit selection
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = heightUnit.value == "cm",
                onClick = { heightUnit.value = "cm" }
            )
            Text("cm")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = heightUnit.value == "inch",
                onClick = { heightUnit.value = "inch" }
            )
            Text("inch")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Gender selection
        Text("Gender", style = MaterialTheme.typography.labelLarge)

        Row {
            listOf("Male", "Female", "Other").forEach {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = gender.value == it,
                        onClick = { gender.value = it }
                    )
                    Text(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI calculation button
        Button(
            onClick = {
                val w = weight.value.toFloatOrNull()
                val h = height.value.toFloatOrNull()

                // Validate numeric input
                if (w == null || h == null) {
                    bmiResult.value = "Please enter valid numbers"
                    return@Button
                }

                // Convert units to kg and cm if required
                val weightKg =
                    if (weightUnit.value == "lbs") w * 0.45359237f else w
                val heightCm =
                    if (heightUnit.value == "inch") h * 2.54f else h

                // BMI formula calculation
                val bmi = weightKg / ((heightCm / 100) * (heightCm / 100))

                // Determine BMI category
                val category = when {
                    bmi < 18.5 -> "Underweight"
                    bmi < 24.9 -> "Normal"
                    bmi < 29.9 -> "Overweight"
                    else -> "Obese"
                }

                // Prepare history entry
                val entry = "BMI ${String.format("%.2f", bmi)} - $category"
                val userRef = db.collection("users").document(userId)

                // Keep only the last 5 BMI records
                val newHistory = (listOf(entry) + bmiHistory.value).take(5)

                // Update UI immediately
                bmiHistory.value = newHistory

                // Save updated data to Firestore
                userRef.set(
                    mapOf(
                        "weight" to weightKg,
                        "heightCm" to heightCm,
                        "gender" to gender.value,
                        "bmiHistory" to newHistory
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                )

                // Display BMI result
                bmiResult.value =
                    "BMI: ${String.format("%.2f", bmi)}\nCategory: $category"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate BMI")
        }

        // BMI result display
        if (bmiResult.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = bmiResult.value,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show graph only if enough data points exist
        if (bmiHistory.value.size < 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Add at least 2 BMI entries to view the graph",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            BmiLineChart(bmiHistory.value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI history list
        if (bmiHistory.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Last 5 BMI Scores",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Table header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BMI",
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "Category",
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display BMI history rows
                    bmiHistory.value.forEach { item ->
                        val parts = item.split("-")

                        val bmiText =
                            parts.getOrNull(0)?.replace("BMI", "")?.trim() ?: ""
                        val categoryText = parts.getOrNull(1)?.trim() ?: ""

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = bmiText,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = categoryText,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BmiLineChart(bmiHistory: List<String>) {

    // Prevent chart crash when data points are insufficient
    if (bmiHistory.size < 2) return

    val entries = mutableListOf<Entry>()

    // Convert BMI history into chart entries
    bmiHistory.forEach { item ->
        val parts = item.split("-")
        if (parts.size < 2) return@forEach

        val bmiValue = parts[0]
            .replace("BMI", "")
            .trim()
            .toFloatOrNull()
            ?: return@forEach

        entries.add(Entry(entries.size.toFloat(), bmiValue))
    }

    if (entries.size < 2) return

    // Render line chart using MPAndroidChart
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(true)
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "BMI Trend").apply {
                lineWidth = 2f
                setDrawCircles(true)
                setDrawValues(false)
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}
