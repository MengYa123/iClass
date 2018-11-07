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

public class MesAdapter extends ArrayAdapter<Mes> {
    private int resouceId;
    public MesAdapter(@NonNull Context context, int resource, @NonNull List<Mes> objects) {
        super(context, resource, objects);
        resouceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Mes mes = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resouceId,null);
        TextView from_id = view.findViewById(R.id.from_id);
        TextView mess = view.findViewById(R.id.mes);
        from_id.setText(mes.getFrom_id());
        mess.setText(mes.getMes());
        return view;
    }
}
