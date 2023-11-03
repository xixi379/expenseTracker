package com.example.afinally

import android.content.Intent
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
            }
        }

    }


    var showSheetAddWindow = mutableStateOf(false)
    var started = mutableStateOf(0)

    private fun createIntentSecondActivity(yearTime:String,monthTime:String): Intent {
        var intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("transferYearToSecond", "${yearTime }")
        intent.putExtra("transferMonthToSecond", " ${monthTime}")
        started.value++
        return intent
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


        OutlinedTextField(value=user_input_year.value.toString(), onValueChange = { user_input_year.value = it.toInt() },label={Text("Enter year ")})

        Button(onClick = {
            items_list.add("${user_input_year.value} ${user_input_month.value}")
            onDismiss()
        }) {
            Text("Confirm")
        }
    }

}

