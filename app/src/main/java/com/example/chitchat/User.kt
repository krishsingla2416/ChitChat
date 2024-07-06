package com.example.chitchat

data class Transaction(
    val amount: String = "",
    val date: String = "",
    val type: String = "" ,// "sent" or "received"
    val billUrl : String=""
)

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val transactions: MutableList<Transaction> = mutableListOf(),
    var sum : String ="0"
)

