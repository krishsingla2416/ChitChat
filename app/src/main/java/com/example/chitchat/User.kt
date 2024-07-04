package com.example.chitchat

import com.google.firebase.database.Exclude

data class Transaction(
    val amount: String = "",
    val date: String = "",
    val type: String = "" // "sent" or "received"
)

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val transactions: MutableList<Transaction> = mutableListOf()

)

