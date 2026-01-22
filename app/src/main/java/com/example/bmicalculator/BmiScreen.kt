package com.example.bmicalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BmiScreen(
    userEmail: String,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top row: Welcome + Logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.titleMedium
            )

            Button(onClick = onLogoutClick) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        // BMI section
        Text(
            text = "BMI Calculator",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        BmiCalculator()
    }
}


@Composable
fun BmiCalculator(modifier: Modifier = Modifier){
    val weightUnit = remember {mutableStateOf("kg")}
    val heightUnit = remember {mutableStateOf("cm")}
    val weight = remember { mutableStateOf("") }
    val height = remember { mutableStateOf("")}
    val gender = remember {mutableStateOf("male")}
    val bmiResult = remember{ mutableStateOf("")}


    Column(modifier = modifier) {

        Text(text = "BMI Tracker")

        OutlinedTextField(
            value = weight.value,
            onValueChange = { weight.value = it },
            label = { Text("Weight") },
        )

        Row{
            RadioButton(
                selected = weightUnit.value == "kg",
                onClick = {weightUnit.value = "kg"}
            )
            Text("kg")
            RadioButton(
                selected = weightUnit.value == "lbs",
                onClick = {weightUnit.value = "lbs"}
            )
            Text("lbs")
        }

        OutlinedTextField(
            value = height.value,
            onValueChange = { height.value = it },
            label = {Text("Height")}

        )
        Row{
            RadioButton(
                selected = heightUnit.value == "cm",
                onClick = {heightUnit.value = "cm"}
            )
            Text("cm")
            RadioButton(
                selected = heightUnit.value == "inch",
                onClick = {heightUnit.value = "inch"}
            )
            Text("inch")
        }

        Text(text = "Gender")
        Row{
            RadioButton(
                selected = gender.value == "Male",
                onClick = { gender.value = "Male" }
            )
            Text (text = "Male")
        }
        Row{
            RadioButton(
                selected = gender.value == "Female",
                onClick = { gender.value = "Female" }
            )
            Text (text = "Female")
        }
        Row{
            RadioButton(
                selected = gender.value == "Other",
                onClick = { gender.value = "Other"}
            )
            Text(text = "Other")
        }

        Button(onClick = {
            val wInput = weight.value.toFloatOrNull()
            val hInput = height.value.toFloatOrNull()

            if (wInput == null || hInput == null) {
                bmiResult.value = "Please enter valid numbers"
            }else{
                val weightInKg = if(weightUnit.value == "lbs") {
                    wInput * 0.45359237F
                }else{
                    wInput
                }

                val heightInCm = if(heightUnit.value == "inch"){
                    hInput * 2.54F
                }else{
                    hInput
                }
                val heightInMeters = heightInCm / 100
                val bmi = weightInKg / (heightInMeters * heightInMeters)

                val category = when {
                    bmi < 18.5 -> "Underweight"
                    bmi < 24.9 -> "Normal weight"
                    bmi < 29.9 -> "Overweight"
                    else -> "Obese"
                }
                bmiResult.value =
                    "BMI: ${String.format("%.2f", bmi)}\nCategory: $category"
            }

        }) {
            Text("Calculate BMI")
        }
        if(bmiResult.value.isNotEmpty()){
            Text(text = bmiResult.value)
        }
    }
}