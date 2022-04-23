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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewDescription: TextView
    private lateinit var viewUsername: EditText
    private lateinit var viewEmail: EditText
    private lateinit var viewPassword: EditText
    private lateinit var viewPasswordConfirm: EditText
    private lateinit var viewButton: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        viewDescription = findViewById(R.id.view_description)
        viewUsername = findViewById(R.id.input_username)
        viewEmail = findViewById(R.id.input_email)
        viewPassword = findViewById(R.id.input_password)
        viewPasswordConfirm = findViewById(R.id.input_confirm)
        viewButton = findViewById(R.id.button_submit)

        viewUsername.addTextChangedListener { checkValidation() }
        viewEmail.addTextChangedListener { checkValidation() }
        viewPassword.addTextChangedListener { checkValidation() }
        viewPasswordConfirm.addTextChangedListener { checkValidation() }

        viewDescription.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }
        viewButton.setOnClickListener {
            val usernameValue = viewUsername.text.toString()
            val emailValue = viewEmail.text.toString()
            val passwordValue = viewPassword.text.toString()

            register(usernameValue, emailValue, passwordValue)
        }
    }

    private fun checkValidation() : Boolean {
        val usernameValue = viewUsername.text.toString()
        val usernameValidity = !TextUtils.isEmpty(usernameValue)

        val emailValue = viewEmail.text.toString()
        val emailValidity = !TextUtils.isEmpty(emailValue) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()

        val passwordValue = viewPassword.text.toString()
        val confirmValue = viewPasswordConfirm.text.toString()
        val passwordValidity = !TextUtils.isEmpty(passwordValue) && passwordValue == confirmValue

        val valid = usernameValidity && emailValidity && passwordValidity
        viewButton.isEnabled = valid
        viewButton.isClickable = valid

        return valid
    }
    private fun validateUsername(username: String) : String? {
        val length = TextUtils.getTrimmedLength(username)

        if (length < 4) return "Username need to contains at least 4 char."
        if (length > 32) return "Username need to contains at most 32 char."

        return null
    }
    private fun validatePassword(password: String) : String? {
        val length = TextUtils.getTrimmedLength(password)

        if (length < 6) return "Password need to contains at least 6 char."
        if (length > 32) return "Password need to contains at most 32 char."

        return null
    }

    private fun register(username: String, email: String, password: String) {
        val usernameValidity = validateUsername(username)
        if (usernameValidity !== null) return Toast.makeText(this@RegisterActivity, usernameValidity, Toast.LENGTH_SHORT).show()

        val passwordValidity = validatePassword(password)
        if (passwordValidity !== null) return Toast.makeText(this@RegisterActivity, passwordValidity, Toast.LENGTH_SHORT).show()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                addUser(username, email, mAuth.currentUser?.uid!!)
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                Toast.makeText(this@RegisterActivity, "An error has occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUser(username: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase
            .getInstance("https://kotlinproject-73b65-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/")
        mDbRef.child("users").child(uid).setValue(User(uid, username, email, null, null))
    }
}