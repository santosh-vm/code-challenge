package android.santosh.com.luckycodechallenge;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.os.Handler;
import android.santosh.com.luckycodechallenge.controller.MainController;

/**
 * Created by Santosh on 10/30/17.
 */

public class AppApplication extends Application {
    private AppAPI appAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        MainController mainController = new MainController(new Handler());
        appAPI = new AppAPI(mainController);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE
                || level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL){
            appAPI.getMainController().resetData();
        }
    }

    public AppAPI getAppAPI() {
        return appAPI;
    }
}
