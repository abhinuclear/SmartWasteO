package com.project.smartwasteo

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthViewModel : ViewModel() {

    var verificationId = mutableStateOf("")
    var otpSent = mutableStateOf(false)
    var isLoading = mutableStateOf(false)

    var errorMessage = mutableStateOf<String?>(null)


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendOtp(phoneNumber: String, context: Context) {
        isLoading.value = true
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    errorMessage.value = e.localizedMessage
                    isLoading.value = false
                }

                override fun onCodeSent(verificationIdStr: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId.value = verificationIdStr
                    otpSent.value = true
                    isLoading.value = false
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String, onSuccess: () -> Unit) {
      //  _isLoading.value = true
       // _errorMessage.value = null
        val credential = PhoneAuthProvider.getCredential(verificationId.value, code)
        signInWithPhoneAuthCredential(credential, onSuccess)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, onSuccess: (() -> Unit)? = null) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess?.invoke()
                } else {
                    errorMessage.value = task.exception?.localizedMessage
                }
            }
    }
}
