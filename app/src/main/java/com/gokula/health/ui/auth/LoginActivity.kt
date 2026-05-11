package com.gokula.health.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gokula.health.databinding.ActivityLoginBinding
import com.gokula.health.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser != null) goToMain()

        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        if (!validate(email, pass)) return
        showLoading(true)
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { goToMain() }
            .addOnFailureListener { e ->
                showLoading(false)
                val msg = when {
                    e.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                        "Error: Email/Password login is not enabled in Firebase Console. Please enable it in Authentication -> Sign-in method."
                    else -> "Login failed: ${e.message}"
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        if (!validate(email, pass)) return
        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { goToMain() }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validate(email: String, pass: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        binding.tilEmail.error = null
        if (pass.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        binding.tilPassword.error = null
        return true
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.btnRegister.isEnabled = !show
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}