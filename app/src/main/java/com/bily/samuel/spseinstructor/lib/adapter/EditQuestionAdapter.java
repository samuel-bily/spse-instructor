package com.bily.samuel.spseinstructor.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.R;
import com.bily.samuel.spseinstructor.lib.database.Question;

import java.util.ArrayList;

/**
 * Created by samuel on 15.4.2016.
 */
public class EditQuestionAdapter extends ArrayAdapter<Question> {

    ArrayList<Question> questions = new ArrayList<>();

    public EditQuestionAdapter(Context context, int resource) {
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
            row = inflater.inflate(R.layout.row_question, parent, false);
        }

        Question q = getItem(position);

        TextView id = (TextView)row.findViewById(R.id.questionId);
        TextView name = (TextView)row.findViewById(R.id.questionName);

        id.setText("" + q.getId());
        name.setText(q.getName());


        return row;
    }

}