package com.example.chitchat

//import TransactionItem
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyApp(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(navController, userViewModel)
        }
        composable(Screen.OldCustomers.route) {
            OldCustomersScreen(navController, userViewModel)
        }
        composable(
            route = "${Screen.CustomerTransactions.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CustomerTransactionsScreen(userId, navController, userViewModel)
        }
        composable(
            route = "${Screen.EditTransaction.route}/{userId}/{amount}/{date}/{type}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val transaction = Transaction(amount, date, type)
            EditTransactionScreen(userId, transaction, navController, userViewModel)
        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate(Screen.AddCustomer.route) }) {
            Text("Add New Customer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.OldCustomers.route) }) {
            Text("See Old Customers")
        }
    }
}

@Composable
fun AddCustomerScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(
            onClick = {
                val user = User(id = phoneNumber, name = name)
                userViewModel.addUser(user)
                // Clear input fields after adding the user
                name = ""
                phoneNumber = ""
                //navController.popBackStack() // Pop the current screen off the stack
                navController.navigate(Screen.OldCustomers.route){
                    navController.popBackStack()
                } // Navigate to the OldCustomers screen
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Add Customer")
        }
    }
}

@Composable
fun OldCustomersScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val users: Map<String, User> by userViewModel.users.observeAsState(emptyMap())

    Column {
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { navController.popBackStack() },Modifier.fillMaxWidth().padding(12.dp).height(60.dp)) {
            Text(text = "Go to Home Screen")
        }
        LazyColumn {
            items(users.values.toList()) { user ->
                CustomerItem(
                    user = user,
                    onClick = { navController.navigate("${Screen.CustomerTransactions.route}/${user.id}") },
                    userViewModel
                )
            }
        }
    }

}


@Composable
fun CustomerTransactionsScreen(
    userId: String,
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.users.observeAsState().value?.get(userId)

    Column(Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = {
            userViewModel.deleteUser(userId)
            navController.popBackStack() },
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(60.dp)) {
            Text(text = "Delete User", fontSize = 24.sp)
        }
        var money by remember { mutableStateOf("") }
        //var date by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = money,
            onValueChange = { money = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        Button(
            onClick = {
                userViewModel.addTransaction(
                    userId,
                    Transaction(money,  getCurrentDateTime(), type)
                )
                money = ""
               // date = ""
                type = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(60.dp)
        ) {
            Text(text = "ADD Transaction", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(50.dp))

        if (user != null) {
            if(user.transactions.isNotEmpty()) {
                LazyColumn {

                    items(user.transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { userViewModel.deleteTransaction(userId, transaction) },
                            onEdit = {
                                navController.navigate(
                                    "${Screen.EditTransaction.route}/${userId}/${transaction.amount}/${transaction.date}/${transaction.type}"
                                )
                            }
                        )
                    }

                }
            }else{
                Text(text = "              No Transactions!", fontSize = 24.sp, color = Color.Red)
            }
        }
    }
}


@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: (Transaction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle click if needed */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Amount: ${transaction.amount}")
                IconButton(onClick = { onEdit(transaction) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Transaction")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${transaction.date}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Type: ${transaction.type}")
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { onDelete() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Delete")
            }
        }
    }
}


@Composable
fun CustomerItem(
    user: User,
    onClick: () -> Unit,
    userViewModel: UserViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Name: ${user.name}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Phone Number: ${user.id}")
            }


        }

    }
}

@Composable
fun EditTransactionScreen(
    userId: String,
    transaction: Transaction,
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var money by remember { mutableStateOf(transaction.amount) }
    var date by remember { mutableStateOf(transaction.date) }
    var type by remember { mutableStateOf(transaction.type) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {


        OutlinedTextField(
            value = money,
            onValueChange = { money = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
//        OutlinedTextField(
//            value = date,
//            onValueChange = { date = it },
//            label = { Text("Date") },
//            modifier = Modifier.fillMaxWidth()
//        )
        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val newTransaction = Transaction(money, date, type)
                userViewModel.editTransaction(userId, transaction, newTransaction)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Save")
        }
    }
}

fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}
