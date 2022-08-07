package com.example.mobilecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    private fun userLoginValidation() {
        val user=userName.text.toString()
        val pass=password.text.toString()

        if(user == "admin" && pass=="admin"){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("user", user)
                putExtra("password",pass)
            }
            startActivity(intent)
            finish()
        }
        else{
            val toast = Toast.makeText(applicationContext, "make login using admin", Toast.LENGTH_SHORT )
            toast.show()
        }

    }

    fun verifyCredentials(view: View) {
//        Log.d("on click problem","user ")
        try {
            userLoginValidation()
        } catch (exc: Exception ){
            Log.d("on click problem",exc.toString())
        }

    }
}