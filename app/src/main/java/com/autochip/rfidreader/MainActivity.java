package com.autochip.rfidreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import app_utility.OnServiceInterface;

public class MainActivity extends AppCompatActivity implements OnServiceInterface {

    public static OnServiceInterface onServiceInterface;
    TextView tvRFID;
    TextView tvStatus;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRFID = findViewById(R.id.tv_rfid);
        tvStatus = findViewById(R.id.tv_status);
        aSwitch = findViewById(R.id.swtich_stock);

        onServiceInterface = this;
        Intent in = new Intent(MainActivity.this, RFIDReadService.class);
        startService(in);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    RFIDReadService.stockFlag = 1;
                else
                    RFIDReadService.stockFlag = 0;
            }
        });
    }

    @Override
    public void onServiceCall(String sCase, String sMSG, String sSubmitStatus) {
        switch (sCase) {
            case "RFID":
                tvRFID.setText(sMSG);
                tvStatus.setText(sSubmitStatus);
                break;
        }
    }
}
