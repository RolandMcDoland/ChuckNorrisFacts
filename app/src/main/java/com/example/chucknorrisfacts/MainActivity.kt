package com.example.chucknorrisfacts

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var dbHandler: DatabaseHandler? = null

    var username: String = ""
    var password: String = ""

    val fileName: String = "UserData.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUser()

        saveFavouriteButton.isEnabled = false

        //init db
        dbHandler = DatabaseHandler(this)

        randomButton.setOnClickListener {
            CommunicationController().execute("https://api.chucknorris.io/jokes/random")

            saveFavouriteButton.isEnabled = true
        }

        searchButton.setOnClickListener {
            CommunicationController().execute("https://api.chucknorris.io/jokes/search?query=" + searchEditText.text)

            saveFavouriteButton.isEnabled = true
        }

        // Get favourite fact of current user
        favouriteButton.setOnClickListener {
            factTextView.text = dbHandler!!.getFavouriteFact(username)
        }

        // Save current fact as the new favourite for current user
        saveFavouriteButton.setOnClickListener {
            var user = Users()

            user.username = username
            user.password = password
            user.favouriteFact = factTextView.text.toString()

            dbHandler!!.updateUser(user)
        }

        //Log the user out
        logoutButton.setOnClickListener{
            val file = File(filesDir, fileName)
            file.delete()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Get username from file
    private fun getUser() {
        val file = File(filesDir, fileName)

        var input = file.bufferedReader().readLines()
        var data = input[0].split(",")

        username = data[0]
        password = data[1]
    }

    //Get a response from specified URL
    inner class CommunicationController : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            var response: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                response = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            handleJSON(result)
        }
    }

    //Handle JSON
    private fun handleJSON(result: String?) {
        val factJson = JSONObject(result)

        val fact:Any
        if(factJson.has("value")) {
            fact = factJson.get("value")
        }
        else {
            if(factJson.get("total").toString().toInt() != 0) {
                val factArray = factJson.get("result")
                fact = JSONArray(factArray.toString()).getJSONObject((0..(factJson.get("total").toString().toInt() - 1)).random()).get("value")
            }
            else {
                fact = "No results found!"
            }
        }

        //Put the fact in the proper textView
        factTextView.text = fact.toString()
    }
}
