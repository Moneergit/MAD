package com.example.receiptsaver.ui

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.model.auth
import com.example.receiptsaver.ui.theme.ReceiptSaverTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Vis succesmeddelelse, hvis der er en
        successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Vis fejlmeddelelse, hvis der er en
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val trimmedUsername = username.trim()
                Log.d("SignUpScreen", "Email after trim: '$trimmedEmail'")
                Log.d("SignUpScreen", "Username after trim: '$trimmedUsername'")
                when {
                    trimmedUsername.isBlank() -> {
                        errorMessage = "Udfyld venligst brugernavn"
                    }
                    trimmedEmail.isBlank() -> {
                        errorMessage = "Udfyld venligst email"
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                        errorMessage = "Ugyldig email-adresse"
                    }
                    password.isBlank() -> {
                        errorMessage = "Udfyld venligst password"
                    }
                    password.length < 6 -> {
                        errorMessage = "Password skal være mindst 6 tegn"
                    }
                    else -> {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        auth.createUserWithEmailAndPassword(trimmedEmail, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Brugeroprettelse succesfuld
                                    successMessage = "Bruger oprettet succesfuldt!"
                                    val user = auth.currentUser
                                    val userData = hashMapOf(
                                        "username" to trimmedUsername,
                                        "email" to trimmedEmail
                                    )
                                    user?.let {
                                        db.collection("users").document(it.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Log.d("SignUpScreen", "Brugerdata gemt i Firestore for UID: ${user.uid}")
                                            }
                                            .addOnFailureListener { e ->
                                                errorMessage = "Kunne ikke gemme brugerdata: ${e.message}"
                                                Log.e("SignUpScreen", "Fejl ved gemning i Firestore: ${e.message}")
                                            }
                                    } ?: run {
                                        errorMessage = "Brugeroprettelse fejlede: Kunne ikke hente brugerdata"
                                    }
                                    // Naviger til EmptyScreen, uanset om Firestore fejler
                                    navController.navigate("empty") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Signup fejlede"
                                    Log.e("SignUpScreen", "Signup fejlede: ${task.exception?.message}")
                                }
                                isLoading = false
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Already have an account? Login")
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun SignUpScreenPreview() {
    ReceiptSaverTheme {
        SignUpScreen(navController = rememberNavController())
    }
}