package com.example.projectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var viewDescription: TextView
    private lateinit var viewEmail: EditText
    private lateinit var viewPassword: EditText
    private lateinit var viewButton: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        viewDescription = findViewById(R.id.view_description)
        viewEmail = findViewById(R.id.input_email)
        viewPassword = findViewById(R.id.input_password)
        viewButton = findViewById(R.id.button_submit)

        viewEmail.addTextChangedListener { checkValidation() }
        viewPassword.addTextChangedListener { checkValidation() }

        viewDescription.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            finish()
            startActivity(intent)
        }
        viewButton.setOnClickListener {
            val emailValue = viewEmail.text.toString()
            val passwordValue = viewPassword.text.toString()

            login(emailValue, passwordValue)
        }
    }

    private fun checkValidation() : Boolean {
        val emailValue = viewEmail.text.toString()
        val emailValidity = !TextUtils.isEmpty(emailValue) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()

        val passwordValue = viewPassword.text.toString()
        val passwordValidity = !TextUtils.isEmpty(passwordValue)

        val valid = emailValidity && passwordValidity
        viewButton.isEnabled = valid
        viewButton.isClickable = valid

        return valid
    }
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, "Email or password incorrect.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}