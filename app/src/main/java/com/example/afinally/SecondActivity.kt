package com.example.afinally

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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class SecondActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text("This is the second activity")
                Button(onClick = {finish() }) {
                    Text("Finish activity, back to main page")
                }
                Text("Total Balance",fontSize=30.sp)
                Text("€ ${balance.value}",fontSize=30.sp)

                Divider()

                OutlinedTextField(value = entered_income.value, onValueChange = {
                    val convertedValue = it.toDoubleOrNull()

                    if(convertedValue!=null){
                        income.value = convertedValue
                    }
                    entered_income.value=it
                    balance.value = income.value - expense.value},

                    label = { Text("enter income")} )


                Spacer(modifier = Modifier.width(50.dp))

                basicTextCard(title = "Expense", subtext ="€ ${expense.value}" )



                Divider()

                // composable function that shows a sheet when click a button
                Button(onClick = { showExpenseAddWindow.value = true }) { Text("+Add Expense Item") }
                if (showExpenseAddWindow.value) { ExpenseAddWindow(onDismiss = { showExpenseAddWindow.value = false }) }

                Spacer(modifier= Modifier.width(16.dp))

//                OutlinedTextField(value = user_input.value, onValueChange = { user_input.value = it }, label = { Text("Enter expense")},keyboardActions = KeyboardActions(onDone = {
//                    val parts = user_input.value.split(",")
//                    if(parts.size==2) {
//                        val expenseDescription = parts[0].trim()
//                        val amount = parts[1].trim().toDoubleOrNull()
//                        if (expenseDescription.isNotBlank()&&amount!=null) {
//                            items_list.add(Pair(expenseDescription, amount))
//                            expense.value += amount
//                            balance.value = income.value - expense.value
//                        }
//                    }
//                }), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
                VerticalList(item_list)

            }
        }

    }

    var entered_income = mutableStateOf("")

    var showExpenseAddWindow = mutableStateOf(false)
}

var item_list = mutableStateListOf<Pair<String,Double>>()
var income = mutableStateOf(0.0)
var expense = mutableStateOf(0.0)
var balance = mutableStateOf(0.0)
var user_input = mutableStateOf("Description , Amount")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseAddWindow(onDismiss:() -> Unit) {

    Column(modifier = Modifier.padding(50.dp)) {

        OutlinedTextField(value = user_input.value, onValueChange = { user_input.value = it }, label = { Text("Enter expense")}

              )

        Button(onClick = {
            val parts = user_input.value.split(",")
            if(parts.size==2) {
                val expenseDescription = parts[0].trim()
                val amount = parts[1].trim().toDoubleOrNull()
                if (expenseDescription.isNotBlank()&&amount!=null) {
                    item_list.add(Pair(expenseDescription, amount))
                    expense.value += amount
                    balance.value = income.value - expense.value
                }
            }
            onDismiss()
        }) {
            Text("Confirm")
        }
    }

}





@Composable
fun basicTextCard(title:String, subtext:String) {
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

