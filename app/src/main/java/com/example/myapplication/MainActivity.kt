package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Model.WeatherVO
import com.example.myapplication.Network.NetworkTask
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    internal lateinit var btns: Button
    internal var tem: TextView? = null
    internal var edit: EditText? = null
    internal var imageView: ImageView? = null


    var cityre : String? = "seoul"
    var countryre : String? = "korea"
    var CITY: String = cityre + "," + countryre
    val API: String = "8118ed6ee68db2debfaaa5a44c832918"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "날씨정보 확인 메인페이지"

        val country = resources.getStringArray(R.array.country)

// access the spinner
        val spinnercountry = findViewById<Spinner>(R.id.spinnercountry)
        val spinnercity = findViewById<Spinner>(R.id.spinnercity)
        var clickable : Int = 0

        val countries = resources.getStringArray(R.array.country)

        input.setOnClickListener{
            if(clickable!=0){
                countryre = spinnercountry.getSelectedItem().toString()
                cityre = spinnercity.getSelectedItem().toString()
                CITY = cityre + "," + countryre

                weatherTask().execute()
            }else{
                Toast.makeText(this@MainActivity, "국가와 도시를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        if (spinnercountry != null) {
            val country = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, country)
            spinnercountry.adapter = country


            clickable = 1
            spinnercountry.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {


                override fun onItemSelected(parent: AdapterView<*>, arg1: View?, pos: Int,
                                            arg3: Long) {
// parent.getItemAtPosition(pos)
                    System.out.println("확인?")
                    clickable = pos
                    when(pos){
                        0 -> {
                            val city = ArrayAdapter
                                    .createFromResource(this@MainActivity, R.array.city_no,
                                            android.R.layout.simple_spinner_item)
                            spinnercity.adapter = city
                        }

                        1-> {
                            val city = ArrayAdapter
                                    .createFromResource(this@MainActivity, R.array.city_korea,
                                            android.R.layout.simple_spinner_item)
                            spinnercity.adapter = city
                        }

                        2-> {
                            val city = ArrayAdapter
                                    .createFromResource(this@MainActivity, R.array.city_china,
                                            android.R.layout.simple_spinner_item)
                            spinnercity.adapter = city
                        }

                        3-> {
                            val city = ArrayAdapter
                                    .createFromResource(this@MainActivity, R.array.city_japan,
                                            android.R.layout.simple_spinner_item)
                            spinnercity.adapter = city
                        }
                    }

                }


                override fun onNothingSelected(parent: AdapterView<*>) {
// write code to perform some action
                }
            }
        }


        //weatherTask()에서 실행한 결과값을 담아온다.
        val WeatherResult = weatherTask().execute().get()
          val target = "temp"
        val target_num = WeatherResult.indexOf(target)
        var a: String?= null
         a = WeatherResult.substring(target_num, WeatherResult.substring(target_num).indexOf(",") + target_num)
        val result: String = a!!.replace("temp\":", "") //결과값의 온도값만 추출

       // val c =Integer.parseInt(result)
        val c :Double = result.toDouble()


        var resultTem: String = ""
        if(c >=0){
            resultTem = "winter"
        }else{
            resultTem = "summer"
        }
            val params = HashMap<String, String>()
            params.put("MinSung","weather")
            params.put("tem",resultTem) //온도값 전송

            val networkTask = NetworkTask()  //결과값 받아오는 부분
            var msg: String? = null
            try {
                msg = networkTask.execute(params).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val gson = GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create()

            val weatherlist: TypeToken<List<WeatherVO?>?> = object : TypeToken<List<WeatherVO?>?>() {}
            val results: String = msg!!.replace("null", "")
            val ListVO_List: List<WeatherVO> = gson.fromJson(results, weatherlist.getType())


        ////////////////////////////////////



            if (ListVO_List.isEmpty()) {
                Toast.makeText(this@MainActivity, "등록된 의상이 없습니다.", Toast.LENGTH_LONG).show()
            } else {
                //이미지 불러와서 뿌리는 공간

                for (weatherVO in ListVO_List) {

                    val top = findViewById<LinearLayout>(R.id.Top)
                    val topimage = ImageView(this)

                    val topUrl : String? = weatherVO.top
                    val pantsUrl : String? = weatherVO.pants
                    println("pantsUrl@@@@@@@@@@@@@@@@@@@@@@@@"+pantsUrl)
                            Glide.with(this).load(topUrl).into(topimage)
                    top.addView(topimage)




                    val pants = findViewById<LinearLayout>(R.id.pants)
                    val pantsimage = ImageView(this)

                    Glide.with(this).load(pantsUrl).into(pantsimage)
                    pants.addView(pantsimage)



   //                 var bundle : Bundle ?= intent.extras
//                    var cityre = bundle!!.getString("city_send")
  //                  var countryre = bundle!!.getString("country_send")
                    //CITY = cityre + "," + countryre

   //                 weatherTask().execute()
                    //작업중인 코드

                }
                Toast.makeText(this@MainActivity, "성공", Toast.LENGTH_SHORT).show()
            }


        /*btns.setOnClickListener {
        //버튼 수정 하는 구간 !!!!!!!!!!!!!!!!!!!!!!!!!!
            Toast.makeText(this@MainActivity, "이곳은 Spinner 재설정시 새로 값 받아오는 부분", Toast.LENGTH_SHORT).show()
        }*/

    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            var send:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8)

            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"



                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                val tempmin = tempMin
                val tempmax = tempMax
                val windput = windSpeed



                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                val ha = findViewById<TextView>(R.id.temp).text as String?
                var tems: String = ha!!.replace("°C", "")
                println("ha값##########################"+tems)
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }

    }

}

private operator fun <K, V> HashMap<K, V>.set(v: V, value: V) {

}
