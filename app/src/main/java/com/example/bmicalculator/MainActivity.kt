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

// Google login imports
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import androidx.activity.compose.rememberLauncherForActivityResult

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
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = task.result
                        val credential =
                            GoogleAuthProvider.getCredential(account.idToken, null)

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

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(
                        "447270742813-sivqd1gipv0fi0b253vqdhq0m29abdog.apps.googleusercontent.com"
                    )
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        BmiScreen(
                            userEmail = auth.currentUser?.email ?: "",
                            onLogoutClick = {
                                googleSignInClient.signOut()
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
                            },
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
