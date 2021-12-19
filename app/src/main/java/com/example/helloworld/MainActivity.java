package com.example.helloworld;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.content.*;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

EditText editText,phoneText,networkText;
Button addLink,phoneLink,networkLink, btnStartService, btnStopService;
static ListView lViewSMS;
SMSObserver smsObserver;
static Context context = null;
    String mPhoneNumber, networkName;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 30000;
    static String getDataStatus = "";

    static String link = null;
    static String phone = null;
    static String network = null;

    /*
     * Step 1: Define the broadcast Receiver variable
     * */
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        System.out.println("VASA APP------------:"+"Vasa Create================================================");

context = getApplicationContext();

         lViewSMS = (ListView) findViewById(R.id.listViewSMS);
        editText  = (EditText)findViewById(R.id.link);
        addLink = (Button) findViewById(R.id.addLink);
        phoneText  = (EditText)findViewById(R.id.mobile);
        networkText  = (EditText)findViewById(R.id.network);

        addLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                String res = storeDate();
                if(res != null) {
                     link = retrieveData("link");
                     phone = retrieveData("phone");
                     network = retrieveData("network");

                    Toast.makeText(MainActivity.this, "Link stored successfully: " + link + ", "+phone+", "+network, Toast.LENGTH_SHORT).show();
                }
                    else
                    Toast.makeText(MainActivity.this, "Unable to store", Toast.LENGTH_SHORT).show();
            }
        });




        //Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_SMS))
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }

        }
        else
        {
            /* do nothing */
            /* permission is granted */
           System.out.println("VASA APP------------:"+"Permission granted===========");
        }



        //retrieve link from shared preference

         link = retrieveData("link");
         phone = retrieveData("phone");
         network = retrieveData("network");
//e        markSmsAsRead("BoubyanBank");


        TelephonyManager tMgr = (TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
         mPhoneNumber = tMgr.getLine1Number();
        networkName =  tMgr.getNetworkOperatorName();
        if(mPhoneNumber != null || !mPhoneNumber.isEmpty()) {
            System.out.println("VASA APP------------:" + "mPhoneNumber & networkName==========================" + mPhoneNumber + " networkName:=====" + networkName);
            if(link != null && !link.isEmpty()) {
                System.out.println("VASA APP------------:"+"Link data:" + link);

                //Fetch SMS - via scheduler
updateUI();



            }else
                Toast.makeText(MainActivity.this, "Please set the API link!", Toast.LENGTH_LONG).show();

        } else
            Toast.makeText(MainActivity.this, "Mobile number or SIM Card not available!", Toast.LENGTH_LONG).show();

        btnStartService = findViewById(R.id.start);
        btnStopService = findViewById(R.id.stop);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
               //registerReceiver();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
    }

    static void updateUI(){
        @SuppressWarnings("" +
                "unchecked")
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, fetchInbox());
        lViewSMS.setAdapter(adapter);
    }


    /*
     * Step 2: Register the broadcast Receiver in the activity
     * */
    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String otpCode = intent.getStringExtra("com.an.sms.example.otp");

                /*
                 * Step 3: We can update the UI of the activity here
                 * */
                Toast.makeText(MainActivity.this, "Register Receiver is working man", Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.an.sms.example"));
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        System.out.println("VASA APP------------:"+"Vasa Resume================================================");
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                Toast.makeText(MainActivity.this, "This method is run every "+delay/1000+" seconds",
//                        Toast.LENGTH_SHORT).show();
//
//
//                @SuppressWarnings("" +
//                        "unchecked")
//                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, fetchInbox());
//                lViewSMS.setAdapter(adapter);
//
//            }
//        }, delay);
        super.onResume();
    }
    @Override
    protected void onPause() {
        System.out.println("VASA APP------------:"+"Vasa Pause================================================");
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        super.onPause();
    }




    String storeDate(){
        try {
            // Storing data into SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
            myEdit.putString("link", editText.getText().toString());
            myEdit.putString("phone", phoneText.getText().toString());
            myEdit.putString("network", networkText.getText().toString());

// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
            myEdit.commit();
            return "Success";
        }catch (Exception e){

            e.printStackTrace();

        }
        return null;
    }

    String retrieveData(String key){
        try {
            // Retrieving the value using its keys the file name
// must be same in both saving and retrieving the data
            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
            String s1 = sh.getString(key, "");
            //int a = sh.getInt("age", 0);

// We can then use the data
            if(key.equalsIgnoreCase("link"))
            editText.setText(s1);
            else if(key.equalsIgnoreCase("phone"))
                phoneText.setText(s1);
            else if(key.equalsIgnoreCase("network"))
                networkText.setText(s1);
            return s1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }






    static void getData(String link, String msgBody, String phone, String network){
        try {
            System.out.println("VASA APP------------getData");
            //String myUrl = "http://10.0.2.2:8080/RESTfulExample/json/product/postSMS";
            String myUrl = link;
                    JSONObject jsonBody = new JSONObject();
            jsonBody.put("msgBody", msgBody);
            jsonBody.put("msgSentTime", msgBody);
            jsonBody.put("status", "A");
            jsonBody.put("serviceProvider", network);
            jsonBody.put("mobileNum", phone);

            final String mRequestBody = jsonBody.toString();
            StringRequest myRequest = new StringRequest(Request.Method.POST, myUrl,
                    response -> {
                        try {
                            //Create a JSON object containing information from the API.
                            System.out.println("VASA APP------------:"+"Response----------:"+response);
                            JSONObject myJsonObject = new JSONObject(response);
                            System.out.println("VASA APP------------status:"+myJsonObject.getString("status"));


                        } catch (Exception e) {
                            System.out.println("VASA APP------------getDate network exception:"+e.getMessage());
                            e.printStackTrace();
                        }
                    },
                    volleyError -> Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show()
            ){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody()  {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    try {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                        }

                        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        System.out.println("VASA APP------------JSONSTRING:"+jsonString);
                        getDataStatus = jsonString;
                        return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                    }catch (Exception e){
                        e.printStackTrace();
                        return  null;
                    }
                }
            };


            RequestQueue requestQueue
                    = Volley.newRequestQueue(context);
            requestQueue.add(myRequest);
        }catch (Exception w){
            w.printStackTrace();
        }

    }

     static ArrayList fetchInbox (){
try {
    final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
int count = 0;
    //Retrieves all SMS (if you want only unread SMS, put "read = 0" for the 3rd parameter)
    Cursor cursor = context.getContentResolver().query(SMS_INBOX, null, "read=0", null, "date desc limit 4");
    ArrayList sms = new ArrayList();
    //Get all lines
    while (cursor.moveToNext()) {
        //Gets the SMS information
        String address = cursor.getString(cursor.getColumnIndex("address"));
        String person = cursor.getString(cursor.getColumnIndex("person"));
        String date = cursor.getString(cursor.getColumnIndex("date"));
        String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
        String read = cursor.getString(cursor.getColumnIndex("read"));
        String status = cursor.getString(cursor.getColumnIndex("status"));
        String type = cursor.getString(cursor.getColumnIndex("type"));
        String subject = cursor.getString(cursor.getColumnIndex("subject"));
        String body = cursor.getString(cursor.getColumnIndex("body"));


        if (address.equalsIgnoreCase("BoubyanBank") ) {
            sms.add(address + "\n" + body);
            //Do what you want
            System.out.println("VASA APP------------:" + body);

            /*Call web service*/
           getData(link, body, phone, network);



        }
    }
    return sms;
}catch (Exception e){
    Log.e("Mark Read", "Error in Read: "+e.toString());
    System.out.println("VASA APP--------------Exception:"+e.toString());
    return null;
}
    }

    public void markSmsAsRead(final String from) {

        Thread waiter = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.w("MainActivity", "Exception while sleeping markSmsAsReadThread: " + e.getMessage());
                }

                Uri uri = Uri.parse("content://sms/inbox");
                String selection = "address = ? AND read = ?";
                String[] selectionArgs = {from, "0"};

                ContentValues values = new ContentValues();
                values.put("read", true);

                int rowsUpdated = context.getContentResolver().update(uri, values, selection, selectionArgs);
                Log.i("TAGGG", "rows updated: " + rowsUpdated);
            }
        });
        waiter.start();
    }

    /* And a method to override */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.SEND_SMS) ==  PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No Permission granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private class NetworkAsyncTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            // ... code shown above to send request and retrieve string builder
            String urll=strings[0];
            String data=strings[1];
            String[] values = {"2021-11-29 15:59:02","2021-11-29 15:59:02","A","STC","9565017543"};

            System.out.println("VASA APP------------:"+"==================post data==============URL:"+urll);
            try {
//                OkHttpClient client = new OkHttpClient();
//
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"msgBody\":\"2021-11-30 15:59:02\",\"msgSentTime\":\"2021-11-29 15:59:02\",\"status\":\"A\",\"serviceProvider\":\"STC\",\"mobileNum\":\"9565017543\"}");
//                Request request = new Request.Builder()
//                        .url(urll)
//                        .post(body)
//                        .addHeader("content-type", "application/json")
//                        .addHeader("cache-control", "no-cache")
//                        .addHeader("postman-token", "38fe77dc-ecef-4b83-7c15-de4be1d76026")
//                        .build();
//

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("VASA APP------------:"+e.getMessage());
            }
            return null;

        }

        protected void onPostExecute(String result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // DO SOMETHING WITH STRING RESPONSE
            System.out.println("VASA APP------------:"+"WebService result:"+result);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("VASA APP------------:OnStop");
        /*
         * Step 4: Ensure to unregister the receiver when the activity is destroyed so that
         * you don't face any memory leak issues in the app
         */

    }

    @Override
    protected void onDestroy() {

        System.out.println("VASA APP------------:"+"Vasa Destroy================================================");
//        getContentResolver().
//                unregisterContentObserver(smsObserver);
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}