package com.example.afinally

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        //connect to database first
        tdb = TestDBOpenHelper(this, "test", null, 1)
        sdb = tdb.writableDatabase

        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text("The second activity was started ${started.value} time(s)")
                Text("This is the main activity")
                Text("------------------------------------------------------------------")

                // composable function that shows a sheet when click a button
                Button(onClick = {
                    showSheetAddWindow.value = true
                }) { Text("+Add New Expense Sheet") }
                if (showSheetAddWindow.value) {
                    SheetAddWindow(onDismiss = { showSheetAddWindow.value = false })
                }

                Spacer(modifier = Modifier.width(16.dp))

                //display a lazylist for sheet of different year month,delete function and edit function
                //this delete function only recompose items_list and has nothing to do with database sheet data
                //this edit function can lead to different expense page according to respective year and month
                LazyColumn {
                    for (i in 0 until items_list.size) {
                        item {
                            Row(modifier = Modifier.padding(20.dp)) {
                                Text("sheet item $i:${items_list[i]}")
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    Modifier.clickable {
                                        val splitValues = items_list[i].split(" ")
                                        val yearTime = splitValues[0].trim()
                                        val monthTime = splitValues[1].trim()
                                        startActivity(createIntentSecondActivity(yearTime,monthTime))
                                    })
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    Modifier.clickable {
                                        items_list.remove(items_list[i])
                                    })

                            }
                        }
                    }
                }


                Spacer(modifier= Modifier.width(16.dp))


                //retrieve the sheet from database ,which has at least one expense items for that month year,edit function & delete function
                //edit function will lead to explicit expense items  and income of that year month
                //delete function can delete all the expense items of regarding year month from the database
                Button(onClick = {current_sheet.value = retrieveSheet()}){
                    Text("retrieve current sheet item :")
                }
                if (current_sheet.value.isEmpty()) {
                    Text(text = "")
                } else {
                    LazyColumn {
                        val items = current_sheet.value.split("\n")
                        items.forEach { item ->
                            val parts = item.split(",")
                            if (parts.size >= 2) {
                                val year = parts[0]
                                val month = parts[1]
                                item {
                                    Row {
                                        Text(text = "Year: $year ")
                                        Text(text = "Month: $month ")
                                        Icon(imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.clickable {
                                                startActivity(createIntentSecondActivity(year,month))
                                            }
                                        )
                                        Icon(imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.clickable {
                                               deleteSheet(year,month)
                                                current_sheet.value = retrieveSheet()
                                            }
                                        )
                                    }

                                }
                            }
                        }

                    }
                }


            }
        }

    }


     //create an intent to provide year and time value for each sheet corresponding expense item page
    private fun createIntentSecondActivity(yearTime:String,monthTime:String): Intent {
        var intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("transferYearToSecond", "${yearTime }")
        intent.putExtra("transferMonthToSecond", " ${monthTime}")
        started.value++
        return intent
    }

    var showSheetAddWindow = mutableStateOf(false)
    var started = mutableStateOf(0)
    var current_sheet = mutableStateOf("")

    private lateinit var tdb: TestDBOpenHelper
    private lateinit var sdb: SQLiteDatabase


//    retreive the sheet from database ,which has at least one expense items for that month year
    private fun retrieveSheet(): String {
        sdb = tdb.readableDatabase
        val table_name = "expenseDetail"
        val columns: Array<String> = arrayOf("Year", "Month")
        val group_by: String? = "Year,Month"
        val having: String? = null
        val order_by: String? = null

        var c: Cursor =
            sdb.query(table_name, columns, null, null, group_by, having, order_by)

        val sb: StringBuilder = StringBuilder()
        c.moveToFirst()
        for (i in 0 until c.count) {
            sb.append(c.getInt(0).toString())
            sb.append(",")
            sb.append(c.getString(1).toString())
            sb.append("\n")
            c.moveToNext()
        }
        c.close()
        return sb.toString()
    }

    //delete all the year month related expense item from expenseDetail table
    private fun deleteSheet(year:String,month:String) {
        val whereClause = "Year = ? AND Month = ? "
        val whereArgs = arrayOf(year,month)

        sdb.delete("expenseDetail", whereClause, whereArgs)

    }
}



var items_list = mutableStateListOf<String>()
var user_input_month = mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)+1)
var user_input_year = mutableStateOf(Calendar.getInstance().get(Calendar.YEAR))






@ExperimentalMaterial3Api
@Composable
fun SheetAddWindow(onDismiss:() -> Unit) {

    Column(modifier = Modifier.padding(50.dp)) {

        OutlinedTextField(value=user_input_month.value.toString(), onValueChange = { user_input_month.value = it.toInt() },label={Text("Enter month from 1-12")})


        OutlinedTextField(value=user_input_year.value.toString(), onValueChange = { user_input_year.value = it.toInt() },label={Text("Enter year ")})

        Button(onClick = {
            items_list.add("${user_input_year.value} ${user_input_month.value}")
            onDismiss()
        }) {
            Text("Confirm")
        }
    }

}

