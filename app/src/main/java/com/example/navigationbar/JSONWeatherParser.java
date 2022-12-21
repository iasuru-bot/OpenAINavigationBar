package com.example.navigationbar;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONWeatherParser {
    public static Weather getWeather(String data) throws JSONException {
        Weather weather = new Weather();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);


        // We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("list");
        //Log.e("jsonarray", jArr.toString());
        // We use only the first value

        //JSONObject JSONWeather = jArr.getJSONObject(0);
        //Log.e("jsonobject",JSONWeather.toString());
        //for (int i = 0; i < jArr.length(); i++) {
            JSONObject JSONWeather = jArr.getJSONObject(0);
            Log.e("jsondei",JSONWeather.toString());
            Log.e("weather1",JSONWeather.getString("dt"));




            JSONObject mainObj = JSONWeather.getJSONObject("main");


            Log.e("humidity",mainObj.getString("humidity"));
            weather.currentCondition.setHumidity(mainObj.getInt("humidity"));
            weather.currentCondition.setPressure(mainObj.getInt("pressure"));
            weather.temperature.setMaxTemp((float) mainObj.getDouble("temp_max"));
            weather.temperature.setMinTemp((float) mainObj.getDouble("temp_min"));
            weather.temperature.setTemp((float) mainObj.getDouble("temp"));

            // Wind
            JSONObject wObj = JSONWeather.getJSONObject("wind");
            weather.wind.setSpeed((float) wObj.getDouble("speed"));
            weather.wind.setDeg((float) wObj.getDouble("deg"));

            // Clouds
            JSONObject cObj = JSONWeather.getJSONObject("clouds");
            weather.clouds.setPerc(cObj.getInt("all"));
            //Weather
            JSONArray Wobj = JSONWeather.getJSONArray("weather");
            Log.e("desc",Wobj.toString());
            for (int h = 0; h < Wobj.length(); h++) {
                JSONObject JSONMain = Wobj.getJSONObject(h);

                weather.currentCondition.setDescr(JSONMain.getString("description"));
                weather.currentCondition.setIcon(JSONMain.getString("icon"));
                weather.currentCondition.setWeatherId(JSONMain.getInt("id"));
                weather.currentCondition.setCondition(JSONMain.getString("main"));

            }
       // }


        return weather;
    }

}
