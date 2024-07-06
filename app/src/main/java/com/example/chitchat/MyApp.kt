package com.example.chitchat

//import TransactionItem
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.chitchat.screens.LoginPage
import com.example.chitchat.screens.SignInPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun MyApp(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController = navController, userViewModel) }
        composable("splash2") { SplashScreen2(navController = navController) }

        composable("login") {
            LoginPage(navHostController = navController, viewModel = userViewModel)
        }
        composable("signin") {
            SignInPage(navHostController = navController, viewModel = userViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(Screen.OldCustomers.route) {
            OldCustomersScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(
            route = "${Screen.CustomerTransactions.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CustomerTransactionsScreen(
                userId = userId,
                navController = navController,
                userViewModel = userViewModel
            )
        }


        composable(
            route = "${Screen.EditTransaction.route}/{userId}/{date}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""

            EditTransactionScreen(
                userId = userId,
                date = date,
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}


@Composable
fun OldCustomersScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val users: Map<String, User> by userViewModel.users.observeAsState(emptyMap())

    Column {
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { navController.popBackStack() },
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(60.dp)
        ) {
            Text(text = "Go to Home Screen")
        }
        LazyColumn {
            items(users.values.toList()) { user ->
                CustomerItem(
                    user = user,
                    onClick = { navController.navigate("${Screen.CustomerTransactions.route}/${user.id}") },
                    userViewModel = userViewModel
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
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                userViewModel.deleteUser(userId)
                navController.popBackStack()
            },
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(60.dp)
        ) {
            Text(text = "Delete User", fontSize = 24.sp)
        }

        var money by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = money,
            onValueChange = { money = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                // Hide the keyboard when the user presses the done button

                keyboardController?.hide()
            })
        )

        val type = remember { mutableStateOf("sent") }
        val url = remember { mutableStateOf("") }

        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = type.value == "sent",
                    onClick = { type.value = "sent" }
                )
                Text("Sent", fontSize = 18.sp)
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = type.value == "received",
                    onClick = { type.value = "received" }
                )
                Text("Received", fontSize = 18.sp)
            }
        }

        Button(
            onClick = {
                val res = userViewModel.addTransaction(
                    userId,
                    Transaction(money, getCurrentDateTime(), type.value, url.value)
                )
                if (res == "okay") {
                    money = ""
                    type.value = "sent"
                    Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(60.dp)
        ) {
            Text(text = "ADD Transaction", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val scope = rememberCoroutineScope()
        val isLoading = remember {
            mutableStateOf(false)
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
            uri?.let {
                scope.launch {
                    isLoading.value = true
                    url.value = userViewModel.uploadImageToFirebase(uri, context, userId = userId)
                    isLoading.value = false
                }
            }
        }

        // Adding camera is pending
//        val launcherCam = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.TakePicture()
//        ) {
//            if(it){
//
//            }
//        }

        Row(Modifier.fillMaxWidth()) {
            Button(onClick = { launcher.launch("image/*") }, Modifier.offset(x = 18.dp)) {
                Text(text = "Add bill")
            }

            if (isLoading.value) {
                CircularProgressIndicator(Modifier.offset(x = 90.dp))
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        if (user != null) {
            var userList = user.transactions.values.toList()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            userList = userList.sortedBy { LocalDateTime.parse(it.date, formatter) }.reversed()

            if (userList.isNotEmpty()) {
                LazyColumn {
                    items(userList) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { userViewModel.deleteTransaction(userId, transaction) },
                            navController = navController,
                            userId = userId
                        )
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "No Transactions!", fontSize = 24.sp, color = Color.Red)
                }
            }
        }
    }
}


@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    navController: NavHostController,
    userId: String
) {
    val context = LocalContext.current
    val amountColor = if (transaction.type != "sent") Color(0XFF3e9c35) else Color.Red
    val amountPrefix = if (transaction.type != "sent") "+ ₹" else "- ₹"

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
                Text(
                    text = "$amountPrefix${transaction.amount}",
                    color = amountColor,
                    fontSize = 24.sp
                )
                IconButton(
                    onClick = {
                        navController.navigate(
                            "${Screen.EditTransaction.route}/$userId/${transaction.date}"
                        )
//                        try {
//                            if (transaction.billUrl.isNotEmpty() && Uri.parse(transaction.billUrl).isAbsolute) {
//                                navController.navigate(
//                                    "${Screen.EditTransaction.route}/$userId/${transaction.date}"
//                                )
//                            } else {
//                                Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
//                            }
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Transaction")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${transaction.date}")
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onDelete()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun EditTransactionScreen(
    userId: String,
    date: String,
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val oldTransaction = userViewModel.users.value?.get(userId)?.transactions?.get(date)
    val context = LocalContext.current
    var money by remember { mutableStateOf(oldTransaction!!.amount) }
    val typee = remember { mutableStateOf(oldTransaction!!.type) }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val url = remember { mutableStateOf(oldTransaction!!.billUrl) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = money,
            onValueChange = { money = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = typee.value == "sent",
                    onClick = { typee.value = "sent" }
                )
                Text("Sent", fontSize = 18.sp)
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = typee.value == "received",
                    onClick = { typee.value = "received" }
                )
                Text("Received", fontSize = 18.sp)
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    isLoading.value = true
                    val temp = userViewModel.uploadImageToFirebase(uri, context, userId)
                    if (temp.isNotEmpty() && temp != "error") {
                        url.value = temp
                        Log.d("EditTransactionScreen", "Image URL updated: $temp")
                    }
                    isLoading.value = false
                }
            }
        }

        Row(Modifier.fillMaxWidth()) {
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "Edit bill")
            }

            if (isLoading.value) {
                CircularProgressIndicator(Modifier.offset(x = 90.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (url.value.isNotEmpty() && url.value != "error") {
            AsyncImage(
                model = url.value,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(350.dp) // You can adjust the height as needed
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Card(
                onClick = { /*TODO*/ }, modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(350.dp) // You can adjust the height as needed
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "No Bill Attached",
                    Modifier.offset(x = 80.dp, y = 170.dp),
                    fontSize = 24.sp
                )
            }
        }

        Button(
            onClick = {
                val newTransaction = Transaction(money, date, typee.value, url.value)
                val res = userViewModel.editTransaction(userId, date, newTransaction)
                if (res == "okay") {
                    Toast.makeText(context, "Transaction edited successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Failed to edit transaction", Toast.LENGTH_SHORT).show()
                }
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


@Composable
fun SplashScreen(navController: NavController, viewModel: UserViewModel) {
    val authState = viewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        delay(765) // Delay for 2 seconds
        //LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("splash2") {
                popUpTo("splash") { inclusive = true }
            }

            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }

        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "KhataBook App",
                fontSize = 50.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "                           ~ Made by Krish",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }

}


@Composable
fun SplashScreen2(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(765) // Delay for 2 seconds

        navController.navigate(Screen.Home.route) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Text(text = "Configuring previous data...", fontSize = 20.sp) }

}


@Composable
fun HomeScreen(navController: NavHostController, userViewModel: UserViewModel) {
    Surface {
        Image(
            painter = painterResource(id = R.drawable.paisa2), contentDescription = "",
            Modifier
                .fillMaxWidth()
                .offset(y = 620.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.paisa),
            contentDescription = "Background for home page",
            Modifier
                .fillMaxSize()
                .offset(y = (-25).dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            //verticalArrangement = Arrangement.spacedBy(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello Mr. " + userViewModel.currPerson,
                fontSize = 18.sp,
                modifier = Modifier.offset(y = 30.dp)
            )
            Text(
                text = "Total Balance is : ",
                fontSize = 30.sp,
                modifier = Modifier.offset(y = 40.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            val totalsum = userViewModel.totalBalance.observeAsState()
            if (totalsum.value!!.toInt() > 0) {
                Text(
                    text = "+ " + "₹" + totalsum.value!!,
                    Modifier
                        .padding(end = 18.dp)
                        .offset(y = 20.dp), fontSize = 30.sp, color = Color(0XFF3e9c35)
                )
            } else if (totalsum.value!!.toInt() < 0) {
                Text(
                    text = "- ₹" + (-(totalsum.value!!.toInt())).toString(),
                    Modifier
                        .padding(end = 18.dp)
                        .offset(y = 20.dp), fontSize = 30.sp, color = Color.Red
                )
            } else {
                Text(
                    text = "₹ " + totalsum.value!!,
                    Modifier
                        .padding(end = 18.dp)
                        .offset(y = 20.dp), fontSize = 30.sp, color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            Button(
                onClick = { navController.navigate(Screen.AddCustomer.route) },
                Modifier.offset(y = 430.dp, x = (-90).dp)
            ) {
                Text("Add New Customer")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.OldCustomers.route) },
                Modifier.offset(y = 365.dp, x = 90.dp)
            ) {
                Text("See Old Customers")
            }
            Button(onClick = {
                userViewModel.signOut()
                navController.navigate("login") {
                    navController.popBackStack()
                }
            }, Modifier.offset(y = 370.dp)) {
                Text(text = "Logout")
            }
        }
    }

}

@Composable
fun AddCustomerScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number,
            )
        )
        Button(
            onClick = {
                val user = User(id = phoneNumber, name = name)
                val res = userViewModel.addUser(user)

                if (res != "error") {
                    name = ""
                    phoneNumber = ""
                    navController.navigate(Screen.OldCustomers.route) {
                        navController.popBackStack()
                    } // Navigate to the OldCustomers screen
                } else {
                    // val context = LocalContext.current
                    phoneNumber = ""
                    Toast.makeText(
                        context,
                        "Phone number already registered or invalid phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Name: ${user.name}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Phone Number: ${user.id}")
            }


            if (user.sum.toInt() > 0) {
                Text(
                    text = "+ " + "₹" + user.sum,
                    Modifier.padding(end = 18.dp),
                    fontSize = 20.sp,
                    color = Color(0XFF3e9c35)
                )
            } else if (user.sum.toInt() < 0) {
                Text(
                    text = "- ₹" + (-(user.sum.toInt())).toString(),
                    Modifier.padding(end = 18.dp),
                    fontSize = 20.sp,
                    color = Color.Red
                )
            } else {
                Text(
                    text = "₹ " + user.sum,
                    Modifier.padding(end = 18.dp),
                    fontSize = 20.sp,
                    color = Color.Blue
                )
            }
        }

    }
}


fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}
