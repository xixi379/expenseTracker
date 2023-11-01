package com.example.afinally

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
                    Text("Finish activity")
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

                OutlinedTextField(value = user_input.value, onValueChange = { user_input.value = it }, label = { Text("Enter expense")},keyboardActions = KeyboardActions(onDone = {
                    val parts = user_input.value.split(",")
                    if(parts.size==2) {
                        val expenseDescription = parts[0].trim()
                        val amount = parts[1].trim().toDoubleOrNull()
                        if (expenseDescription.isNotBlank()&&amount!=null) {
                            items_list.add(Pair(expenseDescription, amount))
                            expense.value += amount
                            balance.value = income.value - expense.value
                        }
                    }
                }), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
                VerticalList(items_list,{last_clicked.value= it },{last_long_clicked.value = it})

                Button(onClick = { finishActivity() }) {
                    Text("Finish Activity")

                }
            }
        }

    }
    fun finishActivity() {
        var return_intent = Intent(Intent.ACTION_VIEW)
        return_intent.putExtra("input", balance.value.toString())
        println("entered text was : ${balance.value}")
        setResult(RESULT_OK, return_intent)
        finish()
    }



    var balance = mutableStateOf(0.0)
    var entered_income = mutableStateOf("")
    var income = mutableStateOf(0.0)
    var expense = mutableStateOf(0.0)
    var items_list = mutableStateListOf<Pair<String,Double>>()
    var user_input = mutableStateOf("Description , Amount")
    var last_clicked = mutableStateOf(0)
    var last_long_clicked = mutableStateOf(0)

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
fun VerticalList(items_list: MutableList<Pair<String, Double>>, onStateChanged: (Int) -> Unit, onLongClick: (Int) -> Unit) {
    LazyColumn {
        for (i in 0 until items_list.size) {
            item {
                ListItem(
                    headlineText = { Text("Expense description: ${items_list[i].first}") },
                    supportingText = { Text(text = "Expense amount:${items_list[i].second} ") },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            onStateChanged(i)
                        },
                        onLongClick = {
                            onLongClick(i)
                        }
                    )
                )
            }
        }
    }
}

