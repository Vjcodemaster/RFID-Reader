package app_utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceClass {

    private SharedPreferences sharedPreferences;
    private Context _context;

    private static final String APP_PREFERENCES = "RFIDReaderPreferences";

    private static final int PRIVATE_MODE = 0;

    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    private static final String COMPANY_NAME = "COMPANY_NAME";



    // Constructor
    public SharedPreferenceClass(Context context) {
        this._context = context;

        sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }

    public void setCompanyPreference(String value){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(COMPANY_NAME, value);
        editor.apply();
    }

    public String getCompanyName(){
        return sharedPreferences.getString(COMPANY_NAME, "");
    }

    /*public void setIsStartInventory(){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.putString(USER_NAME, name);
        editor.putString(USER_PHONE_NO, number);
        editor.apply();
    }*/

    /*public void setUserLogStatus(boolean isLoggedIn, String name, String number){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.putString(USER_NAME, name);
        editor.putString(USER_PHONE_NO, number);
        editor.apply();
    }

    public boolean getUserLogStatus() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }*/

    /*public String getUserName(){
        return sharedPreferences.getString(USER_NAME, "");
    }

    public String getUserID(){
        return sharedPreferences.getString(USER_PHONE_NO, "");
    }

    public void setIfUserIsTraceable(boolean isTraceable){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_TRACEABLE, isTraceable);
        editor.apply();
    }

    public void setUserType(int userType){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putInt(USER_TYPE, userType);
        editor.apply();
    }

    public int getUserType(){
        return sharedPreferences.getInt(USER_TYPE, 1);
    }

    public boolean getUserTraceableInfo() {
        return sharedPreferences.getBoolean(IS_TRACEABLE, false);
    }

    public void setUserList(ArrayList<String> alUsers, ArrayList<String> alNames, ArrayList<String> alAdminPermissionList){
        Set<String> set = new LinkedHashSet<>(alUsers);
        Set<String> setName = new LinkedHashSet<>(alNames);

        StringBuilder sb = new StringBuilder(alAdminPermissionList.size());
        String sPermissionList;
        for(int i=0; i<alAdminPermissionList.size(); i++){
            sb.append(alAdminPermissionList.get(i));
            sb.append(",");
        }
        sPermissionList = sb.toString();

        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putStringSet(USER_LIST, set);
        editor.putStringSet(USER_LIST_NAME, setName);
        editor.putString(ADMIN_PERMISSION_LIST, sPermissionList);
        editor.apply();
    }

    public Set<String> getUserList(){
        return sharedPreferences.getStringSet(USER_LIST, null);
    }

    public Set<String> getNamesList(){
        return sharedPreferences.getStringSet(USER_LIST_NAME, null);
    }

    public ArrayList<String> getAdminPermissionList(){
        if(sharedPreferences.getString(ADMIN_PERMISSION_LIST, null)!=null) {
            String[] s = Objects.requireNonNull(sharedPreferences.getString(ADMIN_PERMISSION_LIST, null)).split(",");
            return new ArrayList<>(Arrays.asList(s));
        }
        return null;
    }

    //only for xiaomi devices
    public void setUserAutoStartPermission(boolean isGranted){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTO_START_PERMISSION, isGranted);
        editor.apply();
    }

    public boolean getUserAutoStartPermission() {
        return sharedPreferences.getBoolean(USER_AUTO_START_PERMISSION, false);
    }*/
}
