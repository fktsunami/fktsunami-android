
package devfest.tnp.fktsunami;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

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
        mHandlerThread = new HandlerThread("MyOwnMQTT");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), message -> {
            switch (message.what) {
                case MSG_SEND_TELEMETRY:
//                    publishTelemetry();
                    return true;
            }
            return false;
        });
        mHandler.sendEmptyMessageDelayed(MSG_SEND_TELEMETRY, 1000);
    }

    @Override
    public void release() {
//        mHandler.removeCallbacksAndMessages(null);
//        mHandlerThread.interrupt();
        mAwsMqttClient.disconnect();
//        mDrone.removeOnDroneAttributeChangedListener(this);
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
