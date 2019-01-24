package com.autochip.rfidreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app_utility.CircularProgressBar;
import app_utility.OnServiceInterface;
import app_utility.RFIDAsyncTask;

public class LoginActivity extends AppCompatActivity implements OnServiceInterface {

    private CircularProgressBar circularProgressBar;

    public static OnServiceInterface onServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        onServiceInterface = this;

        setProgressBar();
        RFIDAsyncTask RFIDAsyncTask = new RFIDAsyncTask(LoginActivity.this);
        RFIDAsyncTask.execute(String.valueOf(1));
    }

    private void setProgressBar() {
        circularProgressBar = new CircularProgressBar(LoginActivity.this);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

    @Override
    public void onServiceCall(String sCase, String sMSG, String sSubmitStatus) {
        switch (sCase) {
            case "SUCCESS":
                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
                break;
        }
    }
}
