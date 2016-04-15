package com.bily.samuel.spseinstructor.lib.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.R;
import com.bily.samuel.spseinstructor.lib.database.Test;

import java.util.ArrayList;

/**
 * Created by samuel on 15.4.2016.
 */
public class EditTestAdapter extends ArrayAdapter<Test> {

    ArrayList<Test> tests = new ArrayList<>();

    public EditTestAdapter(Context context, int resource) {
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
            row = inflater.inflate(R.layout.row_test, parent, false);
        }

        Test test = getItem(position);

        TextView id = (TextView)row.findViewById(R.id.testId);
        TextView name = (TextView)row.findViewById(R.id.testName);
        TextView info = (TextView)row.findViewById(R.id.textViewInfo);

        id.setText("" + test.getId_t());
        name.setText(test.getName());
        if(test.getActive()<1){
            name.setTextColor(getContext().getResources().getColor(R.color.colorGrey700));
            name.setTypeface(name.getTypeface(), Typeface.ITALIC);
            info.setText("Koncept");
        }else{
            info.setText("Zverejnené");
        }

        return row;
    }

}