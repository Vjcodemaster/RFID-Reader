package com.autochip.rfidreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app_utility.CircularProgressBar;
import app_utility.OnServiceInterface;
import app_utility.PowerReceiver;
import app_utility.RFIDAsyncTask;
import app_utility.StaticReferenceClass;
import app_utility.StringUtils;

import static app_utility.StaticReferenceClass.sINVENTORYURL;
import static app_utility.StaticReferenceClass.sRFIDURL;

public class MainActivity extends AppCompatActivity implements OnServiceInterface {

    public static OnServiceInterface onServiceInterface;
    TextView tvRFID;
    TextView tvStatus;
    Switch aSwitch, switchDelivery;
    Handler handler;
    RFIDWithUHF mReader;

    private CircularProgressBar circularProgressBar;

    RecyclerView rvProducts;
    private ProductsRVAdapter productsRVAdapter;

    filterAdapter listAdapter;

    private int nFlagSize;
    private static int stockFlag;
    private boolean isInitialized = false;
    private ArrayList<HashMap<String, String>> tagList;
    private boolean loopFlag = false;
    TextView tvTotalRFIDs, tvQuantityHeading;
    ArrayList<String> alDeliveryOrderNumber = new ArrayList<>();
    AutoCompleteTextView textView;
    //RelativeLayout rlRVHeading;
    LinearLayout llRVParent;

    ArrayAdapter<String> adapter;

    IntentFilter intentFilter;
    BroadcastReceiver mReceiver;

    String itemAtPosition = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onServiceInterface = this;
        tagList = new ArrayList<>();
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        //intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new PowerReceiver();
        registerReceiver(mReceiver, intentFilter);

        rvProducts = findViewById(R.id.rv_products);
        //productsRVAdapter = new ProductsRVAdapter(MainActivity.this);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProducts.setLayoutManager(mLinearLayoutManager);
        rvProducts.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        rvProducts.setHasFixedSize(true);

        //productsRVAdapter = new ProductsRVAdapter(MainActivity.this);
        //rvProducts.setAdapter(productsRVAdapter);
        /*RFIDAsyncTask RFIDAsyncTask = new RFIDAsyncTask(MainActivity.this);
        RFIDAsyncTask.execute(String.valueOf(2));*/
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
        tvQuantityHeading = findViewById(R.id.tv_quantity_heading);
        tvTotalRFIDs = findViewById(R.id.tv_total_rfids);
        aSwitch = findViewById(R.id.swtich_stock);
        //rlRVHeading = findViewById(R.id.rl_rv_heading);
        llRVParent = findViewById(R.id.ll_rv_parent);

        switchDelivery = findViewById(R.id.switch_delivery_order);
        textView = findViewById(R.id.actv_delivery_order_no);
        textView.setThreshold(1);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemAtPosition = parent.getItemAtPosition(position).toString().trim();
                //textView.setEnabled(false);
            }
        });

        /*Intent in = new Intent(MainActivity.this, RFIDReadService.class);
        startService(in);*/

        switchDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    aSwitch.setClickable(false);
                    textView.setVisibility(View.VISIBLE);
                    //textView.setEnabled(true);
                    //textView.clearListSelection();
                    RFIDAsyncTask RFIDAsyncTask = new RFIDAsyncTask(MainActivity.this);
                    RFIDAsyncTask.execute(String.valueOf(2));
                    /*alDeliveryOrderNumber.add("ASDA3242342");
                alDeliveryOrderNumber.add("HYJGHJ21212");
                alDeliveryOrderNumber.add("CVCVQW32333");
                alDeliveryOrderNumber.add("KIPP632B2U9");
                final String[] COUNTRIES = new String[] {
                        "ASDGW23234324322", "WGVCC670102020", "TPQPA306619974", "LOLVL188259968", "PYNGH8888445520"
                };

                ArrayList<String> al = new ArrayList<>(Arrays.asList(COUNTRIES));
                    listAdapter = new filterAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, al);*/
                /*adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, al);*/

                    textView.setAdapter(listAdapter);

                } else {
                    aSwitch.setClickable(true);
                    textView.setVisibility(View.GONE);
                    itemAtPosition = "";
                    hideKeyboard(buttonView);
                }
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    stockFlag = 1;
                    switchDelivery.setClickable(false);
                    Log.e("stockflag", " " + 1);
                } else {
                    stockFlag = 0;
                    switchDelivery.setClickable(true);
                    Log.e("stockflag", " " + 0);
                }
            }
        });

        /*final String[] COUNTRIES = new String[] {
                "ASDGW23234324322", "WGVCC23234324322", "TPQPA23234324322", "LOLVL23234324322", "PYNGH23234324322"
        };*/


        textView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.showDropDown();
                return false;
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onStop() {
         /*
        unregistering receivers to avoid memory leak
         */
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mReader.free();
        super.onStop();
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


               /*if(switchDelivery.isChecked() && itemAtPosition.equals("")){
                   Toast.makeText(MainActivity.this, "Please select Order", Toast.LENGTH_SHORT).show();
               } else {*/
                if (!loopFlag) {
                    if (switchDelivery.isChecked() && itemAtPosition.equals("")) {
                        Toast.makeText(MainActivity.this, "Please select Order", Toast.LENGTH_SHORT).show();
                    } else {
                        textView.setEnabled(false);
                        nFlagSize = 0;
                        readTags();
                        //rlRVHeading.setVisibility(View.GONE);
                        llRVParent.setVisibility(View.GONE);
                        rvProducts.setVisibility(View.GONE);
                    }
                } else {
                    stopScan();
                }
                //}
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void readTags() {


        if (mReader.startInventoryTag(0, 0)) {
            tvStatus.setText("");
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
            tvRFID.setText("");
            /*if (mContext.mReader.stopInventory()) {
                //BtInventory.setText(mContext.getString(R.string.btInventory));
            }*/ /*else {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_inventory_stop_fail);
            }*/
        }
    }

    private void sendDataToServer() {
        String sMsg;
        String URL;
        if (stockFlag == 0) {
            sMsg = "false";
            URL = sRFIDURL;
        } else {
            sMsg = "true";
            URL = sINVENTORYURL;
        }
        ArrayList<String> alRFIDs = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("db", StaticReferenceClass.DB_NAME); //Trufrost-Testing
            jsonObject.put("user", StaticReferenceClass.USER_ID);
            jsonObject.put("password", StaticReferenceClass.PASSWORD);

            if (!itemAtPosition.equals("")) {
                jsonObject.put("gatepass_num", itemAtPosition);
            } else {
                jsonObject.put("start_inventory", sMsg);
            }

            HashMap<String, String> hm;

            for (int i = 0; i < tagList.size(); i++) {
                hm = new HashMap<>(tagList.get(i));
                alRFIDs.add(hm.get("tagUii").substring(4));
            }
            //jsonInner.put("tagUii", alRFIDs);
            JSONArray jsonArray = new JSONArray(alRFIDs);
            //jsonArray.put(alRFIDs);
            jsonObject.put("rfids", jsonArray);
            //jsonObject.put("start_inventory", sMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*HashMap<String, String> params = new HashMap<>();
        //ArrayList<String> alTmp = new ArrayList<>();
        //alTmp.add(a.toString());
        params.put("db", "Trufrost-server1"); //Trufrost-Testing
        params.put("user", "admin");
        params.put("password", "a"); //autochip@505
        //String text = a.toString();
        //String sRFID = text.substring(0, a.length() - 2);
        HashMap<String, String> hm;
        ArrayList<String> alRFIDs = new ArrayList<>();

        for(int i=0;i<tagList.size();i++){
            hm = new HashMap<>(tagList.get(i));
            alRFIDs.add(hm.get("tagUii").substring(4));
        }
        String sRFID = android.text.TextUtils.join(",", alRFIDs);
        params.put("rfids", sRFID);
        params.put("start_inventory", sMsg);*/
        if (alRFIDs.size() >= 1) {
            setProgressBar();
            VolleyTask volleyTask = new VolleyTask(getApplicationContext(), jsonObject, "PUSH_RFID", stockFlag, URL);
            tagList = new ArrayList<>();
        } else {
            Toast.makeText(MainActivity.this, "No RFIDs found", Toast.LENGTH_SHORT).show();
        }
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

            HashMap<String, String> map = new HashMap<>();

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

            if (tagList.size() > nFlagSize) {
                //String sb = tvRFID.getText() + System.getProperty("line.separator") + map.get("tagUii");
                if (nFlagSize == 0) {
                    tvRFID.setText("");
                }
                String sb = tvRFID.getText() + System.getProperty("line.separator") + map.get("tagUii").substring(4);

                tvRFID.setText(sb);
                tvTotalRFIDs.setText(String.valueOf(tagList.size()));
            }


            //adapter.notifyDataSetChanged();

        }
        nFlagSize = tagList.size();
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

    class InitTask extends AsyncTask<String, Integer, Boolean> {
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
    public void onServiceCall(String sCase, int code, String sMSG, String sSubmitStatus, ArrayList<Integer> alID, ArrayList<String> alName) {
        switch (sCase) {
            case "RFID":
                if (code == 900 || code == 300 || code == 202 || code == 203 || code == 402) {
                    tvStatus.setText(sSubmitStatus);
                    tvTotalRFIDs.setText("");
                }
                if (code == 901) {
                    tvStatus.setText(sSubmitStatus);
                    tvTotalRFIDs.setText("");
                }
                if (code == 902) {
                    tvQuantityHeading.setText("Delv. Quantity");
                    tvStatus.setText(sSubmitStatus);
                    tvTotalRFIDs.setText("");
                }
                //tvRFID.setText(sMSG);
                /*tvTotalRFIDs.setText("");
                 *//*StringBuilder sb = new StringBuilder();
                String sPreviousString = "";
                for(int i=0; i<alName.size();i++) {
                    sb.append(sPreviousString).append(System.getProperty("line.separator")).append(alName.get(i));
                    sPreviousString = alName.get(i);
                }*//*
                String s = TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")), alName);
                //tvRFID.setText(sb);
                tvStatus.setText(s);*/

                if (code == 201 || code == 200)
                    if (alName != null && alName.size() >= 1) {
                        tvQuantityHeading.setText("Recv. Quantity");
                        productsRVAdapter = new ProductsRVAdapter(MainActivity.this, alName, alID);
                        rvProducts.setAdapter(productsRVAdapter);
                        //rlRVHeading.setVisibility(View.VISIBLE);
                        llRVParent.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.VISIBLE);
                        tvTotalRFIDs.setText("");
                        tvRFID.setText("");
                    }
                break;
            case "SUCCESS":
                this.alDeliveryOrderNumber = alName;
                /*alDeliveryOrderNumber.add("ASDA3242342");
                alDeliveryOrderNumber.add("HYJGHJ21212");
                alDeliveryOrderNumber.add("CVCVQW32333");
                alDeliveryOrderNumber.add("KIPP632B2U9");*/
                /*final String[] COUNTRIES = new String[] {
                        "ASDGW23234324322", "WGVCC23234324322", "TPQPA23234324322", "LOLVL23234324322", "PYNGH23234324322"
                };
                ArrayList<String> al = new ArrayList<>(Arrays.asList(COUNTRIES));*/
                listAdapter = new filterAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, alDeliveryOrderNumber);
                /*adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, alDeliveryOrderNumber);*/

                textView.setAdapter(listAdapter);
                //textView.setAdapter(adapter);
                break;
            case "STOP_PROGRESS_BAR":
                tvStatus.setText(sSubmitStatus);
                circularProgressBar.dismiss();
                break;
        }
        if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }
        if(switchDelivery.isChecked()){
            textView.setEnabled(true);
            textView.setText("");
            itemAtPosition = "";
        }

    }

    public class filterAdapter extends ArrayAdapter<String> implements Filterable {

        ArrayList<String> originalList;
        ArrayList<String> filteredList;

        public filterAdapter(Context context, int textViewResourceId, ArrayList<String> item) {
            super(context, textViewResourceId, item);
            filteredList = item;
            originalList = new ArrayList<>(filteredList);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public String getItem(int position) {
            return filteredList.get(position);
        }

        /*@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            super.getView(position, convertView, parent);
            TextView tv;

            if(convertView!= null)
                tv = (TextView)convertView;
            else
                tv = new TextView(MainActivity.this);

            //changing text size and adding icons to sightseer and destination heading
            *//*if(position == 0)
            {
                tv.setText(filteredList.get(position));
                tv.setTextSize(autoCompleteTextView.getTextSize() - 1);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.cognito);
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                tv.setTextColor(Color.parseColor("#999999"));
            }
            else if(filteredList.get(position).contains("Destination"))
            {
                tv.setText(filteredList.get(position));
                tv.setTextSize(autoCompleteTextView.getTextSize() - 1);
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.favicon);
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                tv.setTextColor(Color.parseColor("#999999"));
            }
            else{
                tv.setText(filteredList.get(position));
                tv.setTextSize(autoCompleteTextView.getTextSize() - 5);
            }*//*
            return tv;
        }*/
        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = (String) resultValue;
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    filteredList.clear();
                    for (String item : originalList) {
                        if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                    return filterResults;
                } else {
                    //filteredList = originalList;
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                /*List<String> filterList = (ArrayList<String>) results.values;
                if (results.count > 0) {
                    //clear();
                    for (String item : filterList) {
                        add(item);

                    }
                    notifyDataSetChanged();
                }*/
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    listAdapter = new filterAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, originalList);
                /*adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, alDeliveryOrderNumber);*/

                    textView.setAdapter(listAdapter);
                    textView.showDropDown();
                }
            }
        };

        /*Filter nameFilter = new Filter()
        {
            @Override
            public CharSequence convertResultToString(Object resultValue)
            {
                String str = (String) resultValue;
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                if (constraint != null)
                {
                    filteredList.clear();
                    for (String names : originalList)
                    {
                        if (names.toLowerCase().contains(constraint.toString().toLowerCase().substring(names.length()-4, names.length()-1)))
                        {
                            filteredList.add(names);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                    return filterResults;
                }
                else
                {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                List<String> filterList = (ArrayList<String>) results.values;
                if (results.count > 0)
                {
                    clear();
                    for (String item : filterList)
                    {
                        add(item);
                        notifyDataSetChanged();
                    }
                }
            }
        };*/
    }

    private void setProgressBar() {
        circularProgressBar = new CircularProgressBar(MainActivity.this);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }
}
