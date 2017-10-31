package android.santosh.com.luckycodechallenge.activity;

import android.os.Bundle;
import android.santosh.com.luckycodechallenge.AppAPI;
import android.santosh.com.luckycodechallenge.AppApplication;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Santosh on 10/30/17.
 */

public class BaseActivity extends AppCompatActivity {
    protected AppAPI appAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appAPI = ((AppApplication) getApplication()).getAppAPI();
    }
}
