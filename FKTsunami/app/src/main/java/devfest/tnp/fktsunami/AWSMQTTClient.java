package devfest.tnp.fktsunami;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Admin on 16/05/2018.
 */

class AwsMqttClient {

    public enum MqttClientStatus {
        Disconnected,
        Connecting,
        Connected,
        ConnectionLost,
        Reconnecting;

        private static MqttClientStatus fromAwsName(String name) {
            MqttClientStatus[] statuses = MqttClientStatus.values();
            for (MqttClientStatus status : statuses) {
                if (status.name().equals(name)) {
                    return status;
                }
            }
            return Disconnected;
        }
    }

    private static final String TAG = AwsMqttClient.class.getSimpleName();

    private CognitoCachingCredentialsProvider mCredentialsProvider;

    private AWSIotMqttManager mMqttManager;

    private String mEndPoint;

    private String mPoolId;

    private String mRegion;

    private MqttClientStatus mStatus = MqttClientStatus.Disconnected;

    private List<String> mSubscribedTopics = new ArrayList<>();

    private MqttListener mMqttListener;

    private AWSIotMqttNewMessageCallback mAwsMqttCallback = new AWSIotMqttNewMessageCallback() {
        @Override
        public void onMessageArrived(String s, byte[] bytes) {
            if (mMqttListener != null) {
                try {
                    String message = new String(bytes, "UTF-8");
                    mMqttListener.onMessageReceived(s, message);
                } catch (Exception e) {
                    Log.d(TAG, "Fail to decode message" + e.toString());
                }
            }
        }
    };

    AwsMqttClient(String endPoint, String poolId, String region) {
        mEndPoint = endPoint;
        mPoolId = poolId;
        mRegion = region;
    }

    public void setMqttListener(MqttListener mqttListener) {
        mMqttListener = mqttListener;
    }

    void connect(Context context) {
        String clientId = UUID.randomUUID().toString();

        Regions region;
        try {
            region = Regions.fromName(mRegion);
        } catch (Exception e) {
            e.printStackTrace();
            region = Regions.DEFAULT_REGION;
        }

        // Initialize the AWS Cognito credentials provider
        mCredentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(), // context
                mPoolId, // Identity Pool ID
                region // Region
        );

        // MQTT Client
        mMqttManager = new AWSIotMqttManager(clientId, mEndPoint);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mqttConnect();
            }
        }).start();
    }

    boolean disconnect() {
        return mMqttManager.disconnect();
    }

    void subscribeTopic(String topic) {
        if (!mSubscribedTopics.contains(topic)) {
            mSubscribedTopics.add(topic);
            if (mStatus == MqttClientStatus.Connected) {
                doSubscribeTopic(topic);
            }
        }
    }

    void unSubscribeTopic(String topic) {
        try {
            mSubscribedTopics.remove(topic);
            mMqttManager.unsubscribeTopic(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void publishMessage(String toTopic, String message) {
        if (toTopic != null && message != null && mStatus == MqttClientStatus.Connected) {
            try {
                mMqttManager.publishString(message, toTopic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mqttConnect() {
        try {
            mMqttManager.connect(mCredentialsProvider, (awsIotMqttClientStatus, throwable) -> {
                mStatus = MqttClientStatus.fromAwsName(awsIotMqttClientStatus.name());
                if (mMqttListener != null) {
                    mMqttListener.onMqttStatusChanged(mStatus);
                }
                if (mStatus == MqttClientStatus.Connected && !mSubscribedTopics.isEmpty()) {
                    for (String topic : mSubscribedTopics) {
                        doSubscribeTopic(topic);
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Fail to connect to MQTT");
            e.printStackTrace();
            mStatus = MqttClientStatus.Disconnected;
        }
    }

    private void doSubscribeTopic(String topic) {
        try {
            Log.d(TAG, "MQTT subscribe to topic: " + topic);
            mMqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0, mAwsMqttCallback);
        } catch (Exception e) {
            Log.d(TAG, "Fail to subscribe topic: " + topic);
            e.printStackTrace();
        }
    }
}
