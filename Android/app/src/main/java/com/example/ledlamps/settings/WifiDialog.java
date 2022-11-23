// Della Matera Lorenzo 5E

package com.example.ledlamps.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.ledlamps.R;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;

public class WifiDialog extends AppCompatDialogFragment {
    private EditText editSsid;
    private EditText editPass;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_wifidialog, null);

        builder.setView(view)
                .setTitle("WiFi")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                        String ssid_now = settingsActivity.getSsid();
                        if(!editSsid.getText().toString().trim().isEmpty()) {
                            if (editSsid.getText().toString().equals(ssid_now))
                                Toast.makeText(getActivity(), "Lamps are already connected to " + ssid_now, Toast.LENGTH_LONG).show();
                            else {
                                JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(settingsActivity.getApplicationContext(), false);
                                jsonHttpRequest.setOnSyncReadyListener(settingsActivity);
                                jsonHttpRequest.execute(LedLampsUtils.getHost() + "/wifi?"
                                        + "ssid=" + editSsid.getText().toString()
                                        + "&pass=" + editPass.getText().toString());
                            }
                        }
                    }
                });

        editSsid = view.findViewById(R.id.edit_ssid);
        editPass = view.findViewById(R.id.edit_password);

        return builder.create();
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
}
