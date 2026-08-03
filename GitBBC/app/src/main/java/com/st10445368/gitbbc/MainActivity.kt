package com.st10445368.gitbbc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.st10445368.gitbbc.auth.BiometricAuthManager
import com.st10445368.gitbbc.auth.BiometricPreferencesRepository
import com.st10445368.gitbbc.auth.GoogleAuthClient
import com.st10445368.gitbbc.navigation.MainAppContainer
import com.st10445368.gitbbc.ui.Screens.AuthScreen
import com.st10445368.gitbbc.ui.Screens.BiometricLockScreen
import com.st10445368.gitbbc.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleAuthClient = GoogleAuthClient(applicationContext)
        val authViewModel = AuthViewModel(googleAuthClient)
        val biometricRepo = BiometricPreferencesRepository(applicationContext)
        val biometricManager = BiometricAuthManager(this)

        setContent {
            val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            // FIX: Default initialValue to false instead of null so it doesn't block the UI
            val biometricEnabled by biometricRepo.enabledRaw.collectAsStateWithLifecycle(initialValue = false)

            var sessionUnlocked by rememberSaveable { mutableStateOf(false) }

            Surface(modifier = Modifier.fillMaxSize()) {
                when {
                    // 1. Unauthenticated State
                    currentUser == null -> {
                        AuthScreen(
                            onSignInClick = {
                                authViewModel.signIn(
                                    onSuccess = { sessionUnlocked = true },
                                    onError = { /* Handle error feedback */ }
                                )
                            }
                        )
                    }

                    // 2. Biometric Lock State
                    biometricEnabled == true && !sessionUnlocked -> {
                        BiometricLockScreen(
                            onUnlockClick = {
                                biometricManager.promptBiometric(
                                    onSuccess = { sessionUnlocked = true },
                                    onError = { /* Handle error feedback */ }
                                )
                            },
                            onSignOutClick = {
                                authViewModel.signOut()
                                sessionUnlocked = false
                            }
                        )
                    }

                    // 3. Fully Authenticated and Unlocked
                    else -> {
                        MainAppContainer(
                            onSignOut = {
                                authViewModel.signOut()
                                sessionUnlocked = false
                            },
                            biometricEnabled = biometricEnabled ?: false,
                            onToggleBiometric = { enabled ->
                                if (enabled) {
                                    biometricManager.promptBiometric(
                                        title = "Enable Biometrics",
                                        onSuccess = {
                                            lifecycleScope.launch {
                                                biometricRepo.setEnabled(true)
                                            }
                                        },
                                        onError = { }
                                    )
                                } else {
                                    lifecycleScope.launch {
                                        biometricRepo.setEnabled(false)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}