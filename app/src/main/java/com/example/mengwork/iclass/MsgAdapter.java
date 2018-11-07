package com.example.mengwork.iclass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Msg> data;
    private static final int TYPE_RECEIVED=0;
    private static final int TYPE_SENT=1;

    public MsgAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Msg> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        return data.get(position).getNumber();
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case TYPE_RECEIVED:
                View view = LayoutInflater.from(context).inflate(R.layout.item_a,parent,false);
                holder = new OneViewHolder(view);
                break;
            case TYPE_SENT:
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_b,parent,false);
                holder = new TwoViewHolder(view1);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType){
            case TYPE_RECEIVED:
                OneViewHolder oneViewHolder = (OneViewHolder) holder;
                oneViewHolder.left_msg.setText(data.get(position).getConnent());
                break;
            case TYPE_SENT:
                TwoViewHolder twoViewHolder = (TwoViewHolder) holder;
                twoViewHolder.right_msg.setText(data.get(position).getConnent());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class OneViewHolder extends RecyclerView.ViewHolder{
        private TextView left_msg;
        public OneViewHolder(View itemView) {
            super(itemView);
            left_msg = (TextView) itemView.findViewById(R.id.left_msg);

        }
    }

    class TwoViewHolder extends RecyclerView.ViewHolder{
        private TextView right_msg;
        public TwoViewHolder(View itemView) {
            super(itemView);
            right_msg = (TextView) itemView.findViewById(R.id.right_msg);

        }
    }

}