package com.bily.samuel.spseinstructor.lib.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.R;
import com.bily.samuel.spseinstructor.lib.database.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 24.2.2016.
 */
public class AddQuestionAdapter extends ArrayAdapter {

    private List<Question> list = new ArrayList<>();

    public AddQuestionAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Question question){
        list.add(question);
    }

    public Question getItem(int index){
        return list.get(index);
    }

    public int getCount(){
        return list.size();
    }

    public View getView(final int position, View row, ViewGroup parent){
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.cardview_add_question, parent, false);
        }

        try{
            TextView number = (TextView)row.findViewById(R.id.textNumber);
            number.setText("" + list.get(position).getId());

            RadioGroup group = (RadioGroup)row.findViewById(R.id.radioGroup);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //Integer pos = (Integer) getPosition(this);
                    Question element = list.get(position);
                    switch (checkedId) { // set the Model to hold the
                        // answer the user picked
                        case R.id.radioButton:
                            element.setCurrent(Question.ONE);
                            break;
                        case R.id.radioButton2:
                            element.setCurrent(Question.TWO);
                            break;
                        case R.id.radioButton3:
                            element.setCurrent(Question.THREE);
                            break;
                        case R.id.radioButton4:
                            element.setCurrent(Question.FOUR);
                            break;
                        default:
                            element.setCurrent(Question.NONE);
                    }
                    Log.e("Selected",""+element.getCurrent());
                }
            });



            if (list.get(position).getCurrent() == Question.NONE) {
                group.clearCheck();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return row;
    }

}
