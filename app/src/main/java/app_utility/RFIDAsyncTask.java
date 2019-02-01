package app_utility;

/*
 * Created by Vijay on 05-06-2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.autochip.rfidreader.LoginActivity;
import com.autochip.rfidreader.MainActivity;

import java.util.ArrayList;
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
    private static int createUID = 2;
    private Boolean isConnected = false;
    private String sMsgResult;
    private int type;
    private Context context;
    private HashMap<String, Object> object = new HashMap<>();
    ArrayList<Integer> alID = new ArrayList<>();
    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alDeliveryOrderNumber = new ArrayList<>();
    private CircularProgressBar circularProgressBar;


    //private ArrayList<String[]> alInsuranceHistory = new ArrayList<>();
    //private ArrayList<String[]> alEmissionHistory = new ArrayList<>();

    private int ERROR_CODE = 0;

    public RFIDAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        setProgressBar();
    }

    @Override
    protected String doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        switch (type) {
            case 1:
                readCompanyTask();
                break;
            case 2:
                readDeliveryNumber();
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
                alName.add(0, "Select Company");
                alID.add(0, 0);
                LoginActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", alID, alName);
                /*if (isConnected) {

                } else {
                }*/
                break;
            case 2:
                MainActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", null, alDeliveryOrderNumber);
                break;
        }
        if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }
    }

    /*
    finds all the data created by this user and returns required fields, which searches for create_uid = 107 condition
     */
    private void readCompanyTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("res.company", new Object[]{
                new Object[]{new Object[]{"id", "!=", 0}}}, "name");

        for (int i = 0; i < data.size(); ++i) {

            alID.add(Integer.valueOf(data.get(i).get("id").toString()));
            alName.add(String.valueOf(data.get(i).get("name").toString()));

        }
        //LoginActivity.onServiceInterface.onServiceCall("SUCCESS", "", "", alID, alName);
    }

    private void readDeliveryNumber() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);

        Object[] conditions = new Object[2];
        conditions[0] = new Object[]{"state", "=", "confirmed"};
        conditions[1] = new Object[]{"delivery_name", "=", "Delivery Orders"};
        //conditions[1] = new Object[]{"origin", "=", "Quot/TC/1819/00036"};
        //conditions[1] = new Object[]{"picking_type_id", "=", "Gurugram Warehouse: Delivery Orders"};
        List<HashMap<String, Object>> data = oc.search_read("stock.picking", new Object[]{conditions}, "name");

        try {
            for (int i = 0; i < data.size(); ++i) {

                alDeliveryOrderNumber.add(String.valueOf(data.get(i).get("name").toString()));

            }
        }
        catch (Exception e){
            e.printStackTrace();
            sMsgResult = "Unable to reach server, please try again later";
            unableToConnectServer(900);
        }
        //LoginActivity.onServiceInterface.onServiceCall("SUCCESS", "", "", alID, alName);
    }


    private void unableToConnectServer(int errorCode) {
        MainActivity.onServiceInterface.onServiceCall("SERVER_ERROR", errorCode, "", sMsgResult, null, null);
    }

    private void setProgressBar() {
        circularProgressBar = new CircularProgressBar(context);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

}
