package com.example.chucknorrisfacts

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.io.FileOutputStream

class LoginActivity : AppCompatActivity() {
    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fileName = "UserData.txt"
        val file = File(filesDir, fileName)
        if(!file.exists()){
            file.createNewFile()
        }

        //init db
        dbHandler = DatabaseHandler(this)

        loginButton.setOnClickListener(){
            if(validation()) {
                var username = usernameEditText.text.toString()
                var password = passwordEditText.text.toString()
                var user = dbHandler!!.validateUser(username, password)
                if(!user.username.equals("")){
                    //save looged user in cache
                    val fileOutputStream: FileOutputStream
                    try {
                        fileOutputStream = openFileOutput(fileName, MODE_PRIVATE)
                        fileOutputStream.write((user.username+",").toByteArray())
                        fileOutputStream.write((user.password).toByteArray())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val thread = Thread() {
                        run {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    thread.start()
                }
                else{
                    Toast.makeText(this,"Wrong Username or Password", Toast.LENGTH_LONG).show()
                }
            }
        }

        registerButton.setOnClickListener(){
            var user: Users = Users()
            if(validation()) {
                var success: Boolean = false
                user.username = usernameEditText.text.toString()
                user.password = passwordEditText.text.toString()
                success = dbHandler!!.addUser(user)

                if (success){
                    Toast.makeText(this,"Saved Successfully", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun validation(): Boolean{
        var validate = false

        if (!usernameEditText.text.toString().equals("") && !passwordEditText.text.toString().equals("")){
            validate = true
        }else{
            validate = false
            Toast.makeText(this,"Fill all details", Toast.LENGTH_LONG).show()
        }

        return validate
    }
}
