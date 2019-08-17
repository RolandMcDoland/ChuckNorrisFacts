package com.example.chucknorrisfacts

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
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
            val factArray = factJson.get("result")
            fact = JSONArray(factArray.toString()).getJSONObject(0).get("value")
        }

        //Put the fact in the proper textView
        factTextView.text = fact.toString()
    }
}
