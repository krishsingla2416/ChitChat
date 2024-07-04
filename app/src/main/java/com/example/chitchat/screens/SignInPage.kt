//package com.example.chitchat.screens
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.example.chitchat.AuthState
//import com.example.chitchat.ViewModel
//
//@Composable
//fun SignInPage(navHostController: NavHostController, viewModel: ViewModel){
//    var email by remember {
//        mutableStateOf("")
//    }
//    var password by remember {
//        mutableStateOf("")
//    }
//
//    val authState = viewModel.authState.observeAsState()
//    val context = LocalContext.current
//
//    LaunchedEffect(authState.value) {
//        when(authState.value){
//            is AuthState.Authenticated -> navHostController.navigate("home")
//            is AuthState.Error -> Toast.makeText(context,
//                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
//            else -> Unit
//        }
//    }
//
//
//    Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
//        Spacer(modifier = Modifier.height(200.dp))
//        Text(text = "Sign in Page")
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        TextField(value = email, onValueChange = {email=it}, label = { Text(text = "Email")})
//        TextField(value = password, onValueChange = {password=it}, label = { Text(text = "Password")})
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Button(onClick = {
//            viewModel.signIn(email,password) },
//            enabled = (authState.value != AuthState.Loading)) {
//            Text(text = "Sign in")
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        TextButton(onClick = {
//            navHostController.navigate("login")
//        }) {
//            Text(text = "Already have an account, Login")
//        }
//
//    }
//}
