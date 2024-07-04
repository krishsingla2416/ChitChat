//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.LineHeightStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chitchat.Transaction
//import com.example.chitchat.User
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
////import androidx.compose.ui.Modifier
//import androidx.compose.foundation.clickable
//
//@Composable
//fun UserListScreen(userViewModel: com.example.chitchat.UserViewModel = viewModel()) {
//    val userList by userViewModel.users.observeAsState(emptyList())
//
//    var name by remember { mutableStateOf("") }
//    var phoneNumber by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Name") },
//            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
//        )
//        OutlinedTextField(
//            value = phoneNumber,
//            onValueChange = { phoneNumber = it },
//            label = { Text("Phone Number") },
//            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
//        )
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
//        )
//        Button(
//            onClick = {
//                val user = User(id = phoneNumber, name = name, email = email)
//                userViewModel.addUser(user)
//                // Clear input fields after adding the user
//                name = ""
//                phoneNumber = ""
//                email = ""
//            },
//            modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp)
//        ) {
//            Text(text = "Add User")
//        }
//
//        LazyColumn {
//            items(userList) { user ->
//                UserItem(
//                    user = user,
//                    onDelete = { userViewModel.deleteUser(it.id) },
//                    onEdit = { editedUser -> userViewModel.editUser(editedUser) },
//                    onAddTransaction = { transaction -> userViewModel.addTransaction(user.id, transaction) },
//                    onDeleteTransaction = { transaction -> userViewModel.deleteTransaction(user.id, transaction) },
//                    onEditTransaction = { oldTransaction, newTransaction -> userViewModel.editTransaction(user.id, oldTransaction, newTransaction) }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun UserItem(
//    user: User,
//    onDelete: (User) -> Unit,
//    onEdit: (User) -> Unit,
//    onAddTransaction: (Transaction) -> Unit,
//    onDeleteTransaction: (Transaction) -> Unit,
//    onEditTransaction: (Transaction, Transaction) -> Unit
//) {
//    var isEditing by remember { mutableStateOf(false) }
//    var name by remember { mutableStateOf(user.name) }
//    var email by remember { mutableStateOf(user.email) }
//
//    var amount by remember { mutableStateOf("") }
//    var date by remember { mutableStateOf("") }
//    var type by remember { mutableStateOf("") }
//
//    if (isEditing) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
//            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
//            Row {
//                Button(onClick = {
//                    onEdit(user.copy(name = name, email = email))
//                    isEditing = false
//                }) {
//                    Text(text = "Save")
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(onClick = { isEditing = false }) {
//                    Text(text = "Cancel")
//                }
//            }
//        }
//    } else {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text(text = "Name: ${user.name}")
//                Text(text = "Email: ${user.email}")
//                user.transactions.forEach { transaction ->
//                    TransactionItem(
//                        transaction = transaction,
//                        onDelete = { onDeleteTransaction(transaction) },
//                        onEdit = { oldTransaction, newTransaction -> onEditTransaction(oldTransaction, newTransaction) }
//                    )
//                }
//                Column {
//                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
//                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
//                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (sent/received)") })
//                    Button(onClick = {
//                        val transaction = Transaction(amount = amount, date = date, type = type)
//                        onAddTransaction(transaction)
//                        // Clear input fields after adding the transaction
//                        amount = ""
//                        date = ""
//                        type = ""
//                    }) {
//                        Text(text = "Add Transaction")
//                    }
//                }
//            }
//            Row {
//                IconButton(onClick = { isEditing = true }) {
//                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
//                }
//                IconButton(onClick = { onDelete(user) }) {
//                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TransactionItem(
//    transaction: Transaction,
//    onDelete: () -> Unit,
//    onEdit: (Transaction, Transaction) -> Unit
//) {
//    var isEditing by remember { mutableStateOf(false) }
//    var amount by remember { mutableStateOf(transaction.amount) }
//    var date by remember { mutableStateOf(transaction.date) }
//    var type by remember { mutableStateOf(transaction.type) }
//
//    if (isEditing) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
//            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
//            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (sent/received)") })
//            Row {
//                Button(onClick = {
//                    val newTransaction = Transaction(amount = amount, date = date, type = type)
//                    onEdit(transaction, newTransaction)
//                    isEditing = false
//                }) {
//                    Text(text = "Save")
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(onClick = { isEditing = false }) {
//                    Text(text = "Cancel")
//                }
//            }
//        }
//    } else {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text(text = "Amount: ${transaction.amount}")
//                Text(text = "Date: ${transaction.date}")
//                Text(text = "Type: ${transaction.type}")
//            }
//            Row {
//                IconButton(onClick = { isEditing = true }) {
//                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
//                }
//                IconButton(onClick = { onDelete() }) {
//                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun UserListScreenPreview() {
//    UserListScreen()
//}