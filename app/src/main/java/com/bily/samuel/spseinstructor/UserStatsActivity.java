package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.StatisticsAdapter;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserStatsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private int idT;
    private String test;
    private StatisticsAdapter statisticsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetUserStats getUserStats;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        db = new DatabaseHelper(getApplicationContext());

        Intent i = getIntent();
        idT = i.getIntExtra("id_t",1);
        test = i.getStringExtra("name");
        setTitle(test);
        loadDataToListView();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeUsers);
        swipeRefreshLayout.setOnRefreshListener(this);
        //noinspection ResourceAsColor
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getUserStats = new GetUserStats();
        getUserStats.execute();
    }

    public void showToast(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        @SuppressWarnings("ConstantConditions") int Y = getSupportActionBar().getHeight();
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,Y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onRefresh() {
        getUserStats = new GetUserStats();
        getUserStats.execute();
    }

    public void loadDataToListView(){
        try{
            final ArrayList<Test> tests = db.getUsers(idT);
            statisticsAdapter = new StatisticsAdapter(getApplicationContext(),R.layout.fragment_stats);
            for(int i = 0; i<tests.size(); i++){
                Test test = tests.get(i);
                statisticsAdapter.add(test);
            }

            ListView listView = (ListView)findViewById(R.id.listUsers);
            listView.setAdapter(statisticsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Test test = statisticsAdapter.getItem(position);
                    Intent i = new Intent(getApplicationContext(),QuestionsStatsActivity.class);
                    i.putExtra("id_u",test.getId_t());
                    i.putExtra("id_t",idT);
                    i.putExtra("name",test.getName());
                    startActivity(i);
                }
            });
        }catch(CursorIndexOutOfBoundsException | NullPointerException e){
            getUserStats = new GetUserStats();
            getUserStats.execute();
        }
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
                    db.dropUsers(idT);
                    JSONArray tests = jsonObject.getJSONArray("users");
                    for(int i = 0; i < tests.length(); i++){
                        JSONObject test = tests.getJSONObject(i);
                        final Test t = new Test();
                        t.setId_t(test.getInt("id_u"));
                        t.setId(idT);
                        t.setName(test.getString("name"));
                        t.setStat(test.getDouble("stat"));
                        db.storeUsers(t);
                    }
                }
            }catch(JSONException e){
                showToast("Ste offline");
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
