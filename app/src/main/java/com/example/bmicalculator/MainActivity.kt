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

// Google Sign-In related imports
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge layout for modern UI
        enableEdgeToEdge()

        setContent {
            BMICalculatorTheme {

                // Application context for Toast messages
                val context = LocalContext.current

                // Firebase Authentication instance
                val auth = FirebaseAuth.getInstance()

                // Firestore database instance
                val db = FirebaseFirestore.getInstance()

                // Track authentication state
                var isLoggedIn by remember {
                    mutableStateOf(auth.currentUser != null)
                }

                // Handles Google Sign-In result
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = task.result

                        // Create Firebase credential from Google account
                        val credential =
                            GoogleAuthProvider.getCredential(account.idToken, null)

                        // Sign in to Firebase using Google credentials
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener {
                                isLoggedIn = true
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                // Google Sign-In configuration
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(
                        "447270742813-sivqd1gipv0fi0b253vqdhq0m29abdog.apps.googleusercontent.com"
                    )
                    .requestEmail()
                    .build()

                // Google Sign-In client
                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Show BMI screen if user is authenticated
                    if (isLoggedIn) {
                        BmiScreen(
                            userEmail = auth.currentUser?.email ?: "",
                            userId = auth.currentUser!!.uid,
                            db = db,
                            onLogoutClick = {
                                // Sign out from Google and Firebase
                                googleSignInClient.signOut()
                                auth.signOut()
                                isLoggedIn = false
                            }
                        )
                    } else {

                        // Show authentication screen if user is not logged in
                        AuthScreen(
                            onLoginClick = { email, password ->

                                // Validate empty input
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Email and password cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@AuthScreen
                                }

                                // Email/password sign-in
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

                                // Validate empty input
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Email and password cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@AuthScreen
                                }

                                // Create new user account
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

                            // Send password reset email
                            onForgotPasswordClick = { email ->
                                auth.sendPasswordResetEmail(email.trim())
                                Toast.makeText(
                                    context,
                                    "Password reset email sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            // Launch Google Sign-In flow
                            onGoogleLoginClick = {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}
