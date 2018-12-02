package devfest.tnp.fktsunami;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class SensorMarker {
    private String sensorId;
    private double lat;
    private double lng;
    private String gatewayId;
    private double force;
    private Gyroscope gyroscope;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LatLng latLng;
    private Circle circle;

    public SensorMarker(JSONObject object){
        sensorId = object.optString("sensorID");
        gatewayId = object.optString("gatewayId");
        lat = object.optDouble("lat");
        lng = object.optDouble("lng");
        force = object.optDouble("force");
        JSONObject gyroscopeJSON = new JSONObject();
        double x = gyroscopeJSON.optDouble("gyroscope_x");
        double y = gyroscopeJSON.optDouble("gyroscope_y");
        double z = gyroscopeJSON.optDouble("gyroscope_z");
        gyroscope=new Gyroscope(x,y,z);
        latLng = new LatLng(lat,lng);
        this.markerOptions = new MarkerOptions().position(latLng);
        this.markerOptions.title(sensorId+" "+lat+" "+lng);
    }

    public SensorMarker(String sensorId, String gatewayId, double lat, double lng, double force, Gyroscope gyroscope) {
        this.sensorId = sensorId;
        this.lat = lat;
        this.lng = lng;
        this.gatewayId = gatewayId;
        this.force = force;
        this.gyroscope = gyroscope;
        latLng = new LatLng(lat,lng);
        this.markerOptions = new MarkerOptions().position(latLng);
        this.markerOptions.title(sensorId+" "+lat+" "+lng);

    }

    public void updateSensorMarker(SensorMarker sensorMarker){
        gatewayId = sensorMarker.getGatewayId();
        lat = sensorMarker.getLat();
        lng = sensorMarker.getLng();
        force = sensorMarker.getForce();
        gyroscope= sensorMarker.getGyroscope();
        latLng = sensorMarker.getLatLng();
        updatePosition();
    }

    private void updatePosition() {
        this.marker.setPosition(latLng);
        this.marker.setTitle(sensorId+" "+lat+" "+lng);
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope(Gyroscope gyroscope) {
        this.gyroscope = gyroscope;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void addToMap(GoogleMap map) {
        this.marker = map.addMarker(this.markerOptions);
        setCircle(map);
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
        this.markerOptions.position(latLng);



    }

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
    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
        markerOptions.position(latLng);

    }

    public void setCircle(GoogleMap map) {
        this.circle = map.addCircle(new CircleOptions().center(latLng));
        this.circle.setStrokeColor(Color.RED);
        this.circle.setRadius(500+force);
        this.circle.setFillColor(Color.BLUE);
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
