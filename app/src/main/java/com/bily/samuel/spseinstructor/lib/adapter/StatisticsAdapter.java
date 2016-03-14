package com.bily.samuel.spseinstructor.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.R;
import com.bily.samuel.spseinstructor.lib.database.Test;


import java.util.ArrayList;

/**
 * Created by samuel on 6.3.2016.
 */
public class StatisticsAdapter extends ArrayAdapter<Test>{

    ArrayList<Test> tests = new ArrayList<>();

    public StatisticsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Test test){
        tests.add(test);
    }

    public void delete(){
        tests.clear();
    }

    public Test getItem(int i){
        return tests.get(i);
    }

    public int getCount(){
        return tests.size();
    }

    public View getView(int position, View row, ViewGroup parent){
        if(row == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_stats, parent, false);
        }

        Test test = getItem(position);

        TextView id = (TextView)row.findViewById(R.id.statId);
        TextView name = (TextView)row.findViewById(R.id.statsTest);
        TextView stat = (TextView)row.findViewById(R.id.statsTestP);

        id.setText("" + test.getId_t());
        name.setText(test.getName());
        if(test.getStat() >= 0) {
            stat.setText(test.getStat() + "%");
        }else{
            stat.setVisibility(View.GONE);
        }

        return row;
    }

}
