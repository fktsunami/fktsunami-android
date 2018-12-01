package devfest.tnp.fktsunami;

import android.app.Application;

public class DroneApplication extends Application {
    private static DroneApplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static DroneApplication getApplication(){
        return sInstance;
    }
}
