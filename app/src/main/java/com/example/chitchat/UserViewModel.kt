package com.example.chitchat

import android.content.Context

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var usersCollection: CollectionReference
    val totalBalance: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var totalBalanceFB: DocumentReference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    var currPerson = "-1"
    val storage = FirebaseStorage.getInstance()
    val users: MutableLiveData<Map<String, User>> = MutableLiveData(emptyMap())


    init {
        checkAuthStatus()
    }


    private fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            currPerson = auth.currentUser!!.email.toString()
            usersCollection = db.collection(currPerson)
            totalBalanceFB = db.collection(currPerson).document("total")
            fetchUsers()
            fetchTotalBalance()
        }
    }

    suspend fun uploadImageToFirebase(
        uri: Uri,
        context: Context,
        userId: String
        //date: String
    ): String {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("billImages/$userId/${uri.lastPathSegment}")

        return try {
            // Upload the file to Firebase Storage
            storageRef.putFile(uri).await()
            // Get the download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            // Update transaction's billUrl in the ViewModel
            //users.value?.get(userId)?.transactions?.get(date)?.billUrl = downloadUrl
            // Inform UI or ViewModel about success (consider using LiveData or callback)
            Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show()
            downloadUrl
        } catch (e: Exception) {
            // Handle specific exceptions or log errors
            Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT).show()
            "error"
        }
    }


    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _authState.value = AuthState.Authenticated
                currPerson = email
                usersCollection = db.collection(currPerson)
                totalBalanceFB = db.collection(currPerson).document("total")
                fetchUsers()
                fetchTotalBalance()
            } else {
                _authState.value = AuthState.Error(it.exception?.message ?: "Error !")
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _authState.value = AuthState.Authenticated
                currPerson = email
                usersCollection = db.collection(currPerson)
                totalBalanceFB = db.collection(currPerson).document("total")
                fetchUsers()
                fetchTotalBalance()
            } else {
                _authState.value = AuthState.Error(it.exception?.message ?: "Error !")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private fun fetchTotalBalance() {
        totalBalanceFB.get().addOnSuccessListener { document ->
            totalBalance.value = document.toObject(Total::class.java)?.total ?: 0
        }.addOnFailureListener {
            totalBalance.value = 0
        }
    }




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
                            continuation.resume(User("-1", "-"))
                        }
                    } else {
                        continuation.resume(User("-1", "-"))
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

            users.value = userMap - "total"

        }
    }


    fun addUser(user: User): String {
        if (users.value?.containsKey(user.id) == true || user.name == "" || invalid(user.id)) {
            return "error"
        }
        viewModelScope.launch {
            usersCollection.document(user.id).set(user)
            // Update the LiveData map
            val currentUsers = users.value ?: emptyMap()
            users.value = currentUsers + (user.id to user)

        }
        return "okay"
    }

    private fun invalid(number: String): Boolean {
        if (number == ""||number=="0") {
            return true
        }
        for (i in number) {
            if (i in '0'..'9') {
                continue
            } else {
                return true
            }
        }
        return false
    }


    fun deleteUser(userId: String) {
        viewModelScope.launch {
            usersCollection.document(userId).delete()
            // Update the LiveData map
            totalBalance.value =
                users.value?.get(userId)?.sum?.let { totalBalance.value?.minus(it.toInt()) }
            totalBalance.value?.let { Total(it) }?.let { totalBalanceFB.set(it) }
            val currentUsers = users.value ?: emptyMap()
            users.value = currentUsers - userId
        }
    }

//    fun editUser(user: User) {
//        viewModelScope.launch {
//            usersCollection.document(user.id).set(user)
//            // Update the LiveData map
//            val userId = user.id
//            totalBalance.value =
//                users.value?.get(userId)?.sum?.let { totalBalance.value?.minus(it.toInt()) }
//            val currentUsers = users.value ?: emptyMap()
//            users.value = currentUsers + (user.id to user)
//            totalBalance.value =
//                users.value?.get(userId)?.sum?.let { totalBalance.value?.plus(it.toInt()) }
//            totalBalance.value?.let { Total(it) }?.let { totalBalanceFB.set(it) }
//        }
//    }

    fun addTransaction(userId: String, transaction: Transaction): String {
        // Update local state first for immediate UI refresh
        if (invalid(transaction.amount)) {
            return "error"
        }
        val currentUsers = users.value?.toMutableMap() ?: mutableMapOf()
        val user = currentUsers[userId]
        if (user != null) {
            user.transactions[transaction.date] = transaction
            var transactionAmount = transaction.amount.toIntOrNull() ?: 0
            if (transaction.type == "sent") {
                transactionAmount = -(transactionAmount)
            }
            user.sum = (user.sum.toInt() + transactionAmount).toString()
            currentUsers[userId] = user
            users.value = currentUsers
            totalBalance.value = totalBalance.value?.plus(transactionAmount)
            totalBalance.value?.let { Total(it) }?.let { totalBalanceFB.set(it) }
        }

        // Sync with Firestore in the background
        viewModelScope.launch {
            val userRef = usersCollection.document(userId)
            userRef.update(
                mapOf(
                    "transactions.${transaction.date}" to transaction,
                    "sum" to user?.sum
                )
            ).addOnFailureListener { e ->
                // Handle the error
                Log.e("Firestore", "Error updating document", e)
            }
        }
        return "okay"
    }

    fun deleteTransaction(userId: String, transaction: Transaction): String {
        // Update local state first for immediate UI refresh
        val currentUsers = users.value?.toMutableMap() ?: mutableMapOf()
        val user = currentUsers[userId]
        if (user != null) {
            user.transactions.remove(transaction.date)
            var transactionAmount = transaction.amount.toIntOrNull() ?: 0
            if (transaction.type == "sent") {
                transactionAmount = -(transactionAmount)
            }
            user.sum = (user.sum.toInt() - transactionAmount).toString()
            currentUsers[userId] = user
            users.value = currentUsers
            totalBalance.value = totalBalance.value?.minus(transactionAmount)
            totalBalance.value?.let { Total(it) }?.let { totalBalanceFB.set(it) }
        }

        // Sync with Firestore in the background
        viewModelScope.launch {
            val userRef = usersCollection.document(userId)
            userRef.update(
                mapOf(
                    "transactions.${transaction.date}" to FieldValue.delete(),
                    "sum" to user?.sum
                )
            ).addOnFailureListener { e ->
                // Handle the error
                Log.e("Firestore", "Error updating document", e)
            }
        }
        return "okay"
    }

    fun editTransaction(
        userId: String,
        //oldTransaction: Transaction,
        date:String,
        newTransaction: Transaction
    ): String {
        // Update local state first for immediate UI refresh

        val currentUsers = users.value?.toMutableMap() ?: mutableMapOf()
        val oldTransaction= currentUsers[userId]?.transactions?.get(date)!!
        val user = currentUsers[userId]
        if (user != null) {
            //user.transactions.remove(oldTransaction.date)
            user.transactions[date] = newTransaction
            var oldTransactionAmount = oldTransaction.amount.toIntOrNull() ?: 0
            if (oldTransaction.type == "sent") {
                oldTransactionAmount = -(oldTransactionAmount)
            }
            var newTransactionAmount = newTransaction.amount.toIntOrNull() ?: 0
            if (newTransaction.type == "sent") {
                newTransactionAmount = -(newTransactionAmount)
            }
            user.sum = (user.sum.toInt() - oldTransactionAmount + newTransactionAmount).toString()
            currentUsers[userId] = user
            users.value = currentUsers
            totalBalance.value =
                totalBalance.value?.minus(oldTransactionAmount)?.plus(newTransactionAmount)
            totalBalance.value?.let { Total(it) }?.let { totalBalanceFB.set(it) }
        }

        // Sync with Firestore in the background
        viewModelScope.launch {
            val userRef = usersCollection.document(userId)
            userRef.update(
                mapOf(
                    "transactions.${oldTransaction.date}" to FieldValue.delete(),
                    "transactions.${newTransaction.date}" to newTransaction,
                    "sum" to user?.sum
                )
            ).addOnFailureListener { e ->
                // Handle the error
                Log.e("Firestore", "Error updating document", e)
            }
        }
        return "okay"
    }



}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}