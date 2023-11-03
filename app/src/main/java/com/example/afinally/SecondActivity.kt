package com.example.afinally

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class SecondActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text("This is the second activity")
                //show expense list with corresponding year and month
                yearTime.value = intent.getStringExtra("transferYearToSecond").toString()
                monthTime.value=intent.getStringExtra("transferMonthToSecond").toString()

                Text(text="Year: ${yearTime.value}   Month:${monthTime.value}",fontSize=20.sp)

                Button(onClick = {finish() }) {
                    Text("back to main page")
                }


                Text("Total Balance",fontSize=30.sp)
                Text("€ ${balance.value}",fontSize=30.sp)

                Divider()

                OutlinedTextField(value = entered_income.value, onValueChange = {
                    //entered :string
                    val convertedValue = it.toDoubleOrNull()

                    if(convertedValue!=null){
                        income.value = convertedValue
                    }

                    entered_income.value=it

                    balance.value = income.value - expense.value},

                    label = { Text("enter income")} )


                Spacer(modifier = Modifier.width(50.dp))

                BasicTextCard(title = "Expense", subtext ="€ ${expense.value}" )



                Divider()

                // composable function that shows a window when click a button
                Button(onClick = { showExpenseAddWindow.value = true }) { Text("+Add Expense Item") }
                if (showExpenseAddWindow.value) { ExpenseAddWindow(onDismiss = { showExpenseAddWindow.value = false }) }

                Spacer(modifier= Modifier.width(16.dp))

                //lazylist for displaying the expense item
                VerticalList(item_list)

            }
        }
        tdb = TestDBOpenHelper(this, "test.db", null, 1)
        sdb = tdb.writableDatabase
    }
    var yearTime: MutableState<String> = mutableStateOf("")
    var monthTime: MutableState<String> = mutableStateOf("")
    var entered_income = mutableStateOf("")

    var showExpenseAddWindow = mutableStateOf(false)

    private var current_data = mutableStateOf("NO data in database")
    private lateinit var tdb: TestDBOpenHelper
    private lateinit var sdb: SQLiteDatabase

//    private fun addData() {
//        val row1: ContentValues = ContentValues().apply {
//            put("Year", "2023")
//            put("Month", "10")
//            put("Description", item_list[i].first)
//            put("Expense", item_list[i].second)
//        }
//
//
//        sdb.insert("test", null, row1)
//
//    }
}

var item_list = mutableStateListOf<Pair<String,Double>>()
var income = mutableStateOf(0.0)
var expense = mutableStateOf(0.0)
var balance = mutableStateOf(0.0)
var user_input = mutableStateOf("Description , Amount")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseAddWindow(onDismiss:() -> Unit) {

    Column(modifier = Modifier.padding(30.dp)) {

        OutlinedTextField(value = user_input.value, onValueChange = { user_input.value = it },
            label = { Text("Enter expense")}
        )

        Button(onClick = {

            val wholeInput = user_input.value.split(",")
            if(wholeInput.size==2) {
                val expenseDescription = wholeInput[0].trim()
                val amount = wholeInput[1].trim().toDoubleOrNull()
                if (expenseDescription.isNotBlank()&&amount!=null) {
                    item_list.add(Pair(expenseDescription, amount))
                    expense.value += amount
                    balance.value = income.value - expense.value
                }
            }
            //for close the adding window and also folding the keyboard
            onDismiss()
        }) {
            Text("Confirm")
        }
    }

}





@Composable
fun BasicTextCard(title:String, subtext:String) {
    val padding = Modifier.padding(5.dp)

    Card(modifier = padding) {
        Column {
            Text(text = title, modifier = padding, fontSize =20.sp)
            Text(text = subtext, modifier = padding)
        }
    }
}



@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
//try to use listitem here ,different from what i use in lazycolumn in mainActivity
fun VerticalList(item_list: MutableList<Pair<String, Double>>) {
    LazyColumn {
        for (i in 0 until item_list.size) {
            item {
                ListItem(
                    headlineText = { Text("Expense description: ${item_list[i].first}") },
                    supportingText = { Text(text = "Expense amount:${item_list[i].second} ") },
                   trailingContent = { Icon(imageVector = Icons.Filled.Delete ,contentDescription="delete", modifier = Modifier.clickable {
                            item_list.remove(item_list[i])
                   })}
                )
            }
        }
    }
}

