package a14a1.duyanhv.weada;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private Button btnChangeCity;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());

    }

    private void initViews() {
        cityName = findViewById(R.id.cityText);
        iconView = findViewById(R.id.thumbnailIcon);
        temp = findViewById(R.id.tempText);
        description = findViewById(R.id.cloudText);
        humidity = findViewById(R.id.humidText);
        pressure = findViewById(R.id.pressureText);
        wind = findViewById(R.id.windText);
        sunrise = findViewById(R.id.riseText);
        sunset = findViewById(R.id.setText);
        updated = findViewById(R.id.updateText);
        btnChangeCity = findViewById(R.id.change_cityId);
        btnChangeCity.setOnClickListener(this);
    }


    public void renderWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city});
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.change_cityId:
                showInputDialog();

                break;
        }
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {


            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String code){
            final DefaultHttpClient client = new DefaultHttpClient();

            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");

            try {
                HttpResponse response = client.execute(getRequest);

                final int statusCode = response.getStatusLine().getStatusCode();

                if(statusCode != HttpStatus.SC_OK){
                    Log.e("DownloadImage", "Error: " +statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if (entity != null){
                    InputStream inputStream = null;

                    inputStream = entity.getContent();

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class WeatherTask extends AsyncTask<String, Void, Weather> {


        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            weather = JSONWeatherParser.getWeather(data);
            Log.v("Weather", weather +"");
            weather.iconData = weather.currentCondition.getIcon();

//            Log.v("Data: ", weather.place.getCity());

            new DownloadImageAsyncTask().execute(weather.iconData);

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();

            String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            String sunsetDate = df.format(new Date(weather.place.getSunset()));
            String lastUpdated = df.format(new Date(weather.place.getLastupdate()));

            //rounded to 1 decimal point
            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + tempFormat + " °C");

            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + " %");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa");
            wind.setText("Wind: " + weather.currentCondition.getHumidity() + " mps");
            sunrise.setText("Sunrise: " + sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last Updated: " + lastUpdated);
            description.setText("Condition: " + weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");

        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);

        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Hanoi,VN");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }


}
