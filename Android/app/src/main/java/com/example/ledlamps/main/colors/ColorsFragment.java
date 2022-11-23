// Della Matera Lorenzo 5E

package com.example.ledlamps.main.colors;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ledlamps.R;
import com.example.ledlamps.main.SynchronizableFragment;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ColorsFragment extends SynchronizableFragment implements View.OnClickListener, JsonHttpRequest.OnSyncReadyListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Button red, green, blue, custom, fade1, fade2;
    private RadioGroup singleGroup, doubleGroup;
    private RadioButton modes[];

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_colors, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });

        red = view.findViewById(R.id.red);
        red.setOnClickListener(this);
        blue = view.findViewById(R.id.blue);
        blue.setOnClickListener(this);
        green = view.findViewById(R.id.green);
        green.setOnClickListener(this);
        custom = view.findViewById(R.id.custom);
        custom.setOnClickListener(this);

        fade1 = view.findViewById(R.id.fade1);
        fade1.setOnClickListener(this);

        fade2 = view.findViewById(R.id.fade2);
        fade2.setOnClickListener(this);

        singleGroup = view.findViewById(R.id.radioGroup_single);
        doubleGroup = view.findViewById(R.id.radioGroup_double);
        modes = new RadioButton[6];
        modes[0] = view.findViewById(R.id.custom_color);
        modes[1] = view.findViewById(R.id.custom_fade);
        modes[2] = view.findViewById(R.id.fade);
        modes[3] = view.findViewById(R.id.doubleColor);
        modes[4] = view.findViewById(R.id.fix);
        modes[5] = view.findViewById(R.id.swap);
        for (RadioButton b : modes) {
            b.setOnClickListener(this);
        }

        //showToast = false;
        //sync();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.red:
                setColor("red");
                break;
            case R.id.blue:
                setColor("blue");
                break;
            case R.id.green:
                setColor("green");
                break;
            case R.id.fade:
                setColor("fade");
                break;
            case R.id.doubleColor:
                setColor("double");
                break;
            case R.id.fix:
                setColor("fix");
                break;
            case R.id.swap:
                setColor("swapping");
                break;
            case R.id.custom_color:
                setColor("custom_color");
                break;
            case R.id.custom_fade:
                setColor("custom_fade");
                break;
            case R.id.custom:
                chooseColor(custom);
                break;
            case R.id.fade1:
                chooseColor(fade1);
                break;
            case R.id.fade2:
                chooseColor(fade2);
                break;
        }
    }

    private void setColor(String mode) {
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
        jsonHttpRequest.setOnSyncReadyListener(ColorsFragment.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode="+mode);
    }

    @Override
    public void sync(){
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
        jsonHttpRequest.setOnSyncReadyListener(ColorsFragment.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/sync.php");
    }

    private void chooseColor(final Button button){
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle("Choose your color")
                .setPositiveButton("select",
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                                jsonHttpRequest.setOnSyncReadyListener(ColorsFragment.this);
                                if(button.getId() == R.id.custom){
                                    jsonHttpRequest.execute(LedLampsUtils.getHost()+"/set_color_hash.php?color=%23"+envelope.getHexCode().substring(2));
                                }
                                else if(button.getId() == R.id.fade1){
                                    String hexColor1 = "first=%23" + envelope.getHexCode().substring(2);
                                    String hexColor2 = "second=%23" + Integer.toHexString(fade2.getBackgroundTintList().getDefaultColor()).substring(2);
                                    jsonHttpRequest.execute(LedLampsUtils.getHost()+"/set_color_hash?"+hexColor1+"&"+hexColor2);
                                }
                                else if(button.getId() == R.id.fade2){
                                    String hexColor1 = "first=%23" + Integer.toHexString(fade1.getBackgroundTintList().getDefaultColor()).substring(2);
                                    String hexColor2 = "second=%23" + envelope.getHexCode().substring(2);
                                    jsonHttpRequest.execute(LedLampsUtils.getHost()+"/set_color_hash?"+hexColor1+"&"+hexColor2);
                                }
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(false) // default is true. If false, do not show the AlphaSlideBar.
                .attachBrightnessSlideBar(true);  // default is true. If false, do not show the BrightnessSlideBar.

        ColorPickerView colorPickerView = builder.getColorPickerView();
        BubbleFlag bubbleFlag = new BubbleFlag(getContext());
        bubbleFlag.setFlagMode(FlagMode.FADE);
        colorPickerView.setFlagView(bubbleFlag);
        colorPickerView.setInitialColor(button.getBackgroundTintList().getDefaultColor());
        builder.show();
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
            singleGroup.clearCheck();
            doubleGroup.clearCheck();
            int mode = jobj.getInt("opMode");
            if(mode>=44 && mode <=49){
                modes[mode-44].setChecked(true);
            }

            int customColor = Color.parseColor(jobj.getString("custom"));
            int customText = LedLampsUtils.brightness(customColor) < 130 ? Color.WHITE : Color.BLACK;
            custom.setBackgroundTintList(ColorStateList.valueOf(customColor));
            custom.setTextColor(customText);
            custom.getCompoundDrawables()[2].setColorFilter(customText, PorterDuff.Mode.SRC_ATOP);

            int fade1Color = Color.parseColor(jobj.getString("fade1"));
            int fade1Text = LedLampsUtils.brightness(fade1Color) < 130 ? Color.WHITE : Color.BLACK;
            fade1.setBackgroundTintList(ColorStateList.valueOf(fade1Color));
            fade1.setTextColor(fade1Text);
            fade1.getCompoundDrawables()[2].setColorFilter(fade1Text, PorterDuff.Mode.SRC_ATOP);

            int fade2Color = Color.parseColor(jobj.getString("fade2"));
            int fade2Text = LedLampsUtils.brightness(fade2Color) < 130 ? Color.WHITE : Color.BLACK;
            fade2.setBackgroundTintList(ColorStateList.valueOf(fade2Color));
            fade2.setTextColor(fade2Text);
            fade2.getCompoundDrawables()[2].setColorFilter(fade2Text, PorterDuff.Mode.SRC_ATOP);

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