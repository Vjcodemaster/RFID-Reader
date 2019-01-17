package com.autochip.rfidreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.HashMap;

import app_utility.OnServiceInterface;
import app_utility.SharedPreferenceClass;
import app_utility.StringUtils;

import static app_utility.StaticReferenceClass.sINVENTORYURL;
import static app_utility.StaticReferenceClass.sRFIDURL;

public class MainActivity extends AppCompatActivity implements OnServiceInterface {

    public static OnServiceInterface onServiceInterface;
    TextView tvRFID;
    TextView tvStatus;
    Switch aSwitch;
    Handler handler;
    RFIDWithUHF mReader;
    private static int stockFlag;
    private boolean isInitialized = false;
    private ArrayList<HashMap<String, String>> tagList;
    private boolean loopFlag = false;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagList = new ArrayList<>();


        /*Intent in = new Intent(MainActivity.this, RFIDDService.class);
        startService(in);*/
        //SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(MainActivity.this);
        try {
            mReader = RFIDWithUHF.getInstance();
            //mReader.init();
            if (mReader != null && !isInitialized) {
                new InitTask().execute();
                isInitialized = true;
            }
        } catch (Exception ex) {
            //toastMessage(ex.getMessage());
            return;
        }
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[1]);
                return true;
            }
        });
        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[1]);
                //mContext.playSound(1);
            }
        };*/

        tvRFID = findViewById(R.id.tv_rfid);
        tvStatus = findViewById(R.id.tv_status);
        aSwitch = findViewById(R.id.swtich_stock);

        onServiceInterface = this;
        /*Intent in = new Intent(MainActivity.this, RFIDReadService.class);
        startService(in);*/

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RFIDReadService.stockFlag = 1;
                    Log.e("stockflag", " " + 1);
                } else {
                    RFIDReadService.stockFlag = 0;
                    Log.e("stockflag", " " + 0);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139 || keyCode == 280) {

            if (event.getRepeatCount() == 0) {
                //RFIDDService.onServiceInterface.onServiceCall("START_READ", "", "");
                /*if (loopFlag)
                    loopFlag = false;
                else
                    readTags();*/

               /* if (mViewPager != null) {

                    KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                    sf.myOnKeyDwon();

                }*/


                if(!loopFlag)
                    readTags();
                else
                    stopScan();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void readTags() {


        if (mReader.startInventoryTag(0, 0)) {
            /*BtInventory.setText(mContext
                    .getString(R.string.title_stop_Inventory));*/
            loopFlag = true;
            //setViewEnabled(false);
            new TagThread().start();
        }

    }

    private void stopScan() {
        if (loopFlag) {
            loopFlag = false;
            //setViewEnabled(true);
            mReader.stopInventory();
            sendDataToServer();
            /*if (mContext.mReader.stopInventory()) {
                //BtInventory.setText(mContext.getString(R.string.btInventory));
            }*/ /*else {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_inventory_stop_fail);
            }*/
        }
    }

    private void sendDataToServer(){
        String sMsg;
        String URL;
        if(stockFlag==0) {
            sMsg = "false";
            URL = sRFIDURL;
        }
        else {
            sMsg = "true";
            URL = sINVENTORYURL;
        }
        HashMap<String, String> params = new HashMap<>();
        //ArrayList<String> alTmp = new ArrayList<>();
        //alTmp.add(a.toString());
        params.put("db", "Trufrost-Live"); //Trufrost-Testing
        params.put("user", "admin");
        params.put("password", "autochip@505");
        //String text = a.toString();
        //String sRFID = text.substring(0, a.length() - 2);
        HashMap<String, String> hm;
        ArrayList<String> alRFIDs = new ArrayList<>();
        for(int i=0;i<tagList.size();i++){
            hm = new HashMap<>(tagList.get(i));
            alRFIDs.add(hm.get("tagUii").substring(3));
        }
        String sRFID = android.text.TextUtils.join(",", alRFIDs);
        params.put("rfids", sRFID);
        params.put("start_inventory", sMsg);
        VolleyTask volleyTask = new VolleyTask(getApplicationContext(), params, "PUSH_RFID", stockFlag, URL);
        //sPreviousRFID = a.toString();
    }


    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data", "EPC:" + res[1] + "|" + strResult);
                    Message msg = handler.obtainMessage();
                    msg.obj = strResult + "EPC:" + mReader.convertUiiToEPC(res[1]) + "@" + res[2];

                    handler.sendMessage(msg);
                }
            }
        }
    }

    private void addEPCToList(String epc, String rssi) {
        if (!TextUtils.isEmpty(epc)) {
            int index = checkIsExist(epc);

            map = new HashMap<>();

            map.put("tagUii", epc);
            map.put("tagCount", String.valueOf(1));
            map.put("tagRssi", rssi);

            // mContext.getAppContext().uhfQueue.offer(epc + "\t 1");

            if (index == -1) {
                tagList.add(map);
                //LvTags.setAdapter(adapter);
                //tv_count.setText("" + adapter.getCount());
            } else {
                int tagcount = Integer.parseInt(
                        tagList.get(index).get("tagCount"), 10) + 1;

                map.put("tagCount", String.valueOf(tagcount));

                tagList.set(index, map);

            }

            //adapter.notifyDataSetChanged();

        }
    }

    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (StringUtils.isEmpty(strEPC)) {
            return existFlag;
        }
        String tempStr;
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp;
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                Toast.makeText(MainActivity.this, "init f",
                        Toast.LENGTH_SHORT).show();
                /*if (mReader != null) {
                    new InitTask().execute();
                }*/
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
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
