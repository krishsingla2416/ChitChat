package com.example.chitchat

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

    init {
        fetchUsers()
    }
   //val users: MutableMap<String, User> = mutableMapOf()
   val users: MutableLiveData<Map<String, User>> = MutableLiveData(emptyMap())



    suspend fun getUserById(userId: String): User {
        return suspendCancellableCoroutine { continuation ->
            val userRef = usersCollection.document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject<User>()
                        if (user != null) {
                            continuation.resume(user)
                        } else {
                            continuation.resume(User("-1", "-", "-", mutableListOf()))
                        }
                    } else {
                        continuation.resume(User("-1", "-", "-", mutableListOf()))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }




    private fun fetchUsers() {
        usersCollection.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                users.value = emptyMap() // Handle error case
                return@addSnapshotListener
            }

            // Populate users map with fetched data
            val userMap = snapshot.documents.mapNotNull { document ->
                val userId = document.id
                val user = document.toObject(User::class.java)?.copy(id = userId)
                if (user != null) {
                    userId to user
                } else {
                    null
                }
            }.toMap()

            users.value = userMap
        }
    }


    fun addUser(user: User) {
        viewModelScope.launch {
            usersCollection.document(user.id).set(user)
            // Update the LiveData map
            val currentUsers = users.value ?: emptyMap()
            users.value = currentUsers + (user.id to user)
        }
    }


    fun deleteUser(userId: String) {
        viewModelScope.launch {
            usersCollection.document(userId).delete()
            // Update the LiveData map
            val currentUsers = users.value ?: emptyMap()
            users.value = currentUsers - userId
        }
    }
    fun editUser(user: User) {
        viewModelScope.launch {
            usersCollection.document(user.id).set(user)
            // Update the LiveData map
            val currentUsers = users.value ?: emptyMap()
            users.value = currentUsers + (user.id to user)
        }
    }


    fun addTransaction(userId: String, transaction: Transaction) {
        viewModelScope.launch {
            val userRef = usersCollection.document(userId)
            userRef.update("transactions", FieldValue.arrayUnion(transaction))
            // Update the LiveData map
            val currentUsers = users.value?.toMutableMap() ?: mutableMapOf()
            currentUsers[userId]?.transactions?.add(transaction)
            users.value = currentUsers
        }
    }


    fun deleteTransaction(userId: String, transaction: Transaction) {
        viewModelScope.launch {
            val userRef = usersCollection.document(userId)
            userRef.update("transactions", FieldValue.arrayRemove(transaction))
            // Update the LiveData map
            val currentUsers = users.value?.toMutableMap() ?: mutableMapOf()
            currentUsers[userId]?.transactions?.remove(transaction)
            users.value = currentUsers
        }
    }


    fun editTransaction(userId: String, oldTransaction: Transaction, newTransaction: Transaction) {
        viewModelScope.launch {
            deleteTransaction(userId, oldTransaction)
            addTransaction(userId, newTransaction)
        }
    }
}
