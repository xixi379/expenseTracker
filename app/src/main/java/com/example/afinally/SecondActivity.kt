package com.example.afinally

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar


class SecondActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        //get a writable connection to the database
        tdb = TestDBOpenHelper(this, "test", null, 1)
        sdb = tdb.writableDatabase


        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Row{
                    Text("This is the second activity")
                    Button(onClick = {finish()
                    }) {
                        Text("back to main page")
                    }
                }
                //show corresponding year and month on each monthly expense & income page through intent
                year_Time.value = intent.getStringExtra("transferYearToSecond").toString()
                month_Time.value=intent.getStringExtra("transferMonthToSecond").toString()
                Text(text="Year: ${year_Time.value}   Month:${month_Time.value}",fontSize=20.sp)


                //show balance value on each monthly expense page, it >0 surplus, it<0, deficit
                Text("Total Balance : € ${balance.value}",fontSize=30.sp)

                Divider()

                //enter income and click confirm button to add into table incomeDetail in database
                Row{
                  OutlinedTextField(value = entered_income.value,
                    onValueChange = { entered_income.value=it },
                    label = { Text("enter income")} ,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))

                  Button(
                    onClick = {

                        val convertedValue = entered_income.value.toDoubleOrNull()
                        if (convertedValue != null) {
                            addIncome(convertedValue)
                        }

                        balance.value = retrieveIncomeData() - expense_total.value
                        entered_income.value=""
                       }
                  ) {
                       Text("confirm")
                  }
               }

                Spacer(modifier = Modifier.width(50.dp))

                //get expense total amount from database
                expense_total.value = getTotalExpense(year_Time.value.trim().toInt(),month_Time.value.trim().toInt())
                balance.value = retrieveIncomeData() - expense_total.value

                //use row layout to display monthly income and expense total figure
                Row{
                BasicTextCard(title = "Total Income", subtext ="€ ${retrieveIncomeData()}" )
                BasicTextCard(title = "Total Expense", subtext ="€ ${expense_total.value}" )

                }

                Divider()

                // composable function that trigger expense item adding activity when click a button
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
                        modifier = Modifier.padding(10.dp)
                    ) {
                        OutlinedTextField(
                            value = user_input_description.value,
                            onValueChange = { user_input_description.value = it },
                            label = { Text("Enter expense description") } ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            value = user_input_amount.value,
                            onValueChange = { user_input_amount.value = it },
                            label = { Text("Enter expense amount") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            value = user_input_day.value,
                            onValueChange = { user_input_day.value = it },
                            label = { Text("Enter day of month") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                        Button(
                            onClick = {


                                    if (user_input_description.value.isNotBlank()) {

                                        addData(user_input_description.value.trim(),user_input_amount.value.trim().toDouble(),
                                            user_input_day.value.trim().toInt())
//                                        item_list.add(Pair(user_input_description.value.trim(), user_input_amount.value.trim().toDouble()))

                                        current_data.value = retrieveData()
//                                        item_list.clear()
                                        user_input_description.value=""
                                        user_input_amount.value=""
                                        user_input_day.value=Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()

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

                //used to retrieve expense items for each year month from the database
                //can be deleted from the database as well
                Button(onClick = {current_data.value = retrieveData()}){
                Text("retrieve current expense item :")
                }
                if (current_data.value.isEmpty()) {
                    Text(text = "")
                } else {
                    LazyColumn {

                        val items = current_data.value.split("\n")
                        items.forEach { item ->
                            val parts = item.split(",")
                            if (parts.size >= 6) {
                                val ID = parts[0]
                                val year = parts[1]
                                val month = parts[2]
                                val day = parts[3]
                                val description = parts[4]
                                val expense = parts[5]

                                item {
                                    Row {
                                        Text(text = "ID: $ID ")
                                        Text(text = " $year-$month-$day")
                                        Text(text = "Description: $description ")
                                        Text(text = "Expense:€$expense")
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.clickable {
                                                deleteData(ID.trim().toInt(),day.trim().toInt(),description.trim(),expense.trim().toDouble())
                                                current_data.value = retrieveData()
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


    var year_Time = mutableStateOf("")
    var month_Time = mutableStateOf("")
    var entered_income = mutableStateOf("")
    var showExpenseAddWindow = mutableStateOf(false)


    //retrieve the total expense amount from database table "expenseDetail"
    fun getTotalExpense(year:Int,month:Int): Double {
        val query = "SELECT SUM(Expense) FROM expenseDetail where Year = $year AND Month = $month"
        val cursor = sdb.rawQuery(query, null)
        var totalExpense = 0.0

        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            totalExpense = cursor.getDouble(0)
        }

        cursor.close()
        return totalExpense
    }

    //retrieve all the relevant expense items for specific year and month
     fun retrieveData(): String {
        sdb = tdb.readableDatabase
        val table_name = "expenseDetail"
        val columns: Array<String> = arrayOf("ID","Year", "Month","Day","Description","Expense")
        val where = "Year=? AND Month= ?"
        val where_args: Array<String> = arrayOf(year_Time.value.trim(),month_Time.value.trim())
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null

        var c: Cursor =
            sdb.query(table_name, columns, where, where_args, group_by, having, order_by)

        val sb: StringBuilder = StringBuilder()
        c.moveToFirst()
        for (i in 0 until c.count) {
            sb.append(c.getString(0).toString())
            sb.append(",")
            sb.append(c.getString(1).toString())
            sb.append(",")
            sb.append(c.getString(2).toString())
            sb.append(",")
            sb.append(c.getString(3).toString())
            sb.append(",")
            sb.append(c.getString(4).toString())
            sb.append(",")
            sb.append(c.getString(5).toString())
            sb.append("\n")
            c.moveToNext()
        }
        c.close()
        return sb.toString()
    }

    //use the latest added income data as the income of each month year from the table "incomeDetail"
    private fun retrieveIncomeData(): Double {
        sdb = tdb.readableDatabase
        val table_name = "incomeDetail"
        val columns: Array<String> = arrayOf("ID","Year", "Month", "Income")
        val where = "Year=? AND Month= ?"
        val where_args: Array<String> = arrayOf(year_Time.value.trim(),month_Time.value.trim())
        val group_by: String? = null
        val having: String? = null
        //make sure the most recently added income data will be retrieved as the total income
        val order_by: String? = "ID DESC"

        var c: Cursor =
            sdb.query(table_name, columns, where, where_args, group_by, having, order_by)

        var incomedata=0.0
        if(c.moveToFirst())
        {
           incomedata= c.getString(3).toDouble()
            //there is a hint to close cursor
            c.close()
        }
        return incomedata

    }

    //for adding new expense items to expenseDetail table in database
   private fun addData(expenseDescription: String, amount: Double,day:Int) {
        val row: ContentValues = ContentValues().apply {
            put("Year", year_Time.value.trim().toInt())
            put("Month", month_Time.value.trim().toInt())
            put("Day", day)
            put("Description", expenseDescription)
            put("Expense", amount)
        }

       sdb.insert("expenseDetail", null, row)


    }

    //add income data to incomeDetail table
    private fun addIncome(income:Double) {
        val row: ContentValues = ContentValues().apply {
            put("Year", year_Time.value.trim().toInt())
            put("Month", month_Time.value.trim().toInt())
            put("Income", income)
        }

        sdb.insert("incomeDetail", null, row)


    }

    //delete expense item data from expenseDetails
    private fun deleteData(ID:Int,day:Int,expenseDescription: String, amount: Double) {
        val whereClause = "ID = ? AND Year = ? AND Month = ? AND Day=? AND Description = ? AND Expense = ? "
        val whereArgs = arrayOf(ID.toString(),year_Time.value.trim(),month_Time.value.trim(),day.toString(),expenseDescription, amount.toString())

        sdb.delete("expenseDetail", whereClause, whereArgs)
        expense_total.value-=amount
    }


    private var current_data = mutableStateOf("")
    private lateinit var tdb: TestDBOpenHelper
    private lateinit var sdb: SQLiteDatabase
}

//var item_list = mutableStateListOf<Pair<String,Double>>()
var expense_total = mutableStateOf(0.0)
var balance = mutableStateOf(0.0)
var user_input_description = mutableStateOf("")
var user_input_amount = mutableStateOf("")
var user_input_day = mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString())

@Composable
fun BasicTextCard(title:String, subtext:String) {
    Card(modifier = Modifier.padding(10.dp)) {
        Column {
            Text(text = title, modifier = Modifier.padding(5.dp), fontSize =20.sp)
            Text(text = subtext, modifier = Modifier.padding(5.dp))
        }
    }
}



