package app_utility;

/*
 * Created by Vijay on 05-06-2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.util.HashMap;
import java.util.List;

import static app_utility.StaticReferenceClass.DB_NAME;
import static app_utility.StaticReferenceClass.NETWORK_ERROR_CODE;
import static app_utility.StaticReferenceClass.PASSWORD;
import static app_utility.StaticReferenceClass.PORT_NO;
import static app_utility.StaticReferenceClass.SERVER_URL;
import static app_utility.StaticReferenceClass.USER_ID;

public class RFIDAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private String res = "";
    private int createdId = -1;
    private static int createUID = 0;
    private Boolean isConnected = false;
    private String sMsgResult;
    private int type;
    private Context context;
    private HashMap<String, Object> object = new HashMap<>();


    //private ArrayList<String[]> alInsuranceHistory = new ArrayList<>();
    //private ArrayList<String[]> alEmissionHistory = new ArrayList<>();

    private int ERROR_CODE = 0;

    public RFIDAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        switch (type) {
            case 1:
                readTask();
                break;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (ERROR_CODE != 0) {
            switch (ERROR_CODE) {
                case NETWORK_ERROR_CODE:
                    unableToConnectServer(ERROR_CODE);
                    break;
            }
            ERROR_CODE = 0;
            return;
        }
        switch (type) {
            case 1:
                /*if (isConnected) {

                } else {
                }*/
                break;
        }
    }

    /*
    finds all the data created by this user and returns required fields, which searches for create_uid = 107 condition
     */
    private void readTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("res.company", null, "id", "name");

        for (int i = 0; i < data.size(); ++i) {

        }

    }


    private void unableToConnectServer(int errorCode) {
        //MainActivity.asyncInterface.onAsyncTaskCompleteGeneral("SERVER_ERROR", 2001, errorCode, "", null);
    }

}
