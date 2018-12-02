
package devfest.tnp.fktsunami;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Admin on 17/05/2018.
 */

public class MqttLogController implements LogController, MqttListener {

    private static final int MSG_SEND_TELEMETRY = 0;
    private static final String TAG = MqttLogController.class.getSimpleName();
    private AwsMqttClient mAwsMqttClient;

    private String mOwnDroneTopic = null;
    private String mOwnAppTopic = null;
    private static final String sWeatherTopic = "weather";
    private final String HOST = "a3n0rmdkx656cj-ats.iot.us-west-2.amazonaws.com";
    private final String ID = "us-west-2:6edcd2e6-3900-4caa-adf4-2b082359d4fa";
    private final String REGION = "us-west-2";

    private Handler mHandler;
    private HandlerThread mHandlerThread;
//
//    private String mMissionId = "";
//    private String mFlightId = "";
//
//    private long mStartTimeSendTelemetry = -1;
//    private long mStartTimeUtc = -1;

    public MqttLogController() {
        mAwsMqttClient = new AwsMqttClient(HOST, ID, REGION);
        mAwsMqttClient.connect(DroneApplication.getApplication());
        mAwsMqttClient.setMqttListener(this);
        mAwsMqttClient.subscribeTopic("+/tsunamiSensor");
//        mAwsMqttClient.subscribeTopic(sWeatherTopic);
//        mDrone = DroneApplication.getDrone();
//        mDrone.addOnDroneAttributeChangedListener(this);
//        subscribeMyOwnTopics();

        UUID uuid= UUID.randomUUID();
        mHandlerThread = new HandlerThread("MyOwnMQTT");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), message -> {
            switch (message.what) {
                case MSG_SEND_TELEMETRY:
                    Log.d("DEBUG", "DEBUGADASDSAD");
                    publishLocation(uuid);
                    return true;
            }
            return false;
        });
        mHandler.sendEmptyMessageDelayed(MSG_SEND_TELEMETRY, 10000);
    }

    @Override
    public void release() {
//        mHandler.removeCallbacksAndMessages(null);
//        mHandlerThread.interrupt();
        mAwsMqttClient.disconnect();
//        mDrone.removeOnDroneAttributeChangedListener(this);
    }
    private void publishLocation(UUID uuid) {
        Log.d("TEST", "TEST");
        JSONObject locationUser = new JSONObject();

        putParamsToJson(locationUser,"userId", uuid);
        putParamsToJson(locationUser,"lat", 16.062380);
        putParamsToJson(locationUser,"lng", 108.249865);
        Log.d("ass ",locationUser.toString());

        String topicName = uuid.toString() + "/user";

        Log.d("topicName", topicName);

        mAwsMqttClient.publishMessage(topicName, locationUser.toString());
        mHandler.sendEmptyMessageDelayed(MSG_SEND_TELEMETRY, 10000);
    }
    public static void putParamsToJson(@NonNull   JSONObject object, String key, Object value) {
        if (value != null) {
            try {
                object.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onMqttStatusChanged(AwsMqttClient.MqttClientStatus status) {
        Log.d(TAG, "MQTT status change: " + status);
    }

    @Override
    public void onMessageReceived(String fromTopic, String message) throws JSONException {
        Log.d("onMessageReceived",message);
//        listener.onMessage(fromTopic, message);
        if (fromTopic.contains("/tsunamiSensor")) {
            Log.d(TAG, "Process TsunamiSensor");
            listener.onHandleSensorMessage(fromTopic, message);
        }
    }

    OnMessageListener listener;

    public interface OnMessageListener{
        void onMessage(String topic, String message) throws JSONException;

        void onHandleSensorMessage(String topic, String message) throws JSONException;
    }



}
