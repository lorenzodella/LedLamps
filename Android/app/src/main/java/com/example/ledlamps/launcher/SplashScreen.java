// Della Matera Lorenzo 5E

package com.example.ledlamps.launcher;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;
import com.example.ledlamps.main.MainActivity;
import com.example.ledlamps.R;
import com.example.ledlamps.user.User;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity implements JsonHttpRequest.OnSyncReadyListener {
    SharedPreferences sharedPref;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        RelativeLayout relativeLayout = findViewById(R.id.splash_bg);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        LedLampsUtils.setSystem(getApplicationContext());
        System.out.println(LedLampsUtils.getSystemSsid());
        checkLocationPermission();
    }


    private void load(){
        String system_ssid = LedLampsUtils.getSystemSsid();
        sharedPref = getSharedPreferences("user", 0);
        System.out.println(system_ssid);

        String last_ssid = getSharedPreferences("settings",0).getString("last_ssid",null);
        //Log.d("last",last_ssid);
        //Log.d("cur",system_ssid);

        if(system_ssid.equals("\"LedLamps\"") || system_ssid.equals("\""+last_ssid+"\"")){
            User.setUser(
                    sharedPref.getInt("idUser", 0),
                    sharedPref.getString("username", ""),
                    sharedPref.getString("password", ""),
                    sharedPref.getString("name", ""),
                    sharedPref.getString("surname", ""),
                    sharedPref.getString("mail", ""),
                    sharedPref.getString("idLamp", "")
            );
            if (sharedPref.getBoolean("message", true)) {
                message();
            } else {
                start();
            }
            return;
        }

        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");

        if(username.isEmpty() && password.isEmpty()){
            login();
        }
        else {
            checkUtente(username, password);
        }
    }

    private void message(){
        new AlertDialog.Builder(this)
                .setTitle("Warning!")
                .setMessage("You are not connected to cloud, your personal information may not be correct. Do you want to continue?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNeutralButton("yes, don't ask anymore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("user", 0).edit();
                        editor.putBoolean("message", false);
                        editor.apply();
                        start();
                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        start();
                    }
                }).show();
    }

    private void checkUtente(String strUsername, String strPassword) {
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(this, true);
        jsonHttpRequest.setOnSyncReadyListener(SplashScreen.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/login.php?details=true", strUsername, strPassword);
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        try {
            User.setUser(
                    jobj.getInt("idUser"),
                    jobj.getString("username"),
                    jobj.getString("password"),
                    jobj.getString("name"),
                    jobj.getString("surname"),
                    jobj.getString("mail"),
                    jobj.getString("idLamp")
            );
            start();
        } catch (JSONException e){
            e.printStackTrace();
            onError("JSONException");
        }
    }

    private void start(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Welcome " + User.getUsername(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }


    @Override
    public void onError(String s) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        login();
    }

    private void login(){
        //Toast.makeText(getApplicationContext(), "Login non valido", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        else {
            load();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                showLocationPermissionAlert();
            } else {
                load();
            }
        }
    }

    private void showLocationPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Warning!")
                .setMessage("You must allow location access to use this app.")
                .setNegativeButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setPositiveButton("allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkLocationPermission();
                    }
                }).show();
    }

}