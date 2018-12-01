package devfest.tnp.fktsunami;

import android.graphics.Camera;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String HOST = "a3n0rmdkx656cj-ats.iot.us-west-2.amazonaws.com";
    private static final String ID = "us-west-2:6edcd2e6-3900-4caa-adf4-2b082359d4fa";
    private static final String REGION = "us-west-2";

    private GoogleMap mMap;
    private ArrayList<MapMarker> mapMarkerList = new ArrayList<>();

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

    // check exist Marker
    public boolean hasMarker(String id) {
        if (mapMarkerList.isEmpty()) return false;
        for (int i = 0; i < mapMarkerList.size(); i++) {
            if (mapMarkerList.get(i).getGatewayId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addMarker(MapMarker mapMarker) {
        mapMarkerList.add(mapMarker);
        LatLng latLng = new LatLng(mapMarker.getLat(), mapMarker.getLng());
        mMap.addMarker(new MarkerOptions().position(latLng)).setTitle(mapMarker.getGatewayId());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void updateMarker(MapMarker mapMarker) {
        for (int i = 0; i < mapMarkerList.size(); i++) {
            if (mapMarker.getGatewayId().equals(mapMarkerList.get(i).getGatewayId())) {
                mapMarkerList.set(i, mapMarker);
                break;
            }
        }
    }

    public MapMarker convertToMapMarker(String message) {
        JSONObject object = null;
        try {
            object = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        double lat = object.optDouble("lat");
        double lng = object.optDouble("lng");
        double force = object.optDouble("force");
        JSONObject gyroscope = new JSONObject();
        double x = gyroscope.optDouble("gyroscope_x");
        double y = gyroscope.optDouble("gyroscope_y");
        double z = gyroscope.optDouble("gyroscope_z");

        String sensorID = object.optString("sensorID");

        MapMarker mapMarker = new MapMarker(sensorID, lat, lng, force, new Gyroscope(x, y, z));
        return mapMarker;
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
//        mAwsMqttClient = new AwsMqttClient(HOST,ID,REGION);
        MqttLogController mqttLogController = new MqttLogController();
        mqttLogController.listener = new MqttLogController.OnMessageListener() {

            @Override
            public void onMessage(String topic, String message) throws JSONException {
                Log.d("Marker Object",convertToMapMarker(message).toString());
            }

        };

    }
}
