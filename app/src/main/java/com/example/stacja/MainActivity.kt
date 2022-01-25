package com.example.stacja
import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.jar.Manifest
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

//https://home.openweathermap.org/api_keys
class MainActivity : AppCompatActivity() {


    //private lateinit var database: DatabaseReference
    //private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var tvTemperature: TextView
    private lateinit var tvCisnienie: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvJakosc: TextView
    private lateinit var tvWilgotnosc: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var currentLocation: Location? = null


    var city: String = "Poznan"
    val API: String = "538641b64c380fbc31725377e486d0c1"
    val useruid: String ="X3hgaV4OrMfYSMh0HjkWEkncAN13"
    val webApi:String = "AIzaSyBUNk2SUBSJx78jNwUUKikIUP8udhFBYj0"

    var PERMISSION_ID = 1000
    //var lat=intent.getStringExtra("lat")
    //var long=intent.getStringExtra("long")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.swiperefresh)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()
        firebasedatabase()
        weather().execute()

        swipeRefreshLayout.setOnRefreshListener {



            checkLocationPermission()
            firebasedatabase()
            weather().execute()
            swipeRefreshLayout.setRefreshing(false)

        }

    }



    fun firebasedatabase(){
        val database =
            Firebase.database("https://esp-firebase-demo-a3e9b-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val Cisnienie: String =
                    dataSnapshot.child("Weather_now").child("Cisnienie").value.toString()
                val Temperatura: String =
                    dataSnapshot.child("Weather_now").child("Temperatura").value.toString()
                val Jakosc: String =
                    dataSnapshot.child("Weather_now").child("Jakosc").value.toString()
                val Wilgotnosc: String =
                    dataSnapshot.child("Weather_now").child("Wilgotnosc").value.toString()
                tvCisnienie = findViewById(R.id.pressure)
                tvTemperature = findViewById(R.id.temp_inside)
                tvJakosc = findViewById(R.id.air_quality)
                tvWilgotnosc = findViewById(R.id.humidity)
                tvCisnienie.setText(Cisnienie)
                tvTemperature.setText(Temperatura)
                tvJakosc.setText(Jakosc)
                tvWilgotnosc.setText(Wilgotnosc)

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(localClassName,"database error")
            }
        })
    }

   private fun checkLocationPermission(){
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return

        }
        task.addOnSuccessListener {
            if(it != null){
                currentLocation = it
            }
            else { Toast.makeText(this, "Brak dostępu do lokalizacji", Toast.LENGTH_SHORT).show()}
        }


    }
    /*fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }*/

    private fun isLocationEnabled():Boolean{
        var locationManager = getSystemService(LOCATION_SERVICE)as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    inner class weather() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE
            findViewById<LinearLayout>(R.id.main).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            var image: String?
            response = try {
                if(currentLocation != null)
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=${currentLocation?.latitude}&lon=${currentLocation?.longitude}&appid=$API")
                        .readText(Charsets.UTF_8)
                else
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtDate =
                    SimpleDateFormat(
                        "dd.MM.yyyy",
                        Locale.ENGLISH
                    ).format(
                        Date(updatedAt * 1000)
                    )
                val updatedAtHour =
                    SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(
                    Date(updatedAt * 1000)
                )
                val temp = main.getString("temp") + "°C"
                val tempMin = main.getString("temp_min") + "°C"
                val tempMax = main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val weatherDescription = weather.getString("description")
                val windspeed = wind.getString("speed")
                val address = jsonObj.getString("name")
                val icon = weather.getString("icon")


                val iconUrl = "https://openweathermap.org/img/w/"+icon+".png"
                val imageView: ImageView = findViewById(R.id.image)

                Picasso.get().load(iconUrl).into(imageView)

                val currentTime = LocalDateTime.now()
                val formaterr = DateTimeFormatter.ofPattern("HH:mm")
                val formated = currentTime.format(formaterr)
                findViewById<TextView>(R.id.time).text = formated.toString()
                findViewById<TextView>(R.id.TCity).text = address
                findViewById<TextView>(R.id.temp_outside).text = temp
                //findViewById<TextView>(R.id.pressure).text = pressure
                //findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.wind).text = windspeed
                findViewById<TextView>(R.id.data).text = updatedAtDate
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                //findViewById<TextView>(R.id.time).text = updatedAtHour
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(sunrise * 1000)
                    )
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(sunset * 1000)
                    )
                findViewById<TextView>(R.id.mintemp).text = tempMin

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<LinearLayout>(R.id.main).visibility = View.VISIBLE
                findViewById<TextView>(R.id.errortext).visibility = View.GONE

            } catch (e: Exception) {

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<LinearLayout>(R.id.main).visibility = View.GONE
                findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE

            }

        }
    }
}



















