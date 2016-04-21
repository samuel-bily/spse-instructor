package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.QuestionAdapter;
import com.bily.samuel.spseinstructor.lib.adapter.StatisticsAdapter;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.Question;
import com.bily.samuel.spseinstructor.lib.database.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionsStatsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private int idU;
    private int idT;
    private QuestionAdapter questionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetQuestionStats getQuestionStats;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_stats);
        db = new DatabaseHelper(getApplicationContext());

        Intent i = getIntent();
        idU = i.getIntExtra("id_u",1);
        idT = i.getIntExtra("id_t",1);
        setTitle(i.getStringExtra("name"));

        loadDataToListView();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.renewTest:
                reNewTest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reNewTest(){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                HashMap<String,String> values = new HashMap<>();
                values.put("tag","reNewTest");
                values.put("idt",""+idT);
                values.put("idu",""+idU);
                try {
                    JSONObject jsonObject = jsonParser.makePostCall(values);
                    if(jsonObject.getInt("success") == 1) {
                        finish();
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(findViewById(android.R.id.content),"Ste offline.",Snackbar.LENGTH_SHORT);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void loadDataToListView(){
        try{
            final ArrayList<Question> questions = db.getQuestions(idT,idU);
            questionAdapter = new QuestionAdapter(getApplicationContext(),R.layout.fragment_question);
            for(int i = 0; i<questions.size(); i++){
                Question q = questions.get(i);
                questionAdapter.add(q);
            }
            ListView listView = (ListView)findViewById(R.id.listQuestions);
            listView.setAdapter(questionAdapter);
        }catch(CursorIndexOutOfBoundsException | NullPointerException e){
        }
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
                    db.dropQuestions(idT,idU);
                    JSONArray questions = jsonObject.getJSONArray("questions");
                    for(int i = 0; i < questions.length(); i++){
                        JSONObject question = questions.getJSONObject(i);
                        final Question q = new Question();
                        q.setName(question.getString("text"));
                        q.setStat(question.getString("answer"));
                        q.setRight(question.getInt("right"));
                        q.setIdT(idT);
                        q.setIdU(idU);
                        db.storeQuestions(q);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadDataToListView();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            return null;
        }
    }
}
