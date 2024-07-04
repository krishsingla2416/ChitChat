//package com.example.chitchat
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.CollectionReference
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ViewModel:ViewModel(){
//
//    private val auth : FirebaseAuth=FirebaseAuth.getInstance()
//    private val _authState =MutableLiveData<AuthState>()
//    val authState : LiveData<AuthState> = _authState
//
//    // on below line creating an instance of firebase firestore.
//    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//    //creating a collection reference for our Firebase Firestore database.
//    //val dbPersons: CollectionReference = db.collection("Persons")
//    //adding our data to our courses object class.
//    private val usersCollection = db.collection("users")
//    var personslist= MutableLiveData<MutableMap<String,MutableList<String>>>()
//    var currPerson="-1"
//
//    init {
//        checkAuthStatus()
//    }
//
//    private fun checkAuthStatus(){
//        if(auth.currentUser==null){
//            _authState.value=AuthState.Unauthenticated
//        }else{
//            _authState.value=AuthState.Authenticated
//            currPerson= auth.currentUser!!.email.toString()
//        }
//    }
//
//    fun login(email : String,password:String){
//        if(email.isEmpty()||password.isEmpty()){
//            _authState.value=AuthState.Error("Email or password can't be empty")
//            return
//        }
//        _authState.value=AuthState.Loading
//        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
//            if(it.isSuccessful){
//                _authState.value=AuthState.Authenticated
//            }else{
//                _authState.value=AuthState.Error(it.exception?.message?:"Error !")
//            }
//        }
//    }
//
//    fun signIn(email : String,password:String){
//        if(email.isEmpty()||password.isEmpty()){
//            _authState.value=AuthState.Error("Email or password can't be empty")
//            return
//        }
//        _authState.value=AuthState.Loading
//        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
//            if(it.isSuccessful){
//                _authState.value=AuthState.Authenticated
//
//            }else{
//                _authState.value=AuthState.Error(it.exception?.message?:"Error !")
//            }
//        }
//    }
//
//    fun signOut(){
//        auth.signOut()
//        _authState.value=AuthState.Unauthenticated
//    }
//
////    fun addContact(name:String,phone:String){
////        dbPersons.add(personInfo(name, phone))
////    }
////
////    fun addName(name:String){
////        dbPersons.add(personName(name))
////    }
//
//    fun addUdhari(name:String,money : String){
//        val dbPerson: CollectionReference = db.collection(name)
//        dbPerson.add(Udhaar(money))
//        //getUdhaariList(name)
//       personslist.value?.get(name)?.add(money)
//    }
//
////    fun getUdhaariList(person :String){
////        db.collection(person).get().addOnSuccessListener {
////            if(!it.isEmpty){
////                val list=it.documents
////                personslist.value?.get(person)?.clear()
////                for(d in list){
////                    val c: Udhaar? = d.toObject(Udhaar::class.java)
////                    if (c != null) {
////                        personslist.value?.get(person)?.add(c.udhar)
////                    }
////                }
////            }
////        }
////    }
////fun getUdhaariList(person: String) {
////    db.collection(person).get().addOnSuccessListener {
////        if (!it.isEmpty) {
////            val list = it.documents
////            val updatedMap = personslist.value?.toMutableMap() ?: mutableMapOf()
////            val personList = updatedMap[person] ?: mutableListOf()
////            personList.clear()
////            for (d in list) {
////                val c: Udhaar? = d.toObject(Udhaar::class.java)
////                if (c != null) {
////                    personList.add(c.udhar)
////                }
////            }
////            updatedMap[person] = personList
////            personslist.value = updatedMap
////        }
////    }
////}
//
//}
//
//sealed class AuthState{
//    object Authenticated : AuthState()
//    object Unauthenticated: AuthState()
//    object Loading : AuthState()
//    data class Error(val message : String) : AuthState()
//}