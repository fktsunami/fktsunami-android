package devfest.tnp.fktsunami;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,Runnable {
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
    private ArrayList<Marker> markers = new ArrayList<>();

    private AwsMqttClient mAwsMqttClient;

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
        if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
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

    public void addSensorMarker(JSONObject sensorData) {
        Log.d("Debug", sensorData.toString());

        String sensorId = sensorData.optString("sensorId");
        String gatewayId = sensorData.optString("gatewayId");
        Double lat = sensorData.optDouble("lat");
        Double lng = sensorData.optDouble("lng");
        Double force = sensorData.optDouble("force");
        JSONObject gyroscopeJSON = sensorData.optJSONObject("gyroScope");

//        SensorMarker newSensor = new SensorMarker(sensorId, gatewayId, lat, lng, force, gyroscopeJSON);

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng latLng = new LatLng(10, 108);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    public void updateSensorMarker(SensorMarker mapMarker) {
        for (int i = 0; i < sensorDataList.size(); i++) {
            if (mapMarker.getGatewayId().equals(sensorDataList.get(i).getGatewayId())) {
                sensorDataList.set(i, mapMarker);
                break;
            }
        }

    }

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


        SensorMarker mapMarker = new SensorMarker(sensorId,gatewayId, lat, lng, force, new Gyroscope(x, y, z));
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
//       for(int i=0;i<10;i++){
//           LatLng sydney = new LatLng(-100 + i+10, 100+i);
//           googleMap.addMarker(new MarkerOptions().position(sydney)
//                   .title("Marker in Sydney"));
//           googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//       }
        MqttLogController mqttLogController = new MqttLogController();
        mqttLogController.listener = new MqttLogController.OnMessageListener() {
            @Override
            public void onMessage(String topic, String message) {
                Log.d("onMessage", message);
                try {
                    JSONObject jsonMessage = new JSONObject(message);
                    Log.d("jsonMessage",convertToSensorMarker(jsonMessage).toString());
                } catch (JSONException e) {
                    Log.e("jsonMessage", e.toString());
                }
            }

            @Override
            public void onHandleSensorMessage(String topic, String message) {
                Log.d("onHandleSensorMessage", message);
//                LatLng sydney = new LatLng(24, 108);
//                mMap.addMarker(new MarkerOptions().position(sydney)
//                        .title("Marker in Sydney"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                try {
                    JSONObject jsonMessage = new JSONObject(message);
                    String stringSensorId = jsonMessage.optString("sensorID");
                    Log.d("SensorId", stringSensorId);

                    addSensorMarker(jsonMessage);

//                    SensorMarker sensorMarker = convertToSensorMarker(jsonMessage);
//                    int index = indexOfSensorData(sensorMarker.getSensorId());
//                    if (index!=-1) {
//                        // Update Sensor Attribute
//                        sensorDataList.set(index,sensorMarker);
//                        // Get sensorMarker in Sensor ArrayList                    } else {
//                        // Create New Sensor
////                        addSensorMarker(jsonMessage);
//                    }else{
//                        addSensorMarker(jsonMessage);
//                    }

                } catch (JSONException e) {
                    Log.e("jsonMessage", e.toString());
                }
            }
        };
    }


    @Override
    public void onLocationChanged(Location location) {
            Log.d("onLocationChanged",location.getLatitude()+" + "+location.getLongitude());
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

    @Override
    public void run() {
        LatLng latLng = new LatLng(10, 108);
        mMap.addMarker(new MarkerOptions().position(latLng));
    }
}
