package com.bily.samuel.spseinstructor.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.R;
import com.bily.samuel.spseinstructor.lib.database.Question;

import java.util.ArrayList;

/**
 * Created by samuel on 7.3.2016.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {

    ArrayList<Question> questions = new ArrayList<>();

    public QuestionAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Question question){
        questions.add(question);
    }

    public void delete(){
        questions.clear();
    }

    public Question getItem(int i){
        return questions.get(i);
    }

    public int getCount(){
        return questions.size();
    }

    public View getView(int position, View row, ViewGroup parent){
        if(row == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_question, parent, false);
        }

        Question question = getItem(position);

        TextView name = (TextView)row.findViewById(R.id.questionName);
        TextView stat = (TextView)row.findViewById(R.id.answer);

        name.setText(question.getName());
        stat.setText(question.getStat());

        if(question.getRight()>0){
            stat.setTextColor(row.getResources().getColor(R.color.colorGreen));
        }else{
            stat.setTextColor(row.getResources().getColor(R.color.colorRed));
        }


        return row;
    }
}
