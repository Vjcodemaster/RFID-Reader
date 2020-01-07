package app_utility;

/*
 * Created by Vijay on 05-06-2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.autochip.rfidreader.LoginActivity;
import com.autochip.rfidreader.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.OdooUser;
import oogbox.api.odoo.client.AuthError;
import oogbox.api.odoo.client.OdooVersion;
import oogbox.api.odoo.client.helper.data.OdooRecord;
import oogbox.api.odoo.client.helper.data.OdooResult;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;
import oogbox.api.odoo.client.listeners.AuthenticateListener;
import oogbox.api.odoo.client.listeners.IOdooResponse;
import oogbox.api.odoo.client.listeners.OdooConnectListener;

import static app_utility.StaticReferenceClass.DB_NAME;
import static app_utility.StaticReferenceClass.PASSWORD;
import static app_utility.StaticReferenceClass.PORT_NO;
import static app_utility.StaticReferenceClass.SERVER_URL;
import static app_utility.StaticReferenceClass.USER_ID;
import static app_utility.StaticReferenceClass.sURL;

public class RFIDAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private String res = "";
    //private int createdId = -1;
    //private static int createUID = 2;
    //private Boolean isConnected = false;
    private String sMsgResult;
    private int type;
    private Context context;
    //private HashMap<String, Object> object = new HashMap<>();
    private ArrayList<Integer> alID = new ArrayList<>();
    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<String> alDeliveryOrderNumber = new ArrayList<>();
    private CircularProgressBar circularProgressBar;

    private OdooClient client;
    private AuthenticateListener loginCallback;
    //private ArrayList<String[]> alInsuranceHistory = new ArrayList<>();
    //private ArrayList<String[]> alEmissionHistory = new ArrayList<>();

    //private int ERROR_CODE = 0;

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
                //readCompanyViaLibrary();
                break;
            case 2:
                readDeliveryNumber();
                //readDeliveryNumberViaLibrary();
                break;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        switch (type){
            case 1:
                LoginActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", alID, alName);
                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
                break;
            case 2:
                MainActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", null, alDeliveryOrderNumber);

                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
                break;
        }
        /*if (ERROR_CODE != 0) {
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
                *//*if (isConnected) {

                } else {
                }*//*
                break;
            case 2:
                MainActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", null, alDeliveryOrderNumber);
                break;
        }
        if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }*/
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
            alName.add(data.get(i).get("name").toString());

        }
        alName.add(0, "Select Company");
        alID.add(0, 0);
        //LoginActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", alID, alName);

        /*if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }*/
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

                alDeliveryOrderNumber.add(String.valueOf(data.get(i).get("name")));

            }
        }
        catch (Exception e){
            e.printStackTrace();
            sMsgResult = "Unable to reach server, please try again later";
            unableToConnectServer(900);
        }
        //LoginActivity.onServiceInterface.onServiceCall("SUCCESS", "", "", alID, alName);
    }

    private void readCompanyViaLibrary() {

        Uri.Builder builder = Uri.parse(sURL).buildUpon();
        //builder.appendQueryParameter("EmployeeId", "12345555");
        //builder.appendQueryParameter("Environment", "DEV");
        client = new OdooClient.Builder(context)
                .setHost(builder.toString())
                .setConnectListener(new OdooConnectListener() {
                    @Override
                    public void onConnected(OdooVersion version) {
                        client.authenticate("admin", "2019@autochip", DB_NAME, loginCallback);
                    }
                }).build();
        loginCallback = new AuthenticateListener() {
            @Override
            public void onLoginSuccess(OdooUser user) {
                ODomain domain = new ODomain();
                domain.add("id", "!=", 0);
                OdooFields fields = new OdooFields();
                fields.addAll("id", "name");

                int offset = 0;
                int limit = 80;

                String sorting = "";
                client.searchRead("res.company", domain, fields, offset, limit, sorting, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        OdooRecord[] records = result.getRecords();
                        for (OdooRecord record : records) {

                            float value = record.getFloat("id");
                            int n = Math.round(value);
                            alID.add(n);
                            alName.add(record.getString("name"));

                        }
                        alName.add(0, "Select Company");
                        alID.add(0, 0);
                        LoginActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", alID, alName);

                        if (circularProgressBar != null && circularProgressBar.isShowing()) {
                            circularProgressBar.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onLoginFail(AuthError error) {

                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
            }
        };

        /*client.searchRead("res.company", domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                for (OdooRecord record : records) {

                    alID.add(Integer.valueOf(record.get("id").toString()));
                    alName.add(String.valueOf(record.get("name").toString()));

                }
            }
        });*/
    }

    private void readDeliveryNumberViaLibrary(){

        client = new OdooClient.Builder(context)
                .setHost(sURL)//"https://demotrufrost.odoo.com"
                .setConnectListener(new OdooConnectListener() {
                    @Override
                    public void onConnected(OdooVersion version) {
                        client.authenticate("admin", "2019@autochip", DB_NAME, loginCallback);//"demotrufrosts-master-485814"
                    }
                }).build();

        loginCallback = new AuthenticateListener() {
            @Override
            public void onLoginSuccess(OdooUser user) {
                ODomain domain = new ODomain();
                domain.add("state", "=", "confirmed");
                domain.add("delivery_name", "=", "Deliveryv Orders");

                OdooFields fields = new OdooFields();
                fields.addAll("name");

                int offset = 0;
                int limit = 80;
                String sorting = "";

                client.searchRead("stock.picking", domain, fields, offset, limit, sorting, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        OdooRecord[] records = result.getRecords();
                        for (OdooRecord record : records) {
                            alDeliveryOrderNumber.add(record.get("name").toString());
                            //Log.e(">>", record.getString("name"));
                        }
                        MainActivity.onServiceInterface.onServiceCall("SUCCESS", 0,"", "", null, alDeliveryOrderNumber);

                        if (circularProgressBar != null && circularProgressBar.isShowing()) {
                            circularProgressBar.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onLoginFail(AuthError error) {
                if (circularProgressBar != null && circularProgressBar.isShowing()) {
                    circularProgressBar.dismiss();
                }
            }
        };


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
