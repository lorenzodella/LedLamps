// Della Matera Lorenzo 5E

package com.example.ledlamps.main.modes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ledlamps.R;
import com.example.ledlamps.main.SynchronizableFragment;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ModesFragment extends SynchronizableFragment implements View.OnClickListener, JsonHttpRequest.OnSyncReadyListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ModeButton modeButtons[];

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modes, container, false);
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

        modeButtons = new ModeButton[16];
        modeButtons[0] = new ModeButton(view.findViewById(R.id.mode1), view.findViewById(R.id.check1));
        modeButtons[1] = new ModeButton(view.findViewById(R.id.mode2), view.findViewById(R.id.check2));
        modeButtons[2] = new ModeButton(view.findViewById(R.id.mode3), view.findViewById(R.id.check3));
        modeButtons[3] = new ModeButton(view.findViewById(R.id.mode4), view.findViewById(R.id.check4));
        modeButtons[4] = new ModeButton(view.findViewById(R.id.mode5), view.findViewById(R.id.check5));
        modeButtons[5] = new ModeButton(view.findViewById(R.id.mode6), view.findViewById(R.id.check6));
        modeButtons[6] = new ModeButton(view.findViewById(R.id.mode7), view.findViewById(R.id.check7));
        modeButtons[7] = new ModeButton(view.findViewById(R.id.mode8), view.findViewById(R.id.check8));
        modeButtons[8] = new ModeButton(view.findViewById(R.id.mode9), view.findViewById(R.id.check9));
        modeButtons[9] = new ModeButton(view.findViewById(R.id.mode10), view.findViewById(R.id.check10));
        modeButtons[10] = new ModeButton(view.findViewById(R.id.mode11), view.findViewById(R.id.check11));
        modeButtons[11] = new ModeButton(view.findViewById(R.id.mode12), view.findViewById(R.id.check12));
        modeButtons[12] = new ModeButton(view.findViewById(R.id.mode13), view.findViewById(R.id.check13));
        modeButtons[13] = new ModeButton(view.findViewById(R.id.mode14), view.findViewById(R.id.check14));
        modeButtons[14] = new ModeButton(view.findViewById(R.id.mode15), view.findViewById(R.id.check15));
        modeButtons[15] = new ModeButton(view.findViewById(R.id.mode16), view.findViewById(R.id.check16));
        for(ModeButton m : modeButtons){
            m.getButton().setOnClickListener(this);
        }

        //showToast = false;
        //sync();
    }

    @Override
    public void onClick(View v) {
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
        jsonHttpRequest.setOnSyncReadyListener(ModesFragment.this);
        switch(v.getId()){
            case R.id.mode1:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=sound_reactive");
                break;
            case R.id.mode2:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=chill_fade");
                break;
            case R.id.mode3:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=color_wipe");
                break;
            case R.id.mode4:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=color_flash");
                break;
            case R.id.mode5:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=rainbow_cycle");
                break;
            case R.id.mode6:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=rainbow_fade");
                break;
            case R.id.mode7:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=rainbow_chase");
                break;
            case R.id.mode8:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=strobe");
                break;
            case R.id.mode9:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=fire");
                break;
            case R.id.mode10:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=balls");
                break;
            case R.id.mode11:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=fill_random");
                break;
            case R.id.mode12:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=sound_colors");
                break;
            case R.id.mode13:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=twinkle");
                break;
            case R.id.mode14:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=sparkle");
                break;
            case R.id.mode15:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=strobe_shot");
                break;
            case R.id.mode16:
                jsonHttpRequest.execute(LedLampsUtils.getHost()+"/mode.php?opMode=strobe_fade");
                break;
        }
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
            for(ModeButton m : modeButtons){
                m.getImageView().setVisibility(View.GONE);
            }
            int mode = jobj.getInt("opMode");
            if(mode<=16){
                modeButtons[mode-1].getImageView().setVisibility(View.VISIBLE);
            }

            if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"") && jobj.getInt("connected") == 0 && showToast){
                Toast.makeText(getContext(), "Your lamp is not connected!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
        showToast = true;
    }

    @Override
    public void sync(){
        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
        jsonHttpRequest.setOnSyncReadyListener(ModesFragment.this);
        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/sync.php");
    }


    class ModeButton {
        private Button button;
        private ImageView imageView;

        public ModeButton(Button button, ImageView imageView) {
            this.button = button;
            this.imageView = imageView;
        }

        public Button getButton() {
            return button;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}