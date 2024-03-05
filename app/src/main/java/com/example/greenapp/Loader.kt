package com.example.greenapp

import android.content.res.Resources
import com.example.greenapp.Model.FirebaseModel
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Tip
import com.google.gson.Gson
import com.example.greenapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Loader {

    data class AllData(
        val data: List<Tip>,
    )

    fun loadAndSaveAllTips(resources: Resources) {
        val g = Gson()
        resources.openRawResource(R.raw.data)
            .bufferedReader()
            .use {
                val allLines = it.readText()
                val allData = g.fromJson(allLines, AllData::class.java)
                for (something in allData.data) {
                    something.addedAt = System.currentTimeMillis()
                    Firebase.firestore
                        .collection(FirebaseModel.TIPS_COLLECTION_PATH)
                        .document(something.id)
                        .set(something)
                }
            }
    }
}