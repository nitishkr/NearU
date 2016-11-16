package com.iiitd.team10.nearyou;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP_FetchName {
    HttpURLConnection httpURLConnection = null;

    public String read(String httpUrl) throws IOException {
       if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
        String httpData = "";
        InputStream inputStream = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            new Connection().execute();
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
            if(inputStream != null)
                inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
               // Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                httpURLConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}