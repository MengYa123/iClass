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

public class PageAdapter extends ArrayAdapter<Page> {
    private int resourseId;

    public PageAdapter(@NonNull Context context, int resource, @NonNull List<Page> objects) {
        super(context, resource, objects);
        resourseId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Page page = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourseId,null);
        TextView pagenum = view.findViewById(R.id.pagenum);
        TextView num = view.findViewById(R.id.num);
        pagenum.setText(""+page.getPagenum());
        num.setText(""+page.getNum());
        return view;
    }
}
