package com.iiitd.team10.nearyou;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class HTTP_FetchName {
    String data=null;

    public String read(String httpUrl) throws IOException, ExecutionException, InterruptedException {
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
        }*/
        data=new Connection().execute(httpUrl).get();
        return data;
    }

    private class Connection extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            String httpData = "";
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                httpData = stringBuffer.toString();
                bufferedReader.close();
            } catch (Exception e) {
                Log.d("reading Http url", e.toString());
            } finally {
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                httpURLConnection.disconnect();
            }
            return httpData;
        }

        @Override
        protected  void onPostExecute(String result) {

        }
    }

}