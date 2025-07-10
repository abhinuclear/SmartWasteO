package com.project.smartwasteo

import android.view.View
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth =FirebaseAuth.getInstance()
   // auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
   // FirebaseAuth.getInstance().firebaseAuthSettings.setAppVerificationDisabledForTesting(true)


    init{
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value=AuthState.unauthenticated
        }else{
            _authState.value=AuthState.Authenticated
        }
    }
    fun login(email:String,password: String){
        if(email.isEmpty()||password.isEmpty()){
            _authState.value=AuthState.Error("Email or Password can't be empty")
            return

        }
        _authState.value=AuthState.Loading

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _authState.value=AuthState.Authenticated
                }else{
                    _authState.value=AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }


        fun signout() {
            FirebaseAuth.getInstance().signOut()
            _authState.value = AuthState.unauthenticated
        }


    }

    sealed class  AuthState{
        object Authenticated: AuthState()
        object unauthenticated: AuthState()
        object Loading: AuthState()
        data class Error(val message:String) : AuthState()

    }

}



