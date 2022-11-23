// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledlamps.R;
import com.example.ledlamps.main.CustomSwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class RecyclerViewAdapterAutomations extends RecyclerView.Adapter<RecyclerViewAdapterAutomations.MyViewHolder>{
    private ArrayList<Automation> automations;
    private RecyclerView recyclerView;
    private OnActionChangedListener onActionChangedListener;
    private OnAutomationClickListener onAutomationClickListener;
    private Context context;
    private CustomSwipeRefreshLayout swipeRefreshLayout;

    public RecyclerViewAdapterAutomations(ArrayList<Automation> automations, RecyclerView recyclerView, Context context, CustomSwipeRefreshLayout swipeRefreshLayout){
        this.automations = automations;
        this.recyclerView = recyclerView;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView name;
        private TextView duration;
        private TextView username;
        private ImageButton arrow;
        private FloatingActionButton add;
        private RelativeLayout relativeLayout;
        private RecyclerView recyclerView_actions;
        private RecyclerViewAdapterActions adapter;

        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(final View view){
            super(view);
            add = view.findViewById(R.id.add);
            add.setOnClickListener(this);
            relativeLayout = view.findViewById(R.id.layout);
            name = view.findViewById(R.id.name);
            duration = view.findViewById(R.id.duration);
            username = view.findViewById(R.id.username);
            arrow = view.findViewById(R.id.arrow);
            recyclerView_actions = view.findViewById(R.id.recyclerView_actions);
            recyclerView_actions.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_MOVE:
                            swipeRefreshLayout.disableInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            swipeRefreshLayout.disableInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView_actions);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(add)){
                if (onAutomationClickListener != null) onAutomationClickListener.onAddActionClick(automations.get(getAdapterPosition()), adapter);
            }
            else {
                if (onAutomationClickListener != null) onAutomationClickListener.onAutomationClick(automations.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onAutomationClickListener != null) onAutomationClickListener.onAutomationLongClick(automations.get(getAdapterPosition()));
            return false;
        }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            private boolean moved = false;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                LinkedList<Action> actions = automations.get(getAdapterPosition()).getActions();
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(actions, fromPosition, toPosition);
                actions.get(toPosition).setMovePosition(toPosition+1);
                adapter.notifyItemMoved(fromPosition, toPosition);
                moved = true;
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Action action = automations.get(getAdapterPosition()).getActions().get(viewHolder.getAdapterPosition());
                if (onActionChangedListener != null) onActionChangedListener.onDelete(action, adapter);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if(moved) {
                    Action action = automations.get(getAdapterPosition()).getActions().get(viewHolder.getAdapterPosition());
                    if (action.getPosition() != action.getMovePosition()) {
                        if (onActionChangedListener != null)
                            onActionChangedListener.onMove(action, adapter);
                    }
                    moved = false;
                }
            }

            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(Color.RED)
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

    }

    @NonNull
    @Override
    public RecyclerViewAdapterAutomations.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_automation, parent , false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterAutomations.MyViewHolder holder, int position) {
        Automation a = automations.get(position);

        if(a.isActive()){
            holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_list));
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.relativeLayout.getBackground();
            animationDrawable.setEnterFadeDuration(1000);
            animationDrawable.setExitFadeDuration(2000);
            animationDrawable.start();
        }
        else {
            holder.relativeLayout.setBackground(null);
        }

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.setExpanded(!a.isExpanded());
                if(a.isExpanded()){
                    holder.arrow.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_open));
                }
                else {
                    holder.arrow.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_close));
                }
                notifyItemChanged(position);
            }
        });
        holder.name.setText(a.getName());
        holder.duration.setText("Duration: "+a.getDuration()+"sec");
        holder.username.setText(a.getUsername());

        holder.adapter = new RecyclerViewAdapterActions(a.getActions(), holder.recyclerView_actions);
        holder.recyclerView_actions.setAdapter(holder.adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.recyclerView_actions.setLayoutManager(layoutManager);
        holder.recyclerView_actions.setItemAnimator(new DefaultItemAnimator());

        holder.recyclerView_actions.setVisibility(a.isExpanded() ? View.VISIBLE : View.GONE);
        holder.add.setVisibility(a.isExpanded() ? View.VISIBLE : View.GONE);
        holder.arrow.setRotation(a.isExpanded() ? 180 : 0);

        if(holder.recyclerView_actions.getItemDecorationCount()==1) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            holder.recyclerView_actions.addItemDecoration(dividerItemDecoration);
        }
    }

    @Override
    public int getItemCount() {
        return automations.size();
    }

    public void deleteItem(int position){
        automations.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, automations.size());
    }

    public void addItem(Automation a){
        automations.add(a);
        notifyDataSetChanged();
    }

    public void setAutomations(ArrayList<Automation> automations, boolean animation){
        this.automations = automations;
        notifyDataSetChanged();
        if(animation)
            recyclerView.scheduleLayoutAnimation();
    }

    public interface OnActionChangedListener {
        void onMove(Action action, RecyclerViewAdapterActions adapter);
        void onDelete(Action action, RecyclerViewAdapterActions adapter);
    }
    public void setOnActionChangedListener(OnActionChangedListener onActionChangedListener){
        this.onActionChangedListener = onActionChangedListener;
    }

    public interface OnAutomationClickListener {
        void onAutomationClick(Automation automation);
        void onAutomationLongClick(Automation automation);
        void onAddActionClick(Automation automation, RecyclerViewAdapterActions adapter);
    }
    public void setOnAutomationClickListener(OnAutomationClickListener onAutomationClickListener) {
        this.onAutomationClickListener = onAutomationClickListener;
    }
}
