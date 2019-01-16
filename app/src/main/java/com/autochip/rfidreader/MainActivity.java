package com.autochip.rfidreader;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.HashMap;

import app_utility.OnServiceInterface;
import app_utility.StringUtils;

public class MainActivity extends AppCompatActivity implements OnServiceInterface {

    public static OnServiceInterface onServiceInterface;
    TextView tvRFID;
    TextView tvStatus;
    Switch aSwitch;
    Handler handler;
    RFIDWithUHF mReader;
    private ArrayList<HashMap<String, String>> tagList;
    private boolean loopFlag = false;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagList = new ArrayList<>();
        try {
            mReader = RFIDWithUHF.getInstance();
            mReader.init();
        } catch (Exception ex) {
            //toastMessage(ex.getMessage());
            return;
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                addEPCToList(strs[0], strs[1]);
                //mContext.playSound(1);
            }
        };

        tvRFID = findViewById(R.id.tv_rfid);
        tvStatus = findViewById(R.id.tv_status);
        aSwitch = findViewById(R.id.swtich_stock);

        onServiceInterface = this;
        Intent in = new Intent(MainActivity.this, RFIDReadService.class);
        startService(in);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RFIDReadService.stockFlag = 1;
                    Log.e("stockflag", " "+1);
                }
                else {
                    RFIDReadService.stockFlag = 0;
                    Log.e("stockflag", " "+0);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139 ||keyCode == 280) {

            if (event.getRepeatCount() == 0) {
                readTags();

               /* if (mViewPager != null) {

                    KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                    sf.myOnKeyDwon();

                }*/
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void readTags(){


        if (mReader.startInventoryTag(0,0)) {
            /*BtInventory.setText(mContext
                    .getString(R.string.title_stop_Inventory));*/
            loopFlag = true;
            //setViewEnabled(false);
            new TagThread().start();
        }

    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
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
                    Log.i("data","EPC:"+res[1]+"|"+strResult);
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

            map = new HashMap<String, String>();

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
        String tempStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
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
