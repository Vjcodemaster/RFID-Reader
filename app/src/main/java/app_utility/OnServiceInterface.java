package app_utility;

import java.util.ArrayList;

public interface OnServiceInterface {
    void onServiceCall(String sCase, int code, String sMSG, String sSubmitStatus, ArrayList<Integer> alID, ArrayList<String> alName);
}
