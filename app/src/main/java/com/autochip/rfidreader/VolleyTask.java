package com.autochip.rfidreader;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;

import app_utility.ApplicationController;

import static app_utility.StaticReferenceClass.sRFIDURL;
import static com.autochip.rfidreader.MainActivity.onServiceInterface;

public class VolleyTask {

    private Context context;
    private int mStatusCode = 0;
    //private JSONObject jsonObject = new JSONObject();
    private HashMap<String, String> params;
    //private int position;
    String msg;

    public VolleyTask(Context context, HashMap<String, String> params, String sCase) {
        this.context = context;
        this.params = params;
        Volley(sCase);
    }

    private void Volley(String sCase) {
        switch (sCase) {
            case "PUSH_RFID":
                pushRFIDToOdoo();
                break;
        }
    }

    private void pushRFIDToOdoo(){

        StringRequest request = new StringRequest(
                Request.Method.POST, sRFIDURL, //BASE_URL + Endpoint.USER
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Success
                        onPostVolleyPushRFID(mStatusCode, response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mStatusCode == 401) {
                            // HTTP Status Code: 401 Unauthorized
                        }
                    }
                }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        // add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(request);

    }

    private void onPostVolleyPushRFID(int mStatusCode, String response) {
        //SnackBarToast snackBarToast;
        switch (mStatusCode) {
            case 200: //success
                msg = "Success";
                if(onServiceInterface!=null){
                    onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                } else {
                    Intent in = new Intent(context, MainActivity.class);
                    context.startActivity(in);
                    onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                }
                Toast.makeText(context, "RFID : " + params.get("rfids") + " Submitted successfully", Toast.LENGTH_LONG).show();
                /*JSONObject jsonObject;
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
                aActivity.finish();*/

                break;
            case 201: //password is not updated

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
                    onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                } else {
                    Intent in = new Intent(context, MainActivity.class);
                    context.startActivity(in);
                    onServiceInterface.onServiceCall("RFID", params.get("rfids"), msg);
                }
                break;
        }
        /*if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }*/
    }
}
