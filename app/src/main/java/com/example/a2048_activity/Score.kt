package com.example.a2048_activity

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Score {
    private var score: Int = 0
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val scoreRef: DatabaseReference = database.getReference("score")

    /*init {
        // Recupera el puntaje inicialmente desde Firebase al iniciar la clase Score
        scoreRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Intenta convertir el valor de Firebase a Int
                val firebaseScore = dataSnapshot.getValue(Long::class.java)
                firebaseScore?.let {
                    score = it.toInt()
                }
            }
        }
    }*/

    fun getScore(): Int {
        return score
    }

    fun updateScore(combinedValue: Int) {
        score += combinedValue
        // Actualiza el puntaje en Firebase
        scoreRef.setValue(score)
    }

    fun resetScore() {
        score = 0
        // Reinicia el puntaje en Firebase
        scoreRef.setValue(0)
    }
}
