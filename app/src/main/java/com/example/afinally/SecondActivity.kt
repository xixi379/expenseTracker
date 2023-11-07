package com.example.afinally

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.SQLException


class SecondActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        //get a writable connection to the database
        tdb = TestDBOpenHelper(this, "test", null, 1)
        sdb = tdb.writableDatabase



//        if (sdb != null) {
//            // Database opened successfully, you can now use it.
//            Log.i("successful","--------------------------------------------------")
//        } else {
//            Log.i("fail","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
//        }

        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Text("This is the second activity")
                //show expense_total list with corresponding year and month
                year_Time.value = intent.getStringExtra("transferYearToSecond").toString()
                month_Time.value=intent.getStringExtra("transferMonthToSecond").toString()
                Text(text="Year: ${year_Time.value}   Month:${month_Time.value}",fontSize=20.sp)

                Button(onClick = {finish() }) {
                    Text("back to main page")
                }

                Text("Total Balance : € ${balance.value}",fontSize=30.sp)

                Divider()

                OutlinedTextField(value = entered_income.value, onValueChange = {
                    //entered :string
                    val convertedValue = it.toDoubleOrNull()
                    if(convertedValue!=null){
                        income.value = convertedValue
                    }
                    entered_income.value=it

                    balance.value = income.value - expense_total.value},


                    label = { Text("enter income")} ,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))

                Spacer(modifier = Modifier.width(50.dp))

                BasicTextCard(title = "Total Expense", subtext ="€ ${expense_total.value}" )



                Divider()

                // composable function that shows a window when click a button
                Button(
                    onClick = {
                        showExpenseAddWindow.value = true
                    }
                ) {
                    Text("+Add Expense Item")
                }

                //use column composable to show the expense item adding window
                if (showExpenseAddWindow.value) {
                    Column(
                        modifier = Modifier.padding(30.dp)
                    ) {
                        OutlinedTextField(
                            value = user_input_description.value,
                            onValueChange = { user_input_description.value = it },
                            label = { Text("Enter expense description") }
                        )
                        OutlinedTextField(
                            value = user_input_amount.value,
                            onValueChange = { user_input_amount.value = it },
                            label = { Text("Enter expense amount") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                        Button(
                            onClick = {


                                    if (user_input_description.value.isNotBlank()) {

                                        addData(user_input_description.value.trim(),user_input_amount.value.trim().toDouble())
                                        item_list.add(Pair(user_input_description.value.trim(), user_input_amount.value.trim().toDouble()))
                                        expense_total.value += user_input_amount.value.toDouble()
                                        balance.value = income.value - expense_total.value


                                    }

                               //for close the adding window
                                showExpenseAddWindow.value = false
                            }

                    ){
                            Text("Submit")
                        }

                    }
                }

               Spacer(modifier= Modifier.width(16.dp))

                //lazylist for displaying the expense item
                LazyColumn {
                    for (i in 0 until item_list.size) {
                        item {
                            ListItem(
                                headlineText = { Text("Expense description: ${item_list[i].first}") },
                                supportingText = { Text(text = "Expense amount:${item_list[i].second} ") },
                                trailingContent = { Icon(imageVector = Icons.Filled.Delete ,contentDescription="delete", modifier = Modifier.clickable {
                                    expense_total.value-=item_list[i].second
                                    balance.value = income.value - expense_total.value

                                    deleteData(user_input_description.value.trim(),user_input_amount.value.trim().toDouble())

                                    item_list.remove(item_list[i])


                                })}
                            )
                        }
                    }
                }
            }
        }

    }


    var year_Time: MutableState<String> = mutableStateOf("")
    var month_Time: MutableState<String> = mutableStateOf("")
    var entered_income = mutableStateOf("")
    var showExpenseAddWindow = mutableStateOf(false)

   private fun addData(expenseDescription: String, amount: Double) {
        val row: ContentValues = ContentValues().apply {
            put("Year", year_Time.value.trim().toInt())
            put("Month", month_Time.value.trim().toInt())
            put("Description", expenseDescription)
            put("Expense", amount)
        }

       sdb.insert("expenseDetail", null, row)

    }


    private fun deleteData(expenseDescription: String, amount: Double) {
        val whereClause = "Year = ? AND Month = ? AND Description = ? AND Expense = ? "
        val whereArgs = arrayOf(year_Time.value.trim(),month_Time.value.trim(),expenseDescription, amount.toString())

        sdb.delete("expenseDetail", whereClause, whereArgs)
    }


    private lateinit var tdb: TestDBOpenHelper
    private lateinit var sdb: SQLiteDatabase
}

var item_list = mutableStateListOf<Pair<String,Double>>()
var income = mutableStateOf(0.0)
var expense_total = mutableStateOf(0.0)
var balance = mutableStateOf(0.0)
var user_input_description = mutableStateOf("")
var user_input_amount = mutableStateOf("")

@Composable
fun BasicTextCard(title:String, subtext:String) {
    Card(modifier = Modifier.padding(10.dp)) {
        Column {
            Text(text = title, modifier = Modifier.padding(5.dp), fontSize =20.sp)
            Text(text = subtext, modifier = Modifier.padding(5.dp))
        }
    }
}



