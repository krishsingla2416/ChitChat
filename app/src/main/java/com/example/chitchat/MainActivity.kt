package com.example.chitchat

//import UserListScreen
//import UserListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userViewModel : UserViewModel by viewModels()
        FirebaseApp.initializeApp(this)
        setContent {

            MyApp(userViewModel = userViewModel)

        }
    }
}

