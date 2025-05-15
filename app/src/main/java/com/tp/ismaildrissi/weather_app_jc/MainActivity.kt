package com.tp.ismaildrissi.weather_app_jc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(applicationContext)

        setContent {
            var city by remember { mutableStateOf("") }
            var meteo by remember { mutableStateOf<MeteoItem?>(null) }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFE3F2FD)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Weather App",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF0288D1),
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedLabelColor = Color.DarkGray,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            fetchWeather(city) { item -> meteo = item }
                        },
                        modifier = Modifier.fillMaxWidth(0.5f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF03A9F4),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Chercher", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    meteo?.let {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            MeteoScreen(it)
                        }
                    }
                }
            }
        }

    }

    private fun fetchWeather(city: String, onResult: (MeteoItem) -> Unit) {
        val apiKey = "<api-key>"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val main = jsonObject.getJSONObject("main")
                    val wind = jsonObject.getJSONObject("wind")
                    val weather = jsonObject.getJSONArray("weather").getJSONObject(0)

                    val date = Date(jsonObject.getLong("dt") * 1000)
                    val dateString = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)

                    val item = MeteoItem(
                        temperature = (main.getDouble("temp") - 273.15).toInt(),
                        tempMax = (main.getDouble("temp_max") - 273.15).toInt(),
                        tempMin = (main.getDouble("temp_min") - 273.15).toInt(),
                        pression = main.getInt("pressure"),
                        humidite = main.getInt("humidity"),
                        vent = wind.getDouble("speed"),
                        meteo = weather.getString("main"),
                        date = dateString
                    )
                    onResult(item)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }
}