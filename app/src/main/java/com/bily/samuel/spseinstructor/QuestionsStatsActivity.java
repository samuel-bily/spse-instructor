package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.QuestionAdapter;
import com.bily.samuel.spseinstructor.lib.database.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class QuestionsStatsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private int idU;
    private int idT;
    private QuestionAdapter questionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetQuestionStats getQuestionStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_stats);

        Intent i = getIntent();
        idU = i.getIntExtra("id_u",1);
        idT = i.getIntExtra("id_t",1);
        setTitle(i.getStringExtra("name"));

        questionAdapter = new QuestionAdapter(getApplicationContext(),R.layout.fragment_question);
        ListView listView = (ListView)findViewById(R.id.listQuestions);
        listView.setAdapter(questionAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuestions);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getQuestionStats = new GetQuestionStats();
        getQuestionStats.execute();

    }

    @Override
    public void onRefresh() {
        getQuestionStats = new GetQuestionStats();
        getQuestionStats.execute();
    }

    class GetQuestionStats extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("tag","getQuestionStats");
            values.put("id_t",""+idT);
            values.put("id_u",""+idU);
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                if(jsonObject.getInt("success") == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            questionAdapter.delete();
                        }
                    });
                    JSONArray questions = jsonObject.getJSONArray("questions");
                    for(int i = 0; i < questions.length(); i++){
                        JSONObject question = questions.getJSONObject(i);
                        final Question q = new Question();
                        q.setName(question.getString("text"));
                        q.setStat(question.getString("answer"));
                        q.setRight(question.getInt("right"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questionAdapter.add(q);
                                questionAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            return null;
        }
    }
}
