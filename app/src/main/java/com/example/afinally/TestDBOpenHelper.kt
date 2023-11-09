package com.example.afinally

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TestDBOpenHelper(context: Context, name:String, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(CREATE_TABLE)
        p0.execSQL(CREATE_TABLE_1)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        db?.execSQL(CREATE_TABLE)
        db?.execSQL(DROP_TABLE_1)
        db?.execSQL(CREATE_TABLE_1)
    }

    private val CREATE_TABLE: String = "create table expenseDetail(" +
            "Year integer," +
            "Month integer," +
            "Day integer," +
            "Description string," +
            "Expense double"+
            ")"
    private val DROP_TABLE: String = "drop table expenseDetail"


    private val CREATE_TABLE_1: String = "create table incomeDetail(" +
            "ID integer PRIMARY KEY AUTOINCREMENT," +
            "Year integer," +
            "Month integer," +
            "Income double"+
            ")"
    private val DROP_TABLE_1: String = "drop table expenseDetail"

}






