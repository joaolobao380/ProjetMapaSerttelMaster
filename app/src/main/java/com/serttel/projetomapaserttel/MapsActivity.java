package com.serttel.projetomapaserttel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MyTask task = new MyTask();
        String urlAPI = "http://desafio.serttel.com.br/dadosRecifeSemaforo.json";
        task.execute(urlAPI);



    }


    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputSteamReader = null;
            StringBuffer buffer = null;

            try {
                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                //recupera os dados em bytes
                inputStream = conexao.getInputStream();

                inputSteamReader = new InputStreamReader(inputStream);

                BufferedReader reader = new BufferedReader(inputSteamReader);
                buffer = new StringBuffer();

                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();


        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            String records = null;


            try {
                JSONObject jsonObject = new JSONObject(resultado);

                records = jsonObject.getString("records");

                JSONArray jsonArray = new JSONArray(records);

                // Populando objeto e colocando os makers no mapa.
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject j = jsonArray.optJSONObject(i);

                    MarkerOptions m = new MarkerOptions()
                            .title(j.optString("utilizacao"))
                            .position(new LatLng(j.optDouble("Latitude"), j.optDouble("Longitude")))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.semaforo1));
                    mMap.addMarker(m);
                    LatLng zoom = new LatLng(-8.060502, -34.888369);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom, 25));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    }

