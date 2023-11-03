package com.example.afinally

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TestDBOpenHelper(context: Context, name:String, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(p0: SQLiteDatabase) {
        p0?.execSQL(CREATE_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        db?.execSQL(CREATE_TABLE)
    }

    private val CREATE_TABLE: String = "create table expenseDetail(" +
            "ID integer primary key autoincrement," +
            "Year integer," +
            "Month integer," +
            "Description string," +
            "Expense double"+
            ")"
    private val DROP_TABLE: String = "drop table expenseDetail"



}






