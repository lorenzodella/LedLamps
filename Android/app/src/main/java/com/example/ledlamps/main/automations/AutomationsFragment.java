// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ledlamps.R;
import com.example.ledlamps.main.CustomSwipeRefreshLayout;
import com.example.ledlamps.main.SynchronizableFragment;
import com.example.ledlamps.user.User;
import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AutomationsFragment extends SynchronizableFragment implements
        JsonHttpRequest.OnSyncReadyListener,
        RecyclerViewAdapterAutomations.OnActionChangedListener,
        RecyclerViewAdapterAutomations.OnAutomationClickListener,
        View.OnClickListener {

    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterAutomations adapterAutomations;
    private RecyclerViewAdapterActions adapterActions;
    private FloatingActionButton add;
    private TextView no_automation;
    private Action[] mode_list_for_actions;
    private HashMap<Integer, Automation> automations;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_automations, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        no_automation = view.findViewById(R.id.no_automation);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });

        add = view.findViewById(R.id.add);
        add.setOnClickListener(this);

        automations = new HashMap<>();
        recyclerView = view.findViewById(R.id.recyclerView_automations);
        setRecyclerView();

        //showToast = false;
        //sync();
        getModes();

    }

    private void setRecyclerView() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapterAutomations = new RecyclerViewAdapterAutomations(new ArrayList<>(automations.values()), recyclerView, getContext(), swipeRefreshLayout);
        adapterAutomations.setOnActionChangedListener(this);
        adapterAutomations.setOnAutomationClickListener(this);
        recyclerView.setAdapter(adapterAutomations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void automate(int idAutomation, String operation){
        showToast = true;
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/automator.php?operation="+operation+"&idAutomation="+idAutomation);
        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sync(){
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/select.php");
        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    private void getModes(){
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/getModes.php");
        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        try {
            if(jobj.has("automations")) {
                parseAutomations(jobj.getJSONArray("automations"));
            }
            else if(jobj.has("actions")){
                parseActions(jobj.getJSONArray("actions"));
            }
            else if(jobj.has("modes")){
                parseModes(jobj.getJSONArray("modes"));
            }
            else{
                for (Automation a : automations.values()) {
                    a.setActive(false);
                }
                if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"") && jobj.getInt("activeAutomation") > 0){
                    automations.get(jobj.getInt("activeAutomation")).setActive(true);
                }
                adapterAutomations.notifyDataSetChanged();
                if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"") && jobj.getInt("connected") == 0 && showToast){
                    Toast.makeText(getContext(), "Your lamp is not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void parseAutomations(JSONArray automations_array) throws JSONException{
        automations = new HashMap<>();

        for (int i = 0; i < automations_array.length(); i++) {
            JSONObject automation = automations_array.getJSONObject(i);
            JSONArray actions_arr = automation.getJSONArray("actions");
            LinkedList<Action> actions = new LinkedList<Action>();
            for (int j = 0; j < actions_arr.length(); j++) {
                JSONObject action = actions_arr.getJSONObject(j);
                actions.add(new Action(
                        action.getInt("idAutomation"),
                        action.getInt("position"),
                        action.getInt("modeId"),
                        action.getString("modeName"),
                        action.getString("custom"),
                        action.getString("fade1"),
                        action.getString("fade2"),
                        action.getInt("time")
                ));
            }
            automations.put(automation.getInt("idAutomation"), new Automation(
                    automation.getInt("idAutomation"),
                    automation.getInt("idUser"),
                    automation.getString("name"),
                    automation.getString("username"),
                    automation.getInt("isActive")==1,
                    actions
            ));
        }

        adapterAutomations.setAutomations(new ArrayList<>(automations.values()), true);
        if(automations.size()>0)
            no_automation.setVisibility(View.GONE);
        else
            no_automation.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        System.out.println(automations.toString());
    }
    private void parseActions(JSONArray actions_arr) throws JSONException{
        LinkedList<Action> actions = new LinkedList<Action>();
        for (int j = 0; j < actions_arr.length(); j++) {
            JSONObject action = actions_arr.getJSONObject(j);
            actions.add(new Action(
                    action.getInt("idAutomation"),
                    action.getInt("position"),
                    action.getInt("modeId"),
                    action.getString("modeName"),
                    action.getString("custom"),
                    action.getString("fade1"),
                    action.getString("fade2"),
                    action.getInt("time")
            ));
            automations.get(action.getInt("idAutomation")).setActions(actions);
            adapterActions.setActions(actions, false);
        }
        automate(actions.get(0).getIdAutomation(), "update");
    }
    private void parseModes(JSONArray modes_array) throws JSONException{
        mode_list_for_actions = new Action[modes_array.length()];
        for (int i = 0; i < modes_array.length(); i++) {
            JSONObject mode = modes_array.getJSONObject(i);
            mode_list_for_actions[i] = new Action(
                    0,
                    1,
                    mode.getInt("modeId"),
                    mode.getString("modeName"),
                    null,
                    null,
                    null,
                    0
            );
        }
    }

    @Override
    public void onError(String error) {
        if(error.contains("creation failed")){
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMove(Action action, RecyclerViewAdapterActions adapter) {
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            adapterActions = adapter;
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/moveAction.php?" +
                                    "idAutomation="+action.getIdAutomation() +
                                    "&from="+action.getPosition() +
                                    "&to="+action.getMovePosition());
        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDelete(Action action, RecyclerViewAdapterActions adapter) {
        new AlertDialog.Builder(getContext())
                .setMessage("Do you want to remove action "+action.getPosition()+"?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(action.getPosition()-1);
                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
                            adapterActions = adapter;
                            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
                            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/deleteAction.php?" +
                                    "idAutomation="+action.getIdAutomation() +
                                    "&pos="+action.getPosition());
                        }
                        else if(showToast){
                            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    public void onAutomationClick(Automation automation) {
        if(automation.getActions().size()>0) {
            if (automation.isActive())
                automate(automation.getIdAutomation(), "stop");
            else
                automate(automation.getIdAutomation(), "start");
        }
        else {
            Toast.makeText(getContext(), "This automation is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAutomationLongClick(Automation automation) {
        new AlertDialog.Builder(getContext())
                .setMessage("Do you want to delete automation "+automation.getName()+"?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
                            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
                            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/delete.php?" +
                                    "idAutomation="+automation.getIdAutomation());
                        }
                        else if(showToast){
                            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    public void onAddActionClick(Automation automation, RecyclerViewAdapterActions adapter) {
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            adapterActions = adapter;
            ActionDialog actionDialog = new ActionDialog(mode_list_for_actions, new ActionDialog.OnActionCreatedListened() {
                @Override
                public void onActionCreated(Action action) {
                    try {
                        action.setIdAutomation(automation.getIdAutomation());
                        System.out.println(action);
                        String custom = action.getCustom() != null ? action.getCustom() : "null";
                        String fade1 = action.getFade1() != null ? action.getFade1() : "null";
                        String fade2 = action.getFade2() != null ? action.getFade2() : "null";
                        JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
                        jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
                        jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/addAction.php?" +
                                "idAutomation=" + action.getIdAutomation() +
                                "&modeId=" + action.getModeId() +
                                "&position=" + action.getPosition() +
                                "&custom=" + URLEncoder.encode(custom, "utf-8") +
                                "&fade1=" + URLEncoder.encode(fade1, "utf-8") +
                                "&fade2=" + URLEncoder.encode(fade2, "utf-8") +
                                "&time=" + action.getTime());

                    } catch (UnsupportedEncodingException e){}
                }
            });
            actionDialog.show(getActivity().getSupportFragmentManager(), "mode_dialog");
        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            EditText input = new EditText(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(50, 0, 50, 0);
            input.setLayoutParams(params);
            input.setHint("name");
            input.setLines(1);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.addView(input);

            new AlertDialog.Builder(getContext())
                    .setTitle("Create a new automation")
                    .setMessage("Choose a name (different from other automations)")
                    .setView(linearLayout)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!input.getText().toString().trim().isEmpty()) {
                                String automation_name = input.getText().toString();
                                ActionDialog actionDialog = new ActionDialog(mode_list_for_actions, new ActionDialog.OnActionCreatedListened() {
                                    @Override
                                    public void onActionCreated(Action action) {
                                        createAutomation(automation_name, action);
                                    }
                                });
                                actionDialog.show(getActivity().getSupportFragmentManager(), "mode_dialog");
                            }
                            else {
                                Toast.makeText(getContext(), "Name can't be blanck", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();

        }
        else if(showToast){
            Toast.makeText(getContext(), "You must be connected to cloud to use your automations", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAutomation(String automation_name, Action action){
        try {
            System.out.println(action);
            String custom = action.getCustom() != null ? action.getCustom() : "null";
            String fade1 = action.getFade1() != null ? action.getFade1() : "null";
            String fade2 = action.getFade2() != null ? action.getFade2() : "null";
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(getContext(), showToast);
            jsonHttpRequest.setOnSyncReadyListener(AutomationsFragment.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/automations/create.php?" +
                    "idUser=" + User.getId() +
                    "&name=" + automation_name +
                    "&modeId=" + action.getModeId() +
                    "&custom=" + URLEncoder.encode(custom, "utf-8") +
                    "&fade1=" + URLEncoder.encode(fade1, "utf-8") +
                    "&fade2=" + URLEncoder.encode(fade2, "utf-8") +
                    "&time=" + action.getTime());

        } catch (UnsupportedEncodingException e){}
    }
}