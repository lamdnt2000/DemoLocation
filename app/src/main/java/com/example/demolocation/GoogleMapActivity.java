package com.example.demolocation;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.io.IOException;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity  {
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Button btStyle;
    private int state;
    private Location currentLocation;
    private static final LatLng BEN_THANH_MARKET = new LatLng(10.7731, 106.6983);
    private static final LatLng SAIGON_OPERA_HOUSE = new LatLng(10.7767,
            106.7032);
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng p;
    private int status;
    private final String accessToken = "pk.eyJ1IjoiaDNuenkiLCJhIjoiY2wwMHBjOWltMGIwdjNpcWdtbnE3amd6eSJ9.WW_dkTFiWwgINaBrRWHlZg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,accessToken);
        setContentView(R.layout.activity_google_map);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                GoogleMapActivity.this.mapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        List<String> pr = locationManager.getProviders(true);
                        currentLocation = null;
                        for (int i = 0; i < pr.size(); i++) {
                            currentLocation = locationManager.getLastKnownLocation(pr.get(i));
                            if (currentLocation != null) {
                                Log.d("bbb", currentLocation.getLatitude() + "");
                                break;
                            }
                        }
                        if (currentLocation!=null) {
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latLng)
                                    .title("My position")
                                    .snippet("Mobile Programming")
                                    .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.ic_location)));

                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,45));

                            mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);
                        }

                    }
                });
            }
        });
        btStyle = (Button) findViewById(R.id.btStyle);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    public void clickToChangeStyle(View view) {
        String title = "";
        switch (state) {
            case 0:
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT);
                title = "Normal - Change to Hybrid";
                state = 1;
                break;
            case 1:
                mapboxMap.setStyle(Style.SATELLITE);
                title = "Hybrid - Change to Satellite";
                state = 2;
                break;
            case 2:
                mapboxMap.setStyle(Style.SATELLITE_STREETS);
                title = "Satellite - Change to None";
                state = 3;
                break;
            case 3:
                mapboxMap.setStyle(Style.LIGHT);
                title = "Terrain - Change to None";
                state = 4;
                break;
            case 4:
                mapboxMap.setStyle(Style.TRAFFIC_DAY);
                title = "None - Change to Normal";
                state = 0;
                break;
        }
        btStyle.setText(title);
    }




    private class MyOnMapClicklistener implements MapboxMap.OnMapClickListener {
        @Override
        public boolean onMapClick(@NonNull LatLng point) {
            Toast.makeText(getBaseContext(), "Postion: " +
                            point.getLatitude() + ":" + point.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d ("ddd", "dddd");
                Toast.makeText(
                        getBaseContext(),
                        "Position: " + location.getLatitude() + ":"
                                + location.getLongitude(), Toast.LENGTH_SHORT)
                        .show();
                p = new LatLng(location.getLatitude(), location.getLongitude());
                mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p,18));
                    }
                });
            }
        }
        @Override
        public void onProviderDisabled (String provider) {
            Toast.makeText(getBaseContext(), "Disabled provider " + provider,
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Enabled provider " + provider,
                    Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onStatusChanged (String provider, int status, Bundle extras) {
            String strStatus = "";
            switch (status) {
                case LocationProvider.AVAILABLE:
                    strStatus = "Available";
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    strStatus = "Out of Service";
                    break;
                case LocationProvider. TEMPORARILY_UNAVAILABLE:
                    strStatus = "temporarily unavailable";
                    break;
            }
            Toast.makeText(getBaseContext(), provider + " " + strStatus,
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_showtraffic:
                mapboxMap.setStyle(Style.TRAFFIC_DAY);
                break;
            case R.id.menu_zoomin:

                mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.menu_zoomout:
                mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.menu_gotolocation:
                CameraPosition cameraPos = new CameraPosition.Builder()
                        .target(BEN_THANH_MARKET).zoom(17).bearing(90).tilt(30)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                        .position(BEN_THANH_MARKET)
                        .title("Ben Thanh Market")
                        .snippet("HCM City")
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.ic_location)));
                break;
            case R.id.menu_showcurrentlocation:
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                List<String> pr = locationManager.getProviders(true);
                currentLocation = null;
                for (int i = 0; i < pr.size(); i++) {
                    currentLocation = locationManager.getLastKnownLocation(pr.get(i));
                    if (currentLocation != null) {
                        Log.d("bbb", currentLocation.getLatitude() + "");
                        break;
                    }
                }
                if (currentLocation!=null) {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latLng)
                            .title("My position")
                            .snippet("Mobile Programming")
                            .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.ic_location)));

                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,45));

                    mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);
                }
                break;
            case R.id.menu_lineconnecttwopoints:
                mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(SAIGON_OPERA_HOUSE)
                        .title("Saigon Opera House")
                        .snippet("HCM City")
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.ic_minion)));
                mapboxMap.addPolyline(new PolylineOptions()
                        .add(BEN_THANH_MARKET, SAIGON_OPERA_HOUSE).width(5)
                        .color(Color.RED));
                break;
            case R.id.menu_getLocationData:
                locationListener = new MyLocationListener();
                status = 1;
            case R.id.menu_getLocationOnTouch:
                mapboxMap.addOnMapClickListener(new MyOnMapClicklistener());
                break;
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    @Override
    protected void onResume () {
        super.onResume();
        if (status != 0) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (status != 0) {
            locationManager.removeUpdates(locationListener);
        }
    }
    public void clickToFind(View view) {
        EditText txtFind = (EditText) findViewById(R.id.edLocation);
        String strLocation = txtFind.getText().toString();
        if (strLocation != null && !strLocation.trim().equals("")) {
            new GeocoderTask().execute(strLocation);
        }
    }
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {


        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geo = new Geocoder(getBaseContext());
            List<Address> address = null;
            try {
                address = geo.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(List<Address> result) {
            if (result == null || result.size() == 0) {
                Toast.makeText(getBaseContext(), "Not Found",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mapboxMap.clear();
            for (int i = 0; i < result.size(); i++) {
                Address address = (Address) result.get(i);
                LatLng findPos = new LatLng(address.getLatitude(), address.getLongitude());
                String addressText = String.format("%s %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());
                com.mapbox.mapboxsdk.annotations.MarkerOptions mo = new MarkerOptions();
                mo.position(findPos);
                mo.title(addressText);
                mapboxMap.addMarker(mo);
                if (i == 0) {
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(findPos));
                }
            }
        }


    }


}