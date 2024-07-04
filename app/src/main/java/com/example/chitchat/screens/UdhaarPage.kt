//package com.example.chitchat.screens
//
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.example.chitchat.ViewModel
//@Composable
//fun UdhaarPage(navHostController: NavHostController, viewModel: ViewModel) {
//    Text(text = "hi", fontSize = 50.sp)
//  val personsList by viewModel.personslist.observeAsState()
////
////    Log.d("krish", personsList.toString())
//
////    personsList?.let { map ->
////        LazyColumn(
////            modifier = Modifier.fillMaxSize(),
////            contentPadding = PaddingValues(16.dp),
////            verticalArrangement = Arrangement.spacedBy(8.dp)
////        ) {
////            map.forEach { (person, udharList) ->
////                item {
////                    Text(
////                        text = person,
////                        //style = MaterialTheme.typography.h6,
////                        modifier = Modifier.padding(vertical = 8.dp)
////                    )
////                }
////                items(udharList) { udhar ->
////                    Card(
////                        modifier = Modifier
////                            .fillMaxWidth()
////                            .padding(4.dp),
////                        //elevation = 4.dp
////                    ) {
////                        Text(
////                            text = udhar,
////                            modifier = Modifier.padding(16.dp)
////                        )
////                    }
////                }
////            }
////        }
////    } ?: run {
////        Text(text = "No data available", modifier = Modifier.padding(16.dp))
////    }
//}