package com.example.avitoweather


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.avitoweather.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private var city = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCityIb.setOnClickListener {
            if (binding.group.isVisible) {
                binding.group.visibility = View.GONE
            } else {
                binding.group.visibility = VISIBLE
            }
        }
        binding.okCityIb.setOnClickListener {

            if (TextUtils.isEmpty(binding.CityEditText.text)) {
                Toast.makeText(
                    this,
                    getString(R.string.EnterTheText),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                city = binding.CityEditText.text.toString()
                binding.CityTv.text = city
                binding.group.visibility = View.GONE

                with(binding.CityEditText) {
                    setText("")
                    clearFocus()
                    hideKeyboard(this)
                }

                val t1 = thread {
                    val key = "6cd0e358e2217e40eb1b7446f416fe8b"
                    val url =
                        "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$key&units=metric&lang=ru"

                    val apiResponse = URL(url).readText()
                    Log.d("INFO", apiResponse)
                    val weather = JSONObject(apiResponse).getJSONArray("weather")
                    val desc = weather.getJSONObject(0).getString("description")
                    val main = JSONObject(apiResponse).getJSONObject("main")
                    val temp = main.getString("temp")

                    binding.apply {
                        temperatureTv.text = temp
                        descriptionOfTheWeatherTv.text = desc
                        when (descriptionOfTheWeatherTv.text) {
                            "ясно" -> imageView.setImageResource(R.drawable.sunny_sunshine)
                            "пасмурно" -> imageView.setImageResource(R.drawable.overcast_cloudy)
                            "дождь" -> imageView.setImageResource(R.drawable.rain_wather)
                            "град" -> imageView.setImageResource(R.drawable.hail)
                            "гроза" -> imageView.setImageResource(R.drawable.weather_storms)
                            "облачно с прояснениями" -> imageView.setImageResource(R.drawable.sunny_sun_cloud)

                        }
                    }
                }
                t1.join()
            }
        }
    }
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

