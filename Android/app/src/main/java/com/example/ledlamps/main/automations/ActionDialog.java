// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.ledlamps.R;
import com.example.ledlamps.utils.LedLampsUtils;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class ActionDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private Spinner modeSpinner;
    private MySpinnerAdapter spinnerAdapter;
    private Action[] actions;
    private ImageButton custom;
    private ImageButton fade1;
    private ImageButton fade2;
    private NumberPicker minutes, seconds;
    private OnActionCreatedListened onActionCreatedListener;

    public ActionDialog(Action[] actions, OnActionCreatedListened onActionCreatedListener){
        this.actions = actions;
        this.onActionCreatedListener = onActionCreatedListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_actiondialog, null);

        builder.setView(view)
                .setTitle("Create an action")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Action action = (Action) modeSpinner.getSelectedItem();
                        action.setTime(seconds.getValue() + (60*minutes.getValue()));
                        onActionCreatedListener.onActionCreated(action);
                    }
                });

        minutes = view.findViewById(R.id.minutePicker);
        minutes.setMaxValue(10);
        minutes.setMinValue(0);
        seconds = view.findViewById(R.id.secondPicker);
        seconds.setMaxValue(60);
        seconds.setMinValue(5);

        custom = view.findViewById(R.id.custom_picker);
        custom.setOnClickListener(this);
        fade1 = view.findViewById(R.id.fade1_picker);
        fade1.setOnClickListener(this);
        fade2 = view.findViewById(R.id.fade2_picker);
        fade2.setOnClickListener(this);

        modeSpinner = view.findViewById(R.id.mode_spinner);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Action action = (Action) parent.getItemAtPosition(position);
                if(action.getModeId() == 44 || action.getModeId() == 45) {
                    custom.setVisibility(View.VISIBLE);
                    fade1.setVisibility(View.GONE);
                    fade2.setVisibility(View.GONE);

                    setColors(Color.parseColor(LedLampsUtils.getCustom()), null, null);
                }
                else if(action.getModeId() >= 46 && action.getModeId() <= 49){
                    custom.setVisibility(View.GONE);
                    fade1.setVisibility(View.VISIBLE);
                    fade2.setVisibility(View.VISIBLE);

                    setColors(null, Color.parseColor(LedLampsUtils.getFade1()), Color.parseColor(LedLampsUtils.getFade2()));
                }
                else {
                    custom.setVisibility(View.GONE);
                    fade1.setVisibility(View.GONE);
                    fade2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerAdapter = new MySpinnerAdapter(getActivity(), R.layout.spinner_item, actions);
        modeSpinner.setAdapter(spinnerAdapter);

        return builder.create();
    }

    private void setColors(Integer custombg, Integer fade1bg, Integer fade2bg){
        Action action = (Action) modeSpinner.getSelectedItem();
        if(custombg != null) {
            int customfg = LedLampsUtils.brightness(custombg) < 130 ? Color.WHITE : Color.BLACK;
            custom.setBackgroundTintList(ColorStateList.valueOf(custombg));
            custom.setImageTintList(ColorStateList.valueOf(customfg));
            LedLampsUtils.setCustom("#" + Integer.toHexString(custombg).substring(2));
            action.setCustom(LedLampsUtils.getCustom());
            action.setFade1(null);
            action.setFade2(null);
        }
        if(fade1bg != null) {
            int fade1fg = LedLampsUtils.brightness(fade1bg) < 130 ? Color.WHITE : Color.BLACK;
            fade1.setBackgroundTintList(ColorStateList.valueOf(fade1bg));
            fade1.setImageTintList(ColorStateList.valueOf(fade1fg));
            LedLampsUtils.setFade1("#" + Integer.toHexString(fade1bg).substring(2));
            action.setCustom(null);
            action.setFade1(LedLampsUtils.getFade1());
            action.setFade2(null);
        }
        if(fade2bg != null) {
            int fade2fg = LedLampsUtils.brightness(fade2bg) < 130 ? Color.WHITE : Color.BLACK;
            fade2.setBackgroundTintList(ColorStateList.valueOf(fade2bg));
            fade2.setImageTintList(ColorStateList.valueOf(fade2fg));
            LedLampsUtils.setFade2("#" + Integer.toHexString(fade2bg).substring(2));
            action.setCustom(null);
            action.setFade1(null);
            action.setFade2(LedLampsUtils.getFade2());
        }
    }

    public void onClick(View v){
        ImageButton button = (ImageButton)v;
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle("Choose your color")
                .setPositiveButton("select",
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                if(button.equals(custom))
                                    setColors(envelope.getColor(), null, null);
                                else if(button.equals(fade1))
                                    setColors(null, envelope.getColor(), null);
                                else if(button.equals(fade2))
                                    setColors(null, null, envelope.getColor());
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
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setBackgroundColor(Color.TRANSPARENT);
        positive.setTextColor(getResources().getColor(R.color.blue_500, null));
        negative.setBackgroundColor(Color.TRANSPARENT);
        negative.setTextColor(getResources().getColor(R.color.blue_500, null));
    }

    public interface OnActionCreatedListened {
        void onActionCreated(Action action);
    }
}
