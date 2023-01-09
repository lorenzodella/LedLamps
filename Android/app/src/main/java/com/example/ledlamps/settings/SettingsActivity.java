// Della Matera Lorenzo 5E

package com.example.ledlamps.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.ledlamps.R;
import com.example.ledlamps.main.CustomSwipeRefreshLayout;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity implements JsonHttpRequest.OnSyncReadyListener {

    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private EditText ssid, ip;
    private Button connectWifi, connectCloud;
    private TextView isConnectedWifi, isConnectedCloud;
    private ImageView circleWifi, circleCloud;
    private TextView modeName, modeDesc;
    private boolean showToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });

        connectWifi = findViewById(R.id.connect_wifi);
        connectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
                    WifiDialog wifiDialog = new WifiDialog();
                    wifiDialog.show(getSupportFragmentManager(), "wifi dialog");
                }
                else
                    Toast.makeText(getApplicationContext(), "Connect to LedLamps net to edit settings", Toast.LENGTH_SHORT).show();
            }
        });

        circleWifi = findViewById(R.id.circle_wifi);
        isConnectedWifi = findViewById(R.id.is_connected_wifi);
        ssid = findViewById(R.id.edit_ssid);
        ssid.setInputType(InputType.TYPE_NULL);
        ip = findViewById(R.id.edit_ip);
        ip.setInputType(InputType.TYPE_NULL);
        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!ip.getText().toString().isEmpty()) {
                    isConnectedWifi.setText("connected to wifi");
                    isConnectedWifi.setTextColor(Color.GREEN);
                    circleWifi.setColorFilter(Color.GREEN);
                }
                else {
                    isConnectedWifi.setText("not connected to wifi");
                    isConnectedWifi.setTextColor(Color.RED);
                    circleWifi.setColorFilter(Color.RED);
                }
            }
        });

        connectCloud = findViewById(R.id.connect_cloud);
        connectCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(isConnectedWifi.getText()).contains("not")) {
                    Toast.makeText(getApplicationContext(), "Lamps must be connected to wifi", Toast.LENGTH_SHORT).show();
                }
                else if(String.valueOf(isConnectedCloud.getText()).contains("not")) {
                    if (LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
                        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getApplicationContext(), showToast);
                        jsonHttpRequest.setOnSyncReadyListener(SettingsActivity.this);
                        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/connect?host=" + LedLampsUtils.server_ip);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Connect to LedLamps net to edit settings", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Lamps are already connected to cloud", Toast.LENGTH_SHORT).show();
                }
            }
        });

        circleCloud = findViewById(R.id.circle_cloud);
        isConnectedCloud = findViewById(R.id.is_connected_cloud);
        isConnectedCloud.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(String.valueOf(isConnectedCloud.getText()).contains("not")) {
                    isConnectedCloud.setTextColor(Color.RED);
                    circleCloud.setColorFilter(Color.RED);
                }
                else{
                    isConnectedCloud.setTextColor(Color.GREEN);
                    circleCloud.setColorFilter(Color.GREEN);
                }
            }
        });

        modeName = findViewById(R.id.mode_name);
        modeDesc = findViewById(R.id.mode_desc);

        showToast = false;
        sync();

    }

    public String getSsid() {
        return ssid.getText().toString();
    }

    private void sync(){
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(this, showToast);
        jsonHttpRequest.setOnSyncReadyListener(SettingsActivity.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/sync.php?details=true");
    }

    @Override
    public void onError(String error) {
        if(error.equals("sync")) {
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(SettingsActivity.this, 18000);
            jsonHttpRequest.setOnSyncReadyListener(this);
            jsonHttpRequest.execute(LedLampsUtils.getHost() + "/sync.php?details=true");
        }
        else {
            showToast = false;
            sync();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        try {
            String mSsid = jobj.getString("ssid");
            SharedPreferences settings = getSharedPreferences("settings",0);
            settings.edit().putString("last_ssid",mSsid).apply();
            ssid.setText(mSsid);
            String mIp = jobj.getString("ip");
            ip.setText(mIp.contains("unset") ? "" : mIp);
            if(mIp.contains("unset"))
                settings.edit().clear().apply();
            else
                settings.edit().putString("last_ip",mIp).apply();

            isConnectedCloud.setText(jobj.getInt("connected") == 0 ? "not connected to cloud" : "connected to cloud");
            if(jobj.has("modeName")) {
                modeName.setText(jobj.getString("modeName"));
                modeDesc.setText(jobj.getString("modeDesc"));
            }
            else {
                modeName.setText("???");
                modeDesc.setText("You must be connected to cloud to get this information");
            }

            String system_ssid = LedLampsUtils.getSystemSsid();
            if(!system_ssid.equals("\"LedLamps\"") && !system_ssid.equals(mSsid) &&
                    jobj.getInt("connected") == 0 && showToast){
                Toast.makeText(this, "Your lamp is not connected!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
        showToast = true;
    }
}