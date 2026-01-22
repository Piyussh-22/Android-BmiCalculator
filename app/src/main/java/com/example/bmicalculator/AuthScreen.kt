package com.example.bmicalculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider

@Composable
fun AuthScreen(
    // Callback for email/password login
    onLoginClick: (String, String) -> Unit,

    // Callback for new account registration
    onRegisterClick: (String, String) -> Unit,

    // Callback for password reset
    onForgotPasswordClick: (String) -> Unit,

    // Callback for Google Sign-In
    onGoogleLoginClick: () -> Unit
) {
    // State for user input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Screen title
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password input field (masked)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Register new account button
        OutlinedButton(
            onClick = { onRegisterClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Password reset option
        TextButton(
            onClick = { onForgotPasswordClick(email) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider between email login and Google login
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In button
        Button(
            onClick = onGoogleLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }
    }
}
