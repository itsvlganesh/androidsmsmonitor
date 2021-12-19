package com.example.helloworld;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.os.Handler;


public class SMSObserver  extends ContentObserver {

        public SMSObserver(Handler handler) {
            super(handler);
        }



    @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // save the message to the SD card here
            System.out.println("Mabrook-------------Message Received-----------------------------------------------------------");
        }

}
