package com.example.bmicalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bmicalculator.ui.theme.BMICalculatorTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMICalculatorTheme {
                val context = LocalContext.current
                val auth = FirebaseAuth.getInstance()
                var isLoggedIn by remember {
                    mutableStateOf(auth.currentUser != null)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        BmiScreen(
                            userEmail = auth.currentUser?.email ?: "",
                            onLogoutClick = {
                                auth.signOut()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        AuthScreen(
                            onLoginClick = { email, password ->
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Email and password cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@AuthScreen
                                }
                                auth.signInWithEmailAndPassword(
                                    email.trim(),
                                    password.trim()
                                ).addOnSuccessListener {
                                    isLoggedIn = true
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onRegisterClick = { email, password ->
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Email and password cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@AuthScreen
                                }
                                auth.createUserWithEmailAndPassword(
                                    email.trim(),
                                    password.trim()
                                ).addOnSuccessListener {
                                    isLoggedIn = true
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onForgotPasswordClick = { email ->
                                auth.sendPasswordResetEmail(email.trim())
                                Toast.makeText(
                                    context,
                                    "Password reset email sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}