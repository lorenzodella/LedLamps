// Della Matera Lorenzo 5E

package com.example.ledlamps.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ledlamps.R;
import com.example.ledlamps.main.CustomSwipeRefreshLayout;
import com.example.ledlamps.main.SynchronizableFragment;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends SynchronizableFragment implements JsonHttpRequest.OnSyncReadyListener{

    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private AppCompatSeekBar brightness_bar;
    private Switch power;
    private Switch random;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        power = view.findViewById(R.id.power_switch);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                jsonHttpRequest.setOnSyncReadyListener(HomeFragment.this);
                if(power.isChecked()){
                    jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=on");
                }
                else {
                    jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=off");
                }
            }
        });
        random = view.findViewById(R.id.random_switch);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                jsonHttpRequest.setOnSyncReadyListener(HomeFragment.this);
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/random.php?enabled="+random.isChecked());
            }
        });

        //showToast = false;
        //sync();

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });

        brightness_bar = view.findViewById(R.id.brightness_bar);
        brightness_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                swipeRefreshLayout.disableInterceptTouchEvent(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                swipeRefreshLayout.disableInterceptTouchEvent(false);
            }
        });

        Button brightness_conf = view.findViewById(R.id.brightness_confirm);
        brightness_conf.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                   jsonHttpRequest.setOnSyncReadyListener(HomeFragment.this);
                   jsonHttpRequest.execute(LedLampsUtils.getHost()+"/set_brightness_hash.php?brightness="+brightness_bar.getProgress());
               }
           }
        );

    }

    @Override
    public void sync(){
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
        jsonHttpRequest.setOnSyncReadyListener(HomeFragment.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/sync.php");
    }

    @Override
    public void onError(String error) {
        showToast = false;
        sync();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        try {
            power.setChecked(jobj.getInt("opMode") != 42);
            random.setChecked(jobj.getInt("random") == 1);
            brightness_bar.setProgress(jobj.getInt("brightness"));

            if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"") && jobj.getInt("connected") == 0 && showToast){
                Toast.makeText(getContext(), "Your lamp is not connected!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
        showToast = true;
    }
}