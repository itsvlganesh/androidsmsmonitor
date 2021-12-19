package com.example.helloworld;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.content.*;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/*
 * Step 1: Create a broadcastReceiver class called SMSReceiver
 * */
public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null) {

            /*
             * Step 2: We need to fetch the incoming sms that is broadcast.
             * For this we check the intent of the receiver.
             * */

            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (int i = 0; i < pdus.length; i++) {

                SmsMessage smsMessage = Build.VERSION.SDK_INT >= 19
                        ? Telephony.Sms.Intents.getMessagesFromIntent(intent)[i]
                        : SmsMessage.createFromPdu((byte[]) pdus[i]);



                /*
                 * Step 3: We can get the sender & body of the incoming sms.
                 * The actual parsing of otp is not done here since that is not
                 * the purpose of this implementation
                 *  */
                String sender = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody().toString();
                String otpCode = "123456";



                /*
                 * Step 4: We have parsed the otp. Now we can create an intent
                 * and pass the otp data via that intent.
                 * We have to specify an action for this intent. Now this can be anything String.
                 * This action is important because this action identifies the broadcast event
                 *  */

                Intent in = new Intent("com.an.sms.example");
                Bundle extras = new Bundle();
                extras.putString("com.an.sms.example.otp", otpCode);
                in.putExtras(extras);
                context.sendBroadcast(in);
            }
        }
    }
}