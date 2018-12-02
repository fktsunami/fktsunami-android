//package devfest.tnp.fktsunami;
//
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.json.JSONObject;
//
//public class DeviceMarker {
//    private String deviceID;
//    private double lat;
//    private double lng;
//    private MarkerOptions marker;
//    private LatLng latLng;
//
//    public DeviceMarker(JSONObject object){
//        deviceID = object.optString("sensorID");
//        lat = object.optDouble("lat");
//        lng = object.optDouble("lng");
//        JSONObject gyroscopeJSON = new JSONObject();
//        double x = gyroscopeJSON.optDouble("x");
//        double y = gyroscopeJSON.optDouble("y");
//        double z = gyroscopeJSON.optDouble("z");
//        latLng = new LatLng(lat,lng);
//        this.marker = new MarkerOptions();
//        this.marker.position(latLng);
//        this.marker.title(this.deviceID +" ( "+lat+" ; "+lng+" ) ");
//
//    }
//
//    public DeviceMarker(String deviceID, String gatewayId, double lat, double lng, double force, Gyroscope gyroscope) {
//        this.deviceID = deviceID;
//        this.lat = lat;
//        this.lng = lng;
//        this.marker = new MarkerOptions();
//        this.marker.position(new LatLng(lat,lng));
//        this.marker.title(this.deviceID +" ( "+lat+" ; "+lng+" ) ");
//    }
//    public void updateSensorMarker(SensorMarker sensorMarker){
//        lat = sensorMarker.getLat();
//        lng = sensorMarker.getLng();
//        latLng = sensorMarker.getLatLng();
//        this.marker.position(latLng);
//        this.marker.title(this.deviceID + " ( "+lat+" ; "+lng+" ) ");
//
//    }
//    public LatLng getLatLng(){
//        return latLng;
//    }
//
//    public void setDeviceID(String deviceID) {
//        this.deviceID = deviceID;
//    }
//
//
//
//    public MarkerOptions getMarker() {
//        return marker;
//    }
//
//    public void setMarker(MarkerOptions marker) {
//        this.marker = marker;
//    }
//
//    public String getDeviceID() { return deviceID; }
//
//    public double getLat() {
//        return lat;
//    }
//
//    public void setLat(double lat) {
//        this.lat = lat;
//    }
//
//    public double getLng() {
//        return lng;
//    }
//
//    public void setLng(double lng) {
//        this.lng = lng;
//    }
//
//
//}
