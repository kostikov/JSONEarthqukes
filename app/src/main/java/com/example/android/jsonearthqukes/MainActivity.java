package com.example.android.jsonearthqukes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime&limit=10";
    JSONObject jsonResponse;
    TextView viewMagnitude;
    TextView viewPlace;
    TextView viewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewMagnitude = (TextView) findViewById(R.id.viewMagnitude);
        viewPlace = (TextView) findViewById(R.id.viewPlace);
        viewTime = (TextView) findViewById(R.id.viewTime);


        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void run() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                        JSONObject jsonRoot = new JSONObject(myResponse);
                        JSONArray features = jsonRoot.getJSONArray("features");
                        JSONObject properties = features.getJSONObject(0);
                        JSONObject earthquake = properties.getJSONObject("properties");

                        Date quakeTime = new Date(earthquake.getLong("time"));

                        viewMagnitude.setText(earthquake.getString("mag"));
                        viewPlace.setText(earthquake.getString("place"));
                        viewTime.setText(toReadableDate(quakeTime));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    private String toReadableDate (Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.US);
        return simpleDateFormat.format(date);

    }


}
