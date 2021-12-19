package com.example.helloworld;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivityBackup extends AppCompatActivity {

EditText editText;
Button addLink;
SMSObserver smsObserver;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        System.out.println("Vasa Create================================================");


        final ListView lViewSMS = (ListView) findViewById(R.id.listViewSMS);
        editText  = (EditText)findViewById(R.id.link);
        addLink = (Button) findViewById(R.id.addLink);

        addLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                String res = storeDate();
                if(res != null)
                    Toast.makeText(MainActivityBackup.this, "Link stored successfully", Toast.LENGTH_SHORT).show();
                    else
                    Toast.makeText(MainActivityBackup.this, "Unable to store", Toast.LENGTH_SHORT).show();
            }
        });




        //Permission
        if (ContextCompat.checkSelfPermission(MainActivityBackup.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivityBackup.this,
                    Manifest.permission.READ_SMS))
            {
                ActivityCompat.requestPermissions(MainActivityBackup.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivityBackup.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }

        }
        else
        {
            /* do nothing */
            /* permission is granted */
           System.out.println("Permission granted===========");
        }

        smsObserver = new SMSObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://sms/in"),true, smsObserver);

        //Fetch SMS
        if(fetchInbox()!=null)
        {
            @SuppressWarnings("" +
                    "unchecked")
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox());
            lViewSMS.setAdapter(adapter);
        }

        //retrieve link from shared preference
        String link = retrieveData();
        System.out.println("Link data:"+link);
        /*Call web service*/
        getData();


    }

    @Override
    protected void onResume() {
        System.out.println("Vasa Resume================================================");
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                Toast.makeText(MainActivityBackup.this, "This method is run every 10 seconds",
                        Toast.LENGTH_SHORT).show();
            }
        }, delay);
        super.onResume();
    }
    @Override
    protected void onPause() {
        System.out.println("Vasa Pause================================================");
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {

        System.out.println("Vasa Destroy================================================");
//        getContentResolver().
//                unregisterContentObserver(smsObserver);
        super.onDestroy();
    }



    String storeDate(){
        try {
            // Storing data into SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
            myEdit.putString("link", editText.getText().toString());

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

    String retrieveData(){
        try {
            // Retrieving the value using its keys the file name
// must be same in both saving and retrieving the data
            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
            String s1 = sh.getString("link", "");
            //int a = sh.getInt("age", 0);

// We can then use the data
            editText.setText(s1);
            return s1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }






    void getData(){
        try {
            String myUrl = "http://10.0.2.2:8080/RESTfulExample/json/product/postSMS";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("msgBody", "2021-11-29 19:59:02");
            jsonBody.put("msgSentTime", "2021-11-29 19:59:02");
            jsonBody.put("status", "A");
            jsonBody.put("serviceProvider", "STC");
            jsonBody.put("mobileNum", "96550471543");

            final String mRequestBody = jsonBody.toString();
            StringRequest myRequest = new StringRequest(Request.Method.POST, myUrl,
                    response -> {
                        try {
                            //Create a JSON object containing information from the API.
                            System.out.println("Response----------:"+response);
                            JSONObject myJsonObject = new JSONObject(response);
                            System.out.println(myJsonObject.getString("status"));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    volleyError -> Toast.makeText(MainActivityBackup.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show()
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
                        System.out.println(jsonString);
                        return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                    }catch (Exception e){
                        e.printStackTrace();
                        return  null;
                    }
                }
            };


            RequestQueue requestQueue
                    = Volley.newRequestQueue(this);
            requestQueue.add(myRequest);
        }catch (Exception w){
            w.printStackTrace();
        }
    }

    ArrayList fetchInbox (){

        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

        //Retrieves all SMS (if you want only unread SMS, put "read = 0" for the 3rd parameter)
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);
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

            if(address.equalsIgnoreCase("BoubyanBank"))
                sms.add(address+"\n"+body);
            //Do what you want
            System.out.println(body);
        }
        return sms;
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
                    if (ContextCompat.checkSelfPermission(MainActivityBackup.this,
                            Manifest.permission.SEND_SMS) ==  PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(MainActivityBackup.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivityBackup.this, "No Permission granted", Toast.LENGTH_SHORT).show();
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

            System.out.println("==================post data==============URL:"+urll);
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
                System.out.println(e.getMessage());
            }
            return null;

        }

        protected void onPostExecute(String result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // DO SOMETHING WITH STRING RESPONSE
            System.out.println("WebService result:"+result);
            Toast.makeText(MainActivityBackup.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}