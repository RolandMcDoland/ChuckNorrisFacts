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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        randomButton.setOnClickListener {
            CommunicationController().execute("https://api.chucknorris.io/jokes/random")
        }

        searchButton.setOnClickListener {
            CommunicationController().execute("https://api.chucknorris.io/jokes/search?query=" + searchEditText.text)
        }

        //Log the user out
        logoutButton.setOnClickListener{
            val fileName = "UserData.txt"
            val file = File(filesDir, fileName)
            file.delete()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
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
