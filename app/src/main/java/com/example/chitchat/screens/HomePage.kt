//package com.example.chitchat.screens
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.Button
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.app.Person
//import androidx.navigation.NavHostController
////import com.example.chitchat.AuthState
////import com.example.chitchat.ViewModel
//
//@Composable
//
//fun HomePage(navHostController: NavHostController, viewModel: ViewModel){
//
//
//    val authState = viewModel.authState.observeAsState()
//
//    LaunchedEffect(authState.value) {
//        when(authState.value){
//            is AuthState.Unauthenticated -> navHostController.navigate("login")
//            else -> Unit
//        }
//    }
//
//    var name by remember {
//        mutableStateOf("")
//    }
//
//    var phone by remember {
//        mutableStateOf("")
//    }
//
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Home Page", fontSize = 32.sp)
//
//        OutlinedTextField(value = name, onValueChange ={name=it} , label = { Text(text = "Person name")})
//        OutlinedTextField(value = phone, onValueChange ={phone=it}, label = { Text(text = "Money")} )
////        Button(onClick = {
////            viewModel.addContact(name, phone)
////            name=""
////            phone=""
////        }) {
////            Text(text = "Add contact")
////        }
////        Button(onClick = {
////            viewModel.addName(name)
////            name=""
////            phone=""
////        }) {
////            Text(text = "Add Name")
////        }
//
//        Button(onClick = {
//            viewModel.addUdhari(name,phone)
//        }) {
//            Text(text = "Add udhaar")
//        }
//
//        Button(onClick = { navHostController.navigate("udhaar") }) {
//            Text(text = "Go to udhaari page")
//        }
//
//        TextButton(onClick = {
//            viewModel.signOut()
//        }) {
//            Text(text = "Sign out")
//        }
//    }
//}