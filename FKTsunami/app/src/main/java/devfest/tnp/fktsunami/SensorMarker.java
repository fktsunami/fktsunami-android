package devfest.tnp.fktsunami;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;

public class SensorMarker {
    private String sensorId;
    private double lat;
    private double lng;
    private String gatewayId;
    private double force;
    private Gyroscope gyroscope;



//    public SensorMarker() {
//        lat = 0;
//        lng = 0;
//        gatewayId = "Unknown";
//        gyroscope = new Gyroscope(0, 0, 0);
//    }

    public SensorMarker(String sensorId, String gatewayId, double lat, double lng, double force, Gyroscope gyroscope) {
        this.sensorId = sensorId;
        this.lat = lat;
        this.lng = lng;
        this.gatewayId = gatewayId;
        this.force = force;
        this.gyroscope = gyroscope;
    }

//    public void createSensorMaker()
    public String getSensorId() { return sensorId; }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public void setGyroscope(JSONObject object){
        
    }
}
