package devfest.tnp.fktsunami;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    // config
    private static final String HOST = "a3n0rmdkx656cj-ats.iot.us-west-2.amazonaws.com";
    private static final String ID = "us-west-2:6edcd2e6-3900-4caa-adf4-2b082359d4fa";
    private static final String REGION = "us-west-2";
    //
    public GoogleMap mMap;
    // Current location
    private LocationManager locationManager;
    private LocationListener locationListener;

    GoogleApiClient mGoogleApiClient;

    private ArrayList<SensorMarker> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    // check exist Marker
    public int indexOfSensorData(String id) {
        if (sensorDataList.isEmpty()) return -1;
        for (int i = 0; i < sensorDataList.size(); i++) {
            if (sensorDataList.get(i).getSensorId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void addSensorMarker(SensorMarker sensorMarker) {
        sensorDataList.add(sensorMarker);
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sensorMarker.addToMap(mMap);
                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    public void updateSensorMarker(SensorMarker sensorMarker, int index) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sensorDataList.get(index).updateSensorMarker(sensorMarker);
                    Double force = sensorMarker.getForce();
                    if (force > 10) {
                        Double sensorLatLocation = sensorMarker.getLat();
                        Double sensorLngLocation = sensorMarker.getLng();
                        Location sensorLocation = new Location("sensorLocation");
                        sensorLocation.setLatitude(sensorLatLocation);
                        sensorLocation.setLongitude(sensorLngLocation);

                        Location myLocation = new Location("myLocation");
                        myLocation.setLatitude(16.062180);
                        myLocation.setLongitude(108.247865);
                        Float distanceBetweenMyLocationToSensor = myLocation.distanceTo(sensorLocation);

                        Log.d("Distance Between", distanceBetweenMyLocationToSensor.toString());
                        if(distanceBetweenMyLocationToSensor<500)
                            Toast.makeText(MapsActivity.this,"Cảnh Báo Sóng Thần... Chạy Ngay Đi",Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }

    }
//    public void alert(String title, String message){
//        // 1. Instantiate an AlertDialog.Builder with its constructor
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//// 2. Chain together various setter methods to set the dialog characteristics
//        builder.setMessage(message)
//                .setTitle(title);
//
//// 3. Get the AlertDialog from create()
//        AlertDialog dialog = builder.create();
//    }
    public SensorMarker convertToSensorMarker(JSONObject object) {
        String sensorId = object.optString("sensorId");
        String gatewayId = object.optString("gatewayId");
        double lat = object.optDouble("lat");
        double lng = object.optDouble("lng");
        double force = object.optDouble("force");
        JSONObject gyroscope = new JSONObject();
        double x = gyroscope.optDouble("gyroscope_x");
        double y = gyroscope.optDouble("gyroscope_y");
        double z = gyroscope.optDouble("gyroscope_z");


        SensorMarker mapMarker = new SensorMarker(sensorId, gatewayId, lat, lng, force, new Gyroscope(x, y, z));
        return mapMarker;
//        return null;
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

        MqttLogController mqttLogController = new MqttLogController();
        mqttLogController.listener = new MqttLogController.OnMessageListener() {
            @Override
            public void onMessage(String topic, String message) {
//                Log.d("onMessage", message);
//                try {
//                    JSONObject jsonMessage = new JSONObject(message);
//                    Log.d("jsonMessage",convertToSensorMarker(jsonMessage).toString());
//                } catch (JSONException e) {
//                    Log.e("jsonMessage", e.toString());
//                }
            }

            @Override
            public void onHandleSensorMessage(String topic, String message) {
                try {
                    JSONObject jsonMessage = new JSONObject(message);
                    String stringSensorId = jsonMessage.optString("sensorID");
                    Log.d("SensorId", stringSensorId);
                    Log.d("Index Of ", indexOfSensorData(stringSensorId) + "");
                    Log.d("DATA LIST  ", sensorDataList.toString());
                    int index = indexOfSensorData(stringSensorId);
                    if(index == -1)
                        addSensorMarker(new SensorMarker(jsonMessage));
                    else updateSensorMarker(new SensorMarker(jsonMessage),index);

                } catch (JSONException e) {
                    Log.e("jsonMessage", e.toString());
                }
            }
        };
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", location.getLatitude() + " + " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
