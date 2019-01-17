package app_utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.autochip.rfidreader.MainActivity;

public class PowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {

            //Log.e("In on receive", "In Method:  ACTION_SCREEN_OFF");
            //countPowerOff++;
        }
        else */if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            Intent in = new Intent(context, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
            //Log.e("In on receive", "In Method:  ACTION_SCREEN_ON");
        }
    }
}
