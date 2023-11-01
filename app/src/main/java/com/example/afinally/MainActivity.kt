package com.example.afinally

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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
                Text("clicked item ${last_clicked.value}")
                Text("long clicked item ${last_long_clicked.value}")

                MainScreen()
            }

        }
    }






}

var started = mutableStateOf(0)
var items_list = mutableStateListOf<String>()
var last_clicked = mutableStateOf(0)
var last_long_clicked = mutableStateOf(0)

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {


    var showSheet by remember { mutableStateOf(false) }

    Button(onClick = { showSheet = true }) {
        Text("+Add New Expense Sheet")
    }

    if (showSheet) {
        SheetAddWindow(onDismiss = { showSheet = false })
    }
    VerticalList(items_list, { last_clicked.value = it }, { last_long_clicked.value = it })

}





@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun VerticalList(items_list:MutableList<String>,onStateChanged: (Int) -> Unit, onLongClick: (Int) -> Unit){



    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {
        for (i in 0 until items_list.size) {
            item {
                ListItem(

                    headlineText = { Text("created expense sheet item: $i") },
                    supportingText = { Text(text = items_list[i]) },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            onStateChanged(i)
                             startActivity(createIntentSecondActivity())
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



private fun createIntentSecondActivity(): Intent {

    var intent  = Intent( this,SecondActivity::class.java)
    intent. putExtra( "started", started.value)

    started.value++
    return intent
}




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

