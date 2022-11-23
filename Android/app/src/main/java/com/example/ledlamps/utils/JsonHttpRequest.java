// Della Matera Lorenzo 5E

package com.example.ledlamps.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import com.example.ledlamps.R;
import com.example.ledlamps.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class JsonHttpRequest extends AsyncTask<String, Void, String> {
    private Context context;
    private AlertDialog dialog;
    private String strurl;
    private int timeout;
    private boolean showToast;
    private static int DEFAULT_TIMEOUT = 6000;
    private OnSyncReadyListener onSyncReadyListener;

    public JsonHttpRequest(Context context, boolean showToast){
        this.context = context;
        this.showToast = showToast;
        this.timeout = DEFAULT_TIMEOUT;
    }
    public JsonHttpRequest(Context context, int timeout){
        this.context = context;
        this.showToast = true;
        this.timeout = timeout;
    }

    protected void onPreExecute() {
        if (timeout != DEFAULT_TIMEOUT){
            super.onPreExecute();
            startLoadingDialog();
        }
    }

    private void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        builder.setView(inflater.inflate(R.layout.layout_connecting, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(showToast) Toast.makeText(context, "sync cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            strurl = strings[0];
            if(!strurl.contains("ledlampsweb")){
                strurl = strurl.replace(".php","");
                URL url = new URL(strurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
                return request(urlConnection);
            }
            else {
                URL url = new URL(strurl);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
                return request(urlConnection, strings);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        }
    }

    private String request(HttpURLConnection urlConnection) throws IOException{
        int responseCode = urlConnection.getResponseCode();
        if(responseCode==HttpURLConnection.HTTP_ACCEPTED)
            return "sync";
        if(responseCode!=HttpURLConnection.HTTP_OK)
            return "error: server responded with code "+responseCode;

        InputStream response = urlConnection.getInputStream();
        Scanner scanner = new Scanner(response);
        String responseBody = scanner.useDelimiter("\\A").next();
        return responseBody;
    }

    private String request(HttpsURLConnection urlConnection, String... strings) throws IOException{
        String content = "";
        if(strurl.contains(".php")) {
            urlConnection.setDoOutput(true);


            if(strurl.contains("login")){
                String username = strings[1];
                String password = strings[2];
                content = "username=" + URLEncoder.encode(username, "utf-8") +
                        "&password=" + URLEncoder.encode(password, "utf-8");
            }
            else if(strurl.contains("signup")){
                String name = strings[1];
                String surname = strings[2];
                String mail = strings[3];
                String username = strings[4];
                String password = strings[5];
                String idLamp = strings[6];
                content = "name=" + URLEncoder.encode(name, "utf-8") +
                        "&surname=" + URLEncoder.encode(surname, "utf-8") +
                        "&mail=" + URLEncoder.encode(mail, "utf-8") +
                        "&username=" + URLEncoder.encode(username, "utf-8") +
                        "&password=" + URLEncoder.encode(password, "utf-8") +
                        "&idLamp=" + URLEncoder.encode(idLamp, "utf-8");
            }
            else {
                content = "idLamp=" + URLEncoder.encode(User.getIdLamp(), "utf-8");
            }


            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);

            OutputStream output = urlConnection.getOutputStream();
            output.write(content.getBytes());
            output.close();
        }

        int responseCode = urlConnection.getResponseCode();
        if(responseCode==HttpURLConnection.HTTP_ACCEPTED)
            return "sync";
        if(responseCode!=HttpURLConnection.HTTP_OK)
            return "error: server responded with code "+responseCode;

        InputStream response = urlConnection.getInputStream();
        Scanner scanner = new Scanner(response);
        String responseBody = scanner.useDelimiter("\\A").next();
        return responseBody;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }

        if(s!=null && s.equals("sync")){
            onSyncReadyListener.onError(s);
            return;
        }
        if(s!=null && !s.equals("exception")) {
            if(strurl.contains("signup")){
                if(s.contains("error: "))
                    onSyncReadyListener.onError(s.replace("error: ", ""));
                else
                    onSyncReadyListener.onSyncReady(null);
                return;
            }
            if(s.contains("error: ")){
                if(showToast) Toast.makeText(context, "Error connecting to LedLamps: " + s.replace("error: ", ""), Toast.LENGTH_SHORT).show();
                onSyncReadyListener.onError(s);
                return;
            }
            try {
                JSONObject jobj = new JSONObject(s);
                if(jobj.has("custom") && jobj.has("fade1") && jobj.has("fade2")){
                    LedLampsUtils.setCustom(jobj.getString("custom"));
                    LedLampsUtils.setFade1(jobj.getString("fade1"));
                    LedLampsUtils.setFade2(jobj.getString("fade2"));
                }

                onSyncReadyListener.onSyncReady(jobj);
            } catch (JSONException e) {
                onSyncReadyListener.onError(s);
                e.printStackTrace();
            }
        }
        else {
            if(showToast) Toast.makeText(context, "An exception has occurred", Toast.LENGTH_SHORT).show();
            onSyncReadyListener.onError(s);
        }

    }

    public interface OnSyncReadyListener {
        void onSyncReady(JSONObject jobj);
        void onError(String error);
    }

    public void setOnSyncReadyListener(OnSyncReadyListener onSyncReadyListener){
        this.onSyncReadyListener = onSyncReadyListener;
    }
}
