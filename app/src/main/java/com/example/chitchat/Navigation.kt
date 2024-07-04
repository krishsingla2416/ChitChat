//package com.example.chitchat
//
//import com.example.chitchat.UserViewModel
//import androidx.compose.runtime.Composable
//import androidx.lifecycle.ViewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
////import com.example.chitchat.screens.HomePage
////import com.example.chitchat.screens.LoginPage
////import com.example.chitchat.screens.SignInPage
////import com.example.chitchat.screens.UdhaarPage
//
//@Composable
//fun Navigation(viewModel: com.example.chitchat.UserViewModel) {
//    val navController =rememberNavController()
//
//    NavHost(navController = navController, startDestination = "login") {
//        composable("login"){
//            LoginPage(navHostController = navController, viewModel = viewModel)
//        }
//        composable("signin"){
//            SignInPage(navHostController = navController, viewModel =viewModel )
//        }
//        composable("home"){
//            HomePage(navHostController = navController, viewModel = viewModel)
//        }
//
//        composable("udhaar"){
//            UdhaarPage(navHostController = navController, viewModel = viewModel)
//        }
//    }
//}