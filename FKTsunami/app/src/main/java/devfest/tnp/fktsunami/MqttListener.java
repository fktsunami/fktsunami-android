package devfest.tnp.fktsunami;

import org.json.JSONException;

/**
 * Created by Admin on 17/05/2018.
 */

interface MqttListener {
    void onMqttStatusChanged(AwsMqttClient.MqttClientStatus status);
    void onMessageReceived(String fromTopic, String message) throws JSONException;
}
