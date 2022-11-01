package com.abdnezar.unitonequiz.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdnezar.unitonequiz.R
import com.abdnezar.unitonequiz.databinding.FragmentLoginBinding
import com.abdnezar.unitonequiz.utils.Helper.Companion.COUNTRY_PHONE_CODE
import com.abdnezar.unitonequiz.utils.Helper.Companion.log
import com.abdnezar.unitonequiz.utils.Helper.Companion.toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(R.layout.fragment_login) {
    val TAG = this.javaClass.simpleName
    private lateinit var binding : FragmentLoginBinding
    private var _binding : FragmentLoginBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        _binding = binding
    }

    override fun onResume() {
        super.onResume()

        binding.tvLogin.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()

            if (phone.length != 9) {
                toast(getString(R.string.enter_valid_phone))
                return@setOnClickListener
            }

            if (!phone.startsWith("59") && !phone.startsWith("56")) {
                toast(getString(R.string.should_start_with_valid_suffix_number))
                return@setOnClickListener
            }

            binding.tvLogin.isEnabled = false
            binding.pb.visibility = View.VISIBLE

            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(COUNTRY_PHONE_CODE + phone)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        it.isEnabled = true
                        binding.pb.visibility = View.GONE
                        binding.tvLogin.text = "Verify"
                        log(TAG, "onVerificationCompleted:$credential")
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        it.isEnabled = true
                        binding.pb.visibility = View.GONE

                        binding.tvLogin.text = "verify"
                        log(TAG, "onCodeSent: $verificationId")

                        toast("sms sent...")
                        binding.flOtp.visibility = View.VISIBLE

                        binding.tvLogin.setOnClickListener {
                            val code = binding.otpView.otp
                            if (code != null) {
                                if (code.isNotEmpty()) {
                                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                                    signInWithPhoneAuthCredential(credential)
                                } else {
                                    toast("enter code")
                                }
                            }
                        }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        it.isEnabled = true
                        binding.pb.visibility = View.GONE
                        binding.tvLogin.text = "send verification code"
                        log(TAG, "onVerificationFailed $e")

                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                toast("verify failed: " + e.message)
                            }
                            is FirebaseTooManyRequestsException -> {
                                toast("sms quota exceeded")
                            }
                            else -> {
                                toast("login failed" + e.message)
                            }
                        }
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                log(TAG, "signInWithCredential:success")

                val user = task.result?.user
                log(TAG, "user: $user")
                toast("Welcome ${user?.phoneNumber}")
                findNavController().popBackStack()
            } else {
                log(TAG, "signInWithCredential:failure ${task.exception}")
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    toast("Sms code is invalid")
                } else {
                    toast("login failed")
                    log(TAG, "signInWithCredential:failure ${task.exception}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}