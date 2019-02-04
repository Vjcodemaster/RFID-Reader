package com.autochip.rfidreader;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app_utility.ApplicationController;

import static com.autochip.rfidreader.MainActivity.onServiceInterface;
import static com.autochip.rfidreader.RFIDReadService.isAlreadyInProgress;

public class VolleyTask {

    private Context context;
    private int mStatusCode = 0;
    //private JSONObject jsonObject = new JSONObject();
    private HashMap<String, String> params;
    //private int position;
    String msg;

    private int ERROR_CODE = 0;

    ArrayList<String> alData;
    ArrayList<Integer> alID;
    int stockFlag;
    String URL;
    JSONObject jsonObject = new JSONObject();

    public VolleyTask(Context context, JSONObject jsonObject, String sCase, int stockFlag, String URL) {
        this.context = context;
        this.jsonObject = jsonObject;
        this.stockFlag = stockFlag;
        this.URL = URL;
        Volley(sCase);
    }

    public VolleyTask(Context context, HashMap<String, String> params, String sCase, int stockFlag, String URL) {
        this.context = context;
        this.params = params;
        this.stockFlag = stockFlag;
        this.URL = URL;
        Volley(sCase);
    }

    private void Volley(String sCase) {
        switch (sCase) {
            case "PUSH_RFID":
                pushRFIDToOdoo(URL);
                break;
            /*case "OPEN_CONNECTION":
                openConnectionOdoo();
                break;*/
        }
    }

    private void pushRFIDToOdoo(String URL) {

        StringRequest request = new StringRequest(
                Request.Method.POST, URL, //BASE_URL + Endpoint.USER
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Success
                        isAlreadyInProgress = false;
                        onPostVolleyPushRFID(mStatusCode, response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isAlreadyInProgress = false;
                        msg = "No response from Server";
                        onServiceInterface.onServiceCall("STOP_PROGRESS_BAR", 0, params.get("rfids"), msg, null, null);
                        /*if (error.toString().equals("com.android.volley.TimeoutError")) {
                            return;
                        }
                        if (mStatusCode == 401) {
                            // HTTP Status Code: 401 Unauthorized
                        }*/
                    }
                }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                //return new JSONObject(params).toString().getBytes();
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));

        /*request.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        // add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(request);

    }

    /*private void openConnectionOdoo(){
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, sRFIDURL,new JSONObject(params), future, future);
        ApplicationController.getInstance().addToRequestQueue(request);

        try {
            JSONObject response = future.get();

        } catch (InterruptedException | ExecutionException e) {
        }
    }*/

    /*private void onPostVolleyPushRFID(int mStatusCode, String response) {
        //SnackBarToast snackBarToast;

        switch (mStatusCode) {
            case 200: //success
                JSONObject jsonObject;
                int sResponseCode = 0;
                try {
                    jsonObject = new JSONObject(response);
                    String sResult = jsonObject.getString("result");
                    jsonObject = new JSONObject(sResult);
                    sResponseCode = jsonObject.getInt("response_code");

                    if(sResponseCode==200) {
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        //JSONObject jsonObject1;
                        alData = new ArrayList<>();
                        alID = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String product = jsonArray.getJSONObject(i).getString("product");
                            String quantity = jsonArray.getJSONObject(i).getString("quantity_received");

                            //alData.add(product + " : " + quantity);
                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                            //jsonObject1 = new JSONObject(jsonArray.getJSONObject(0).getJSONArray());
                        }
                    }

                    if(sResponseCode==201){
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        //JSONObject jsonObject1;
                        alData = new ArrayList<>();
                        alID =  new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            String product = jsonArray.getJSONObject(i).getString("name");
                            String quantity = jsonArray.getJSONObject(i).getString("product_uom_qty");

                            //alData.add(product + " : " + quantity);
                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                            //jsonObject1 = new JSONObject(jsonArray.getJSONObject(0).getJSONArray());
                        }
                        sendMsgToActivity();
                        *//*ERROR_CODE = 901;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                        return;*//*
                    }
                    if(sResponseCode==300){
                        ERROR_CODE = 0;
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        //JSONObject jsonObject1;
                        alData = new ArrayList<>();
                        alID =  new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            String product = jsonArray.getJSONObject(i).getString("name");
                            String quantity = jsonArray.getJSONObject(i).getString("product_uom_qty");

                            //alData.add(product + " : " + quantity);
                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                            //jsonObject1 = new JSONObject(jsonArray.getJSONObject(0).getJSONArray());
                        }
                        *//*ERROR_CODE = 902;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                        return;*//*
                        //return;
                    }

                    if(sResponseCode==300){
                        ERROR_CODE = 0;
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        //JSONObject jsonObject1;
                        alData = new ArrayList<>();
                        alID =  new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            String product = jsonArray.getJSONObject(i).getString("name");
                            String quantity = jsonArray.getJSONObject(i).getString("product_uom_qty");

                            //alData.add(product + " : " + quantity);
                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                            //jsonObject1 = new JSONObject(jsonArray.getJSONObject(0).getJSONArray());
                        }
                        *//*ERROR_CODE = 902;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                        return;*//*
                        //return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(sResponseCode==300){

                    }
                    ERROR_CODE = 901;
                    msg = "Unable to reach server, please try again";
                    sendMsgToActivity();
                    *//*try {
                        onServiceInterface.onServiceCall("RFID", String.valueOf(this.jsonObject.get("rfids")), msg);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }*//*
                }
                if(sResponseCode == 200) {
                    //msg = "Success";
                    if (onServiceInterface != null) {
                        sendMsgToActivity();
                        //onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                    } else {
                        Intent in = new Intent(context, MainActivity.class);
                        context.startActivity(in);
                        sendMsgToActivity();
                        //onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                    }
                    //Toast.makeText(context, "RFID : " + params.get("rfids") + msg, Toast.LENGTH_LONG).show();
                    *//*for (int i=0; i<2; i++){
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }*//*

                } else if(sResponseCode == 300){
                    //msg = "RFID Doesn't exist";
                    if(onServiceInterface!=null){
                        //onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                        sendMsgToActivity();
                    } else {
                        Intent in = new Intent(context, MainActivity.class);
                        context.startActivity(in);
                        sendMsgToActivity();
                        //onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                    }
                    //Toast.makeText(context, "RFID : " + params.get("rfids") + msg, Toast.LENGTH_LONG).show();
                    *//*for (int i=0; i<2; i++){
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }*//*
                }
                *//*JSONObject jsonObject;
                PreferenceClass preferenceClass = new PreferenceClass(aActivity);

                try {
                    jsonObject = new JSONObject(response);
                    JSONObject jsonObjectUser = jsonObject.getJSONObject("user");
                    String sContactNumber = jsonObjectUser.getString("ContactMobileNumber");
                    preferenceClass.setUserMobileNumber(sContactNumber);
                    JSONArray jsonObjectProfile = jsonObjectUser.getJSONArray("BusinessProfile");
                    //String strUserSelfRating = jsonObjectProfile.getJSONObject(0).getString("SelfRating");

                    preferenceClass.setProfile(true);
                    preferenceClass.setBusinessName(jsonObjectProfile.getJSONObject(0).getString("BusinessName"));
                    preferenceClass.setUserPic(jsonObjectProfile.getJSONObject(0).getString("Logo"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sUserId = params.get("VendorID");
                String sPassWord = params.get("Pwd");

                preferenceClass.setUserIDPassword(sUserId, sPassWord);
                preferenceClass.setUserLogStatus(true);

                Intent in = new Intent(aActivity, HomeScreenActivity.class);
                aActivity.startActivity(in);
                aActivity.finish();*//*

                break;
            case 300: //RFID not exist
                msg = "RFID Doesn't exist";
                if(onServiceInterface!=null){
                    onServiceInterface.onServiceCall("RFID", 0, params.get("rfids"), msg, null, null);
                } else {
                    Intent in = new Intent(context, MainActivity.class);
                    context.startActivity(in);
                    onServiceInterface.onServiceCall("RFID", 0,params.get("rfids"), msg, null, null);
                }
                Toast.makeText(context, "RFID : " + params.get("rfids") + " Doesn't exist", Toast.LENGTH_LONG).show();
                break;
            case 202: //db error

                break;
            case 203: //user not found
                //snackBarToast = new SnackBarToast(aActivity, aActivity.getResources().ge4tString(R.string.user_not_found));
                break;
            case 204: //authentication failed(wrong password)
                //snackBarToast = new SnackBarToast(aActivity, aActivity.getResources().getString(R.string.wrong_password));
                break;
            case 400:
                msg = "Failed to Submit, please check if server is available";
                if(onServiceInterface!=null){
                    onServiceInterface.onServiceCall("RFID", 0, params.get("rfids"), msg, null, null);
                } else {
                    Intent in = new Intent(context, MainActivity.class);
                    context.startActivity(in);
                    onServiceInterface.onServiceCall("RFID", 0, params.get("rfids"), msg, null, null);
                }
                break;
        }
        *//*if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }*//*
    }*/

    private void onPostVolleyPushRFID(int mStatusCode, String response) {
        if (mStatusCode == 200) {
            JSONObject jsonObject;
            int sResponseCode = 0;
            try {
                jsonObject = new JSONObject(response);
                String sResult = jsonObject.getString("result");
                jsonObject = new JSONObject(sResult);
                sResponseCode = jsonObject.getInt("response_code");
            } catch (Exception e) {
                ERROR_CODE = 900;
                msg = "No IDS matched";
                e.printStackTrace();
                sendMsgToActivity();
                return;
            }
            if (sResponseCode == 0) {
                msg = "Unable to connect to server, please try again later";
                sendMsgToActivity();
                return;
            }
            switch (sResponseCode) {
                case 200: //success
                    ERROR_CODE = 200;
                    try {
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        alData = new ArrayList<>();
                        alID = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String product = jsonArray.getJSONObject(i).getString("product");
                            String quantity = jsonArray.getJSONObject(i).getString("quantity_received");

                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                        }
                        sendMsgToActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ERROR_CODE = 901;
                        msg = "Unable to reach server, please try again";
                        sendMsgToActivity();
                    }

                    break;
                case 201:
                    ERROR_CODE = 201;
                    try {
                        msg = jsonObject.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        //JSONObject jsonObject1;
                        alData = new ArrayList<>();
                        alID = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String product = jsonArray.getJSONObject(i).getString("name");
                            String quantity = jsonArray.getJSONObject(i).getString("quantity_done");

                            alData.add(product);
                            alID.add(Integer.valueOf(quantity));
                        }
                        sendMsgToActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 202:
                    try {
                        ERROR_CODE = 202;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 203:
                    try {
                        ERROR_CODE = 203;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 300: //RFID not exist
                    try {
                        ERROR_CODE = 300;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 204: //authentication failed(wrong password)
                    //snackBarToast = new SnackBarToast(aActivity, aActivity.getResources().getString(R.string.wrong_password));
                    break;
                case 402:
                    try {
                        ERROR_CODE = 402;
                        msg = jsonObject.getString("message");
                        sendMsgToActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        /*if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }*/
    }

    private void sendMsgToActivity() {
        try {
            onServiceInterface.onServiceCall("RFID", ERROR_CODE, String.valueOf(this.jsonObject.get("rfids")), msg, alID, alData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
