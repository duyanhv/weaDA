package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;

public class JSONWeatherParser {
    public static Weather getWeather(String data) {
        Weather weather = new Weather();

        //create JsonObject from data
        try {

            if(data != null){
                JSONObject jsonObject = new JSONObject(data);

                Place place = new Place();

                JSONObject coordObj = Utils.getObject("coord", jsonObject);
                place.setLon(Utils.getFloat("lon", coordObj));
                place.setLat(Utils.getFloat("lat", coordObj));

                JSONObject sysObj = Utils.getObject("sys", jsonObject);
                place.setCountry(Utils.getString("country", sysObj));
                place.setLastupdate(Utils.getInt("dt", jsonObject));
                place.setSunrise(Utils.getInt("sunrise", sysObj));
                place.setSunset(Utils.getInt("sunset", sysObj));
                place.setCity(Utils.getString("name", jsonObject));

                weather.place = place;


                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject jsonWeather = jsonArray.getJSONObject(0);
                weather.currentCondition.setWeatherId(Utils.getInt("id", jsonWeather));
                weather.currentCondition.setDescription(Utils.getString("description", jsonWeather));
                weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
                weather.currentCondition.setIcon(Utils.getString("icon", jsonWeather));

                JSONObject mainObj = Utils.getObject("main", jsonObject);
                weather.currentCondition.setHumidity(Utils.getInt("humidity", mainObj));
                weather.currentCondition.setPressure(Utils.getInt("pressure", mainObj));
                weather.currentCondition.setMinTemp(Utils.getFloat("temp_min", mainObj));
                weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainObj));
                weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));


                JSONObject windObj = Utils.getObject("wind", jsonObject);
                weather.wind.setSpeed(Utils.getFloat("speed", windObj));
                weather.wind.setDeg(Utils.getFloat("deg", windObj));

                JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
                weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));

                return weather;
            }
            Place place = new Place();


            place.setLon(0);
            place.setLat(0);

            place.setCountry("Country Not Found");
            place.setLastupdate(0);
            place.setSunrise(0);
            place.setSunset(0);
            place.setCity("City Not Found");

            weather.place = place;


            weather.currentCondition.setWeatherId(0);
            weather.currentCondition.setDescription("Null");
            weather.currentCondition.setCondition("Null");
            weather.currentCondition.setIcon("Null");


            weather.currentCondition.setHumidity(0);
            weather.currentCondition.setPressure(0);
            weather.currentCondition.setMinTemp(0);
            weather.currentCondition.setMaxTemp(0);
            weather.currentCondition.setTemperature(0);


            weather.wind.setSpeed(0);
            weather.wind.setDeg(0);


            weather.clouds.setPrecipitation(0);
            return weather;

        } catch (JSONException e) {
            e.printStackTrace();


            return null;
        }
    }
}
