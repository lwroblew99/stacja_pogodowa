package com.example.stacja

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class OpenWeather : Fragment() {
    //private lateinit var database: DatabaseReference
    //private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var tvTemperature: TextView
    private lateinit var tvCisnienie: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    lateinit var button: Button
    var CITY: String = "Poznan"
    val API: String = "538641b64c380fbc31725377e486d0c1"
    val localDateNow = LocalDate.now()
    val localTimeNow = LocalTime.now()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            weatherTask().execute()



    }

            inner class weatherTask() : AsyncTask<String, Void, String>() {

                override fun onPreExecute() {
                    super.onPreExecute()

                    view!!.findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE

                    view!!.findViewById<TextView>(R.id.errortext).visibility = View.GONE
                }

                override fun doInBackground(vararg p0: String?): String? {
                    var response: String?
                    try {
                        response =
                            URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                                .readText(Charsets.UTF_8)
                    } catch (e: Exception) {
                        response = null
                    }
                    return response
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    try {
                        val jsonObj = JSONObject(result)
                        val main = jsonObj.getJSONObject("main")
                        val sys = jsonObj.getJSONObject("sys")
                        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                        val updatedAt: Long = jsonObj.getLong("dt")
                        val updatedAtText =
                            "Updated at: " + SimpleDateFormat(
                                "dd/MM/yyyy hh:mm a",
                                Locale.ENGLISH
                            ).format(
                                Date(updatedAt * 1000)
                            )
                        val temp = main.getString("temp") + "°C"
                        val tempMin = main.getString("temp_min") + "°C"
                        val tempMax = main.getString("temp_max") + "°C"
                        val pressure = main.getString("pressure")
                        var humidity = main.getString("humidity")
                        val sunrise: Long = sys.getLong("sunrise")
                        val sunset: Long = sys.getLong("sunset")
                        val weatherDescription = weather.getString("description")
                        val address = jsonObj.getString("name") + ", " + sys.getString("country")

                        view!!.findViewById<TextView>(R.id.temp_outside).text = temp
                        view!!.findViewById<TextView>(R.id.pressure).text = pressure
                        view!!.findViewById<TextView>(R.id.humidity).text = humidity

                        view!!.findViewById<TextView>(R.id.errortext).visibility = View.GONE

                    } catch (e: Exception) {
                        view!!.findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE


                    }


                }


            }

        }