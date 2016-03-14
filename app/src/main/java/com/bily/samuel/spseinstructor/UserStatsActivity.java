package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.StatisticsAdapter;
import com.bily.samuel.spseinstructor.lib.database.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserStatsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private int idT;
    private String test;
    private StatisticsAdapter statisticsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetUserStats getUserStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        Intent i = getIntent();
        idT = i.getIntExtra("id_t",1);
        test = i.getStringExtra("name");
        setTitle(test);

        statisticsAdapter = new StatisticsAdapter(getApplicationContext(),R.layout.fragment_stats);

        ListView listView = (ListView)findViewById(R.id.listUsers);
        listView.setAdapter(statisticsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Test test = statisticsAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(),QuestionsStatsActivity.class);
                i.putExtra("id_u",test.getId_t());
                i.putExtra("id_t",idT);
                i.putExtra("name", test.getName());
                startActivity(i);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeUsers);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getUserStats = new GetUserStats();
        getUserStats.execute();
    }

    @Override
    public void onRefresh() {
        getUserStats = new GetUserStats();
        getUserStats.execute();
    }

    class GetUserStats extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "getUsersStats");
            values.put("id_t", "" + idT);
            try{
                JSONObject jsonObject = jsonParser.makePostCall(values);
                Log.e("JSON", jsonObject.toString());
                if(jsonObject.getInt("success") == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsAdapter.delete();
                        }
                    });
                    JSONArray tests = jsonObject.getJSONArray("users");
                    for(int i = 0; i < tests.length(); i++){
                        JSONObject test = tests.getJSONObject(i);
                        final Test t = new Test();
                        t.setId_t(test.getInt("id_u"));
                        t.setName(test.getString("name"));
                        t.setStat(test.getDouble("stat"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statisticsAdapter.add(t);
                                statisticsAdapter.notifyDataSetChanged();
                            }
                        });

                    }


                }
            }catch(JSONException e){
                Log.e("GET STATS", e.toString());
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
