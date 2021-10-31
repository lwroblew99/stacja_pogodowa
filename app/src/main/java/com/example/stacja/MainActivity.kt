package com.example.stacja

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.example.stacja.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {


    //private lateinit var database: DatabaseReference
    //private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var tvTemperature: TextView
    private lateinit var tvCisnienie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCisnienie = findViewById(R.id.tvCisnienie)
        tvTemperature = findViewById(R.id.tvTemp)

        val database =  Firebase.database("https://weather-from-arduino-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val Cisnienie: String = dataSnapshot.child("Cisnienie").getValue().toString()
                val Temperatura: String = dataSnapshot.child("Temperatura").getValue().toString()
                tvCisnienie.setText(Cisnienie)
                tvTemperature.setText(Temperatura)

            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

            }




        }



















