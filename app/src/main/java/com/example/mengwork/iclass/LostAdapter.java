package com.example.mengwork.iclass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;

public class LostAdapter extends ArrayAdapter<LostItem> {
    private int resouceId;
    public LostAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<LostItem> objects) {
        super(context, textViewResourceId, objects);
        resouceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LostItem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resouceId,null);
        TextView lost_name = view.findViewById(R.id.lost_name);
        TextView name = view.findViewById(R.id.pick_name);
        TextView addr = view.findViewById(R.id.lost_addr);
        TextView contract = view.findViewById(R.id.lost_contract);
        lost_name.setText(item.getLost_name());
        name.setText(item.getName());
        contract.setText(item.getContract());
        String addr_current = item.getAddr();
        addr.setText(addr_current);
        return view;
    }
}
