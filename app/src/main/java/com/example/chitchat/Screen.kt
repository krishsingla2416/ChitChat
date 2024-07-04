package com.example.chitchat

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddCustomer : Screen("add_customer")
    data object OldCustomers : Screen("old_customers")
    data object EditTransaction : Screen("edit_screen")
    data object CustomerTransactions : Screen("customer_transactions/{userId}") // Use dynamic route
}