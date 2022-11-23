// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledlamps.R;

import java.util.LinkedList;

public class RecyclerViewAdapterActions extends RecyclerView.Adapter<RecyclerViewAdapterActions.MyViewHolder>{
    private LinkedList<Action> actions;
    private RecyclerView recyclerView;

    public RecyclerViewAdapterActions(LinkedList<Action> actions, RecyclerView recyclerView){
        this.actions = actions;
        this.recyclerView = recyclerView;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView position;
        private TextView modeName;
        private View col1;
        private View col2;
        private TextView time;

        public MyViewHolder(final View view){
            super(view);
            position = view.findViewById(R.id.position);
            modeName = view.findViewById(R.id.mode_name);
            col1 = view.findViewById(R.id.fade1);
            col2 = view.findViewById(R.id.col2);
            time = view.findViewById(R.id.time);
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapterActions.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_action, parent , false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterActions.MyViewHolder holder, int position) {
        Action a = actions.get(position);
        holder.position.setText(a.getPosition()+")");
        holder.modeName.setText(a.getModeName());
        if(a.getModeId() == 44 || a.getModeId() == 45) {
            holder.col1.setVisibility(View.GONE);
            holder.col2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(a.getCustom())));
        }
        else if(a.getModeId() >= 46 && a.getModeId() <= 49){
            holder.col1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(a.getFade1())));
            holder.col2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(a.getFade2())));
        }
        else {
            holder.col1.setVisibility(View.GONE);
            holder.col2.setVisibility(View.GONE);
        }
        holder.time.setText(a.getTime()+"sec");
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void deleteItem(int position){
        actions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, actions.size());
    }

    public void addItem(Action a){
        actions.add(a);
        notifyDataSetChanged();
    }

    public void setActions(LinkedList<Action> actions, boolean animation){
        this.actions = actions;
        notifyDataSetChanged();
        if(animation)
            recyclerView.scheduleLayoutAnimation();
    }

}
