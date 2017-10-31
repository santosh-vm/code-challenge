package android.santosh.com.luckycodechallenge;

import android.santosh.com.luckycodechallenge.controller.MainController;

/**
 * Created by Santosh on 10/30/17.
 */

public class AppAPI {
    private MainController mainController;

    public AppAPI(MainController mainController) {
        this.mainController = mainController;
    }

    public MainController getMainController() {
        return mainController;
    }
}
