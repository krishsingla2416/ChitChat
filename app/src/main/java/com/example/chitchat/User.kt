package com.example.chitchat

data class Transaction(
    val amount: String = "",
    val date: String = "",
    val type: String = "",// "sent" or "received"
    var billUrl : String=""
)

data class User(
    val id: String="",
    val name: String="",
    val transactions: MutableMap<String, Transaction> = mutableMapOf(), // Map of transactions with date as key
    var sum: String = "0"
)
