package com.example.afinally

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {


                Text("The second activity was started ${started.value} time(s)")
                Text("This is the main activity")
                Text("------------------------------------------------------------------")
                // composable function that shows a sheet when click a button
                Button(onClick = { showSheetAddWindow.value = true }) { Text("+Add New Expense Sheet") }
                    if (showSheetAddWindow.value) { SheetAddWindow(onDismiss = { showSheetAddWindow.value = false }) }

                Spacer(modifier= Modifier.width(16.dp))

                LazyColumn{
                    for (i in 0 until items_list.size) {
                        item {
                            Row(modifier=Modifier.padding(20.dp)){
                                Text ( "sheet item $i:${items_list[i]}" )
                                Spacer(modifier= Modifier.width(8.dp))
                                Icon(imageVector = Icons.Filled.Edit, contentDescription ="Edit",Modifier.clickable {
                                    startActivity(createIntentSecondActivity()) } )
                                Spacer(modifier= Modifier.width(8.dp))
                                Icon(imageVector = Icons.Filled.Delete, contentDescription ="Delete", Modifier.clickable {
                                    items_list.remove(items_list[i])})

                            }
                        }
                    }
                }
            }
         }
        tdb = TestDBOpenHelper(this, "test.db", null, 1)
        sdb = tdb.writableDatabase

      }


    var showSheetAddWindow = mutableStateOf(false)
    var started = mutableStateOf(0)

    private fun createIntentSecondActivity(): Intent {
        var intent  = Intent( this,SecondActivity::class.java)
        intent. putExtra( "transferYearToSecond","${user_input_year.value} " )
        intent. putExtra( "transferMonthToSecond"," ${user_input_month.value}" )
        started.value++
        return intent
    }




    private fun updateData() {
        val row: ContentValues = ContentValues().apply {
            put("Description", "rent")
        }

        var table = "test"
        var where = "Description= ?"
        var where_args: Array<String> = arrayOf("food")
        sdb.update(table, row, where, where_args)

    }

    private fun retrieveData(): String {

        val table_name = "test"
        val columns: Array<String> = arrayOf("ID", "Year", "Month", "Description","Expense")
        val where: String? = null
        val where_args: Array<String>? = null
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null

        var c: Cursor =
            sdb.query(table_name, columns, where, where_args, group_by, having, order_by)

        var sb: StringBuilder = StringBuilder()
        c.moveToFirst()
        for (i in 0 until c.count) {
            sb.append(c.getInt(0).toString())
            sb.append("")
            sb.append(c.getString(1).toString())
            sb.append("")
            sb.append(c.getString(2).toString())
            sb.append("")
            sb.append(c.getString(3).toString())
            sb.append("\n")
            c.moveToNext()
        }

        return sb.toString()
    }


    private lateinit var tdb: TestDBOpenHelper
    private lateinit var sdb: SQLiteDatabase



}



var items_list = mutableStateListOf<String>()
var user_input_month = mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)+1)
var user_input_year = mutableStateOf(Calendar.getInstance().get(Calendar.YEAR))

@ExperimentalMaterial3Api
@Composable
fun SheetAddWindow(onDismiss:() -> Unit) {

    Column(modifier = Modifier.padding(50.dp)) {

        OutlinedTextField(value=user_input_month.value.toString(), onValueChange = { user_input_month.value = it.toInt() },label={Text("Enter month from 1-12")})


        TextField(value=user_input_year.value.toString(), onValueChange = { user_input_year.value = it.toInt() },label={Text("Enter year ")})

        Button(onClick = {
            items_list.add("${user_input_year.value} ${user_input_month.value}")
            onDismiss()
        }) {
            Text("Confirm")
        }
    }

}

