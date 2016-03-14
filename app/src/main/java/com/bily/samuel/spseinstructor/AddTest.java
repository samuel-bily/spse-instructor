package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.AddQuestionAdapter;
import com.bily.samuel.spseinstructor.lib.database.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTest extends AppCompatActivity {

    private AddQuestionAdapter questionAdapter;
    private ArrayList<Question> questions;
    private ListView listView;

    private String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        Intent intent = getIntent();
        intent.getStringExtra("name");
        int number = intent.getIntExtra("number",3);

        test = intent.getStringExtra("name");
        setTitle(test);

        questionAdapter = new AddQuestionAdapter(getApplicationContext(),R.layout.cardview_add_question);
        for (int i = 1; i<=number; i++) {
            Question question = new Question();
            question.setId(i);
            questionAdapter.add(question);
        }
        listView = (ListView) findViewById(R.id.listViewAddQuestions);
        listView.setAdapter(questionAdapter);
    }


    public void sendQuestions(View view){
        questions = new ArrayList<>();
        for(int i = 0; i < listView.getAdapter().getCount(); i++){
            View viewQuestion = listView.getChildAt(i);
            EditText questionEdit = (EditText)viewQuestion.findViewById(R.id.questionEdit);
            EditText option0 = (EditText)viewQuestion.findViewById(R.id.optionEdit0);
            EditText option1 = (EditText)viewQuestion.findViewById(R.id.optionEdit1);
            EditText option2 = (EditText)viewQuestion.findViewById(R.id.optionEdit2);
            EditText option3 = (EditText)viewQuestion.findViewById(R.id.optionEdit3);

            String[] options = {option0.getText().toString(),option1.getText().toString(),option2.getText().toString(),option3.getText().toString()};

            int current = questionAdapter.getItem(i).getCurrent();


            Question question = new Question();
            question.setName(questionEdit.getText().toString());
            question.setOptions(options);
            question.setCurrent(current);
            questions.add(question);
        }

        new SendTest().execute();

    }

    public Snackbar prepareSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.clearAnimation();
        view.setLayoutParams(params);
        return snack;
    }

    private class SendTest extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "setTest");
            values.put("name",  test);
            values.put("questions", "{ \"questions\":" + questions.toString() + "}");
            Log.e("Sending",values.toString());
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                int success = jsonObject.getInt("success");
                if(success>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = prepareSnack("Uspešne odoslané.");
                            snackbar.show();
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            finish();
                            startActivity(i);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
