package com.example.chucknorrisfacts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHandler (context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME" +
                "($USERNAME TEXT PRIMARY KEY NOT NULL, $PASSWORD TEXT)"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }

    //Inserting (Creating) data
    fun addUser(user: Users): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USERNAME, user.username)
        values.put(PASSWORD, user.password)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    //get all users
    fun validateUser(user: String, pass: String): Users {
        var out: Users = Users()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var firstName = cursor.getString(cursor.getColumnIndex(USERNAME))
                    var lastName = cursor.getString(cursor.getColumnIndex(PASSWORD))

                    if(user.equals(firstName) && pass.equals(lastName))
                    {
                        out.username = firstName
                        out.password = lastName
                        break
                    }
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return out
    }

    fun updateUser(user: Users) {
        val db = this.writableDatabase
        var values = ContentValues()
        values.put(USERNAME, user.username)
        values.put(PASSWORD, user.password)

        val retVal = db.update(TABLE_NAME, values, "username = " + user.username, null)

        db.close()

    }

    companion object {
        private val DB_NAME = "UsersDB"
        private val DB_VERSIOM = 1;
        private val TABLE_NAME = "users"
        private val USERNAME = "username"
        private val PASSWORD = "password"
    }
}