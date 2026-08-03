package com.st10445368.gitbbc.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.st10445368.gitbbc.auth.GoogleAuthClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val googleAuthClient: GoogleAuthClient
) : ViewModel() {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    fun signIn(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            googleAuthClient.signIn()
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Sign in failed") }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthClient.signOut()
        }
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
}