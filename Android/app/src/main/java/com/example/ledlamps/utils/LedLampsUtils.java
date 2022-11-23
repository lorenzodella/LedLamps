// Della Matera Lorenzo 5E

package com.example.ledlamps.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.skydoves.colorpickerview.ColorEnvelope;

public class LedLampsUtils {
    private static Context context;
    private static String custom;
    private static String fade1;
    private static String fade2;
    //public static String server_ip = "dellamatera.ddns.net";
    public static String server_ip = "80.211.47.247";

    public static void setSystem(Context context) {
        LedLampsUtils.context = context;
    }

    public static String getSystemSsid() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    public static String getHost() {
        String host = "";
        //Toast.makeText(context, getSystemSsid(), Toast.LENGTH_SHORT).show();
        if (getSystemSsid().equals("\"LedLamps\""))
            host = "http://192.168.4.1";
        else
            host = "https://ledlampsweb.it/control";
        //Toast.makeText(context, "request to: "+host, Toast.LENGTH_SHORT).show();
        return host;
    }

    public static int brightness(int color) {
        ColorEnvelope c = new ColorEnvelope(color);
        int[] rgb = c.getArgb();
        return (int) Math.sqrt(
                rgb[1] * rgb[1] * .241 +
                        rgb[2] * rgb[2] * .691 +
                        rgb[3] * rgb[3] * .068);
    }

    public static String getCustom() {
        return custom;
    }

    public static void setCustom(String custom) {
        LedLampsUtils.custom = custom;
    }

    public static String getFade1() {
        return fade1;
    }

    public static void setFade1(String fade1) {
        LedLampsUtils.fade1 = fade1;
    }

    public static String getFade2() {
        return fade2;
    }

    public static void setFade2(String fade2) {
        LedLampsUtils.fade2 = fade2;
    }
}