package com.autochip.rfidreader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

import app_utility.CircularProgressBar;
import app_utility.NetworkState;
import app_utility.OnServiceInterface;
import app_utility.RFIDAsyncTask;
import app_utility.SharedPreferenceClass;

public class LoginActivity extends AppCompatActivity implements OnServiceInterface {

    private CircularProgressBar circularProgressBar;

    SharedPreferenceClass sharedPreferenceClass;

    Spinner spinner;
    ArrayAdapter<String> adapterSpinner;
    ArrayList<Integer> alID = new ArrayList<>();
    ArrayList<String> alCompanyName;
    Button btnDone;
    NetworkState networkState;

    public static OnServiceInterface onServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
                SSLContext sslContext;
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                sslContext.createSSLEngine();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        networkState = new NetworkState();
        onServiceInterface = this;
        sharedPreferenceClass = new SharedPreferenceClass(LoginActivity.this);


        alCompanyName = new ArrayList<>();


        spinner = findViewById(R.id.spinner_company);
        btnDone = findViewById(R.id.btn_done);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(LoginActivity.this, "please select a company", Toast.LENGTH_SHORT).show();
                } else {
                    String sSelectedCompanyName = spinner.getSelectedItem().toString();
                    int id = alID.get(alCompanyName.indexOf(sSelectedCompanyName));
                    sharedPreferenceClass.setCompanyPreference(sSelectedCompanyName + "," + String.valueOf(id));
                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(in);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(LoginActivity.this, "please select a company", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //adapterSpinner.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        /*alCompanyName.add("asdasd");
        alCompanyName.add("asdasd");
        alCompanyName.add("asdasd");
        alCompanyName.add("asdasd");*/
        //spinner.setAdapter(adapterSpinner);
        if (networkState.isOnline() && networkState.isNetworkAvailable(getApplicationContext()))
            if (!sharedPreferenceClass.getCompanyName().equals("")) {
                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            } else {
                setProgressBar();
                RFIDAsyncTask RFIDAsyncTask = new RFIDAsyncTask(LoginActivity.this);
                RFIDAsyncTask.execute(String.valueOf(1));
            }
        else
            Toast.makeText(LoginActivity.this, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
        /*Intent in = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(in);*/
    }

    private void setProgressBar() {
        circularProgressBar = new CircularProgressBar(LoginActivity.this);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

    @Override
    public void onServiceCall(String sCase, int code, String sMSG, String sSubmitStatus, ArrayList<Integer> alID, ArrayList<String> alName) {
        switch (sCase) {
            case "SUCCESS":
                this.alID = alID;
                this.alCompanyName = alName;
                //adapterSpinner.notifyDataSetChanged();
                adapterSpinner = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, alCompanyName);
                spinner.setAdapter(adapterSpinner);
                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
                //android.R.layout.simple_spinner_item
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

                break;
        }
    }
}
