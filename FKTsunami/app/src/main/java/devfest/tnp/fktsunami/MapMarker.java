package devfest.tnp.fktsunami;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;

public class MapMarker {
    private double lat;
    private double lng;
    private String gatewayId;
    private double force;
    private Gyroscope gyroscope;

    public MapMarker( String gatewayId,double lat, double lng, double force, Gyroscope gyroscope) {
        this.lat = lat;
        this.lng = lng;
        this.gatewayId = gatewayId;
        this.force = force;
        this.gyroscope = gyroscope;
    }

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

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope(Gyroscope gyroscope) {
        this.gyroscope = gyroscope;
    }

}
