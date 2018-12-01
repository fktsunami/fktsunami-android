
package devfest.tnp.fktsunami;

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
    private final String ID = "us-west-2:6edcd2e6-3900-4caa-adf4-2b082359d4fa\n";
    private final String REGION = "us-west-2";

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private String mMissionId = "";
    private String mFlightId = "";

    private long mStartTimeSendTelemetry = -1;
    private long mStartTimeUtc = -1;

    public MqttLogController() {
        mAwsMqttClient = new AwsMqttClient(HOST, ID, REGION);
        mAwsMqttClient.connect(DroneApplication.getApplication());
        mAwsMqttClient.setMqttListener(this);
        mAwsMqttClient.subscribeTopic("+/+/app");
        mAwsMqttClient.subscribeTopic(sWeatherTopic);
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
//
//    @Override
//    public void setOnRealTimeListener(OnRealTimeListener onResultListener) {
//        mOnRealTimeListener = onResultListener;
//        String serial = mDrone.getAttribute().getDroneId();
//        if (serial != null && !serial.isEmpty()) {
//            mOnRealTimeListener.onReceiveStreamKey(serial);
//        }
//    }

//    @Override
//    public void sendTelemetryData(String token, String flightId, Location location, double direction, int battery, int remoteBattery, int gpsSatellites, int gpsSignal, int sdcard, long flightTime, String currentPoint, String startAt, int planStatus, boolean isGoingHome) {
//
//    }

//    @Override
//    public void requestRoom(String missionId, double latitude, double longitude, double height, long flightTime) {
//
//    }

//    @Override
//    public void requestRoom(String missionId) {
//
//    }

//    @Override
//    public void emitEventWithData(RealTimeEvent event, Object data) {
//        if (RealTimeEvent.APP_PREPARE_FLIGHT_SUCCEED == event ||
//                RealTimeEvent.APP_PREPARE_FLIGHT_FAIL == event) {
//            publishPrepareStatus(event == RealTimeEvent.APP_PREPARE_FLIGHT_SUCCEED);
//        }
////    }

    @Override
    public void onMqttStatusChanged(AwsMqttClient.MqttClientStatus status) {
        Log.d(TAG, "MQTT status change: " + status);
    }

    @Override
    public void onMessageReceived(String fromTopic, String message) {
//        Log.d(TAG, "Receive Message from: " + fromTopic + " content -> " + message);
//        if (Objects.equals(fromTopic, mOwnDroneTopic)) {
//            processDroneAction(message);
//        } else if (Objects.equals(fromTopic, sWeatherTopic)) {
//            processWeatherData(message);
//        } else {
//            if (fromTopic.contains("/app")) {
//                Log.d(TAG, "Process other telemetry");
//                String droneSerial = fromTopic.split("/")[1];
//                if (!Objects.equals(droneSerial, mDrone.getAttribute().getDroneId())){
//                    processDataFromAppTopic(droneSerial, message);
//                }
//            }
//        }
    }

//    private void processDroneAction(String remoteMessage) {
//        if (mOnRealTimeListener == null) return;
//        try {
//            JSONObject jMessage = new JSONObject(remoteMessage);
//            String msgType = jMessage.optString("type");
//            String msgCmd = jMessage.optString("cmd");
//            String flightId = jMessage.optString("id");
//            if ("ctrl_drone".equals(msgType)) {
//                RemoteDroneAction action = "fp_start".equals(msgCmd) ? RemoteDroneAction.START :
//                        "fp_stop".equals(msgCmd) ? RemoteDroneAction.STOP :
//                                "fp_pause".equals(msgCmd) ? RemoteDroneAction.PAUSE :
//                                        "fp_resume".equals(msgCmd) ? RemoteDroneAction.RESUME :
//                                                "rth".equals(msgCmd) ? RemoteDroneAction.GO_HOME :
//                                                        "land".equals(msgCmd) ? RemoteDroneAction.LAND :
//                                                                "gimbal_pitch_up".equals(msgCmd) ? RemoteDroneAction.GIMBAL_PITCH_UP :
//                                                                        "gimbal_pitch_down".equals(msgCmd) ? RemoteDroneAction.GIMBAL_PITCH_DOWN :
//                                                                                "gimbal_yaw_left".equals(msgCmd) ? RemoteDroneAction.GIMBAL_YAW_LEFT :
//                                                                                        "gimbal_yaw_right".equals(msgCmd) ? RemoteDroneAction.GIMBAL_YAW_RIGHT :
//                                                                                                "zoom_in".equals(msgCmd) ? RemoteDroneAction.ZOOM_IN :
//                                                                                                        "zoom_out".equals(msgCmd) ? RemoteDroneAction.ZOOM_OUT :
//                                                                                                                "shoot_photo".equals(msgCmd) ? RemoteDroneAction.SHOOT_PHOTO : null;
//
//                if (action != null) {
//                    if (action == RemoteDroneAction.START ||
//                            action == RemoteDroneAction.STOP ||
//                            action == RemoteDroneAction.PAUSE ||
//                            action == RemoteDroneAction.RESUME ||
//                            action == RemoteDroneAction.GO_HOME ||
//                            action == RemoteDroneAction.LAND) {
//                        if (Objects.equals(mFlightId, flightId)) {
//                            mOnRealTimeListener.onActionFromWeb(action, mMissionId);
//                        }
//                    } else {
//                        //For camera actions we don't need to validate flight id
//                        mOnRealTimeListener.onActionFromWeb(action, mMissionId);
//                    }
//                }
//            } else if ("utm_cmd".equals(msgType)) {
//                RemoteDroneAction action = "flight_plan".equals(msgCmd) ? RemoteDroneAction.PREPARE_MISSION : null;
//                if (action == RemoteDroneAction.PREPARE_MISSION) {
//                    mMissionId = jMessage.optString("mission_id");
//                    mFlightId = flightId;
//                    mOnRealTimeListener.onPrepareMissionSuccess(mFlightId);
//                    mOnRealTimeListener.onActionFromWeb(action, mMissionId);
//                    notifyCollisionResolverData();
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    private void processDataFromAppTopic(String droneSerial, String message) {
//        if (mOnRealTimeListener == null) return;
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            String type = jsonObject.optString("type");
//            if ("telemetry".equals(type)) {
//                mOnRealTimeListener.onReceivedOtherDroneLog(FlyingDroneLog.parseFromTelemetry(jsonObject));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void processWeatherData(String message) {
//        if (mOnRealTimeListener == null) return;
//        try {
//            Gson gson = PreferenceManager.getInstance().getSimpleGson();
//            mOnRealTimeListener.onReceivedWeatherData(gson.fromJson(message, RealtimeWeatherInfo.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onAttributeChanged(DroneAttribute.GroupOfAttribute groupOfAttribute) {
//        if (groupOfAttribute == DroneAttribute.GroupOfAttribute.IDENTIFICATION) {
//            subscribeMyOwnTopics();
//            String serial = mDrone.getAttribute().getDroneId();
//            if (serial != null && !serial.isEmpty()) {
//                mOnRealTimeListener.onReceiveStreamKey(serial);
//            }
//        }
//    }

//    private void subscribeMyOwnTopics() {
//        DroneAttribute attribute = mDrone.getAttribute();
//        if (attribute.getDroneId() != null && !attribute.getDroneId().isEmpty()) {
//            String ownDroneTopic = String.format("%s/%s/drone", attribute.getManufacturer(), attribute.getDroneId());
//            if (!ownDroneTopic.equals(mOwnDroneTopic)) {
//                if (mOwnDroneTopic != null && !mOwnDroneTopic.isEmpty()) {
//                    mAwsMqttClient.unSubscribeTopic(mOwnDroneTopic);
//                }
//                mOwnDroneTopic = ownDroneTopic;
//                mOwnAppTopic = String.format("%s/%s/app", attribute.getManufacturer(), attribute.getDroneId());
//                mAwsMqttClient.subscribeTopic(mOwnDroneTopic);
//            }
//        }
//    }

//    private void publishTelemetry() {
//        if (mDrone != null && mDrone.isDroneConnected()) {
//            if (mStartTimeSendTelemetry < 0) {
//                mStartTimeSendTelemetry = System.currentTimeMillis();
//                mStartTimeUtc = Utils.getGtmDate().getTime();
//                notifyCollisionResolverData();
//            }
//            DroneAttribute attribute = mDrone.getAttribute();
//            TelemetryBuilder builder = new TelemetryBuilder();
//            builder.addAircraft(attribute.getSdkVersion(), attribute.getDroneId(), attribute.getFirmwareVersion(), attribute.getManufacturer())
//                    .addPosition(attribute.getLatitude(), attribute.getLongitude(), attribute.getAltitude(), attribute.getExecutingWaypointIndex())
//                    .addAttitude(String.format(Locale.US, "%.1f", attribute.getDroneHeading()), String.format(Locale.US, "%.1f", attribute.getVelocity()), String.format(Locale.US, "%.1f", attribute.getFlightDirection()))
//                    .addStatus(attribute.getRemainingBattery(), 0, attribute.getDroneState().ordinal(), attribute.getGpsLevel(), attribute.isStreaming()? 1 : 0)
//                    .addNetworkStatus()
//                    .addFlightId(mFlightId)
//                    .addElapsed((System.currentTimeMillis() - mStartTimeSendTelemetry) / 1000)
//                    .addStartTimeUtc(mStartTimeUtc);
//            //TODO: add missing params
//            mAwsMqttClient.publishMessage(mOwnAppTopic, builder.createMessage());
//        }
//        mHandler.sendEmptyMessageDelayed(MSG_SEND_TELEMETRY, 1000);
//    }

//        private void publishPrepareStatus(boolean isSuccess) {
//            JSONObject data = new JSONObject();
//            Utils.putParamsToJson(data, "type", "utm_cmd");
//            Utils.putParamsToJson(data, "cmd", "flight_plan_res");
//            Utils.putParamsToJson(data, "id", mFlightId);
//            Utils.putParamsToJson(data, "result", isSuccess ? "success" : "failure");
//
//            mAwsMqttClient.publishMessage(mOwnAppTopic, data.toString());
//        }

//    private void notifyCollisionResolverData() {
//        if (mOnRealTimeListener != null && mFlightId != null && !mFlightId.isEmpty() && mStartTimeUtc > 0) {
//            mOnRealTimeListener.onReceiveResolverCollisionData(mFlightId, mStartTimeUtc);
//        }
//    }
}
