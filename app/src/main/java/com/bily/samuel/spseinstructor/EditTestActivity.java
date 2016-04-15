package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.EditTestAdapter;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.Test;
import com.bily.samuel.spseinstructor.lib.database.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditTestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private GetTests getTests;
    private DatabaseHelper db;
    private EditTestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_test);
        db = new DatabaseHelper(getApplicationContext());

        loadDataToListView();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuiz);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getTests = new GetTests();
        getTests.execute();
    }

    @Override
    public void onRefresh() {
        getTests = new GetTests();
        getTests.execute();
    }

    public void loadDataToListView(){
        try{
            final ArrayList<Test> tests = db.getTestsEdit();
            adapter = new EditTestAdapter(getApplicationContext(),R.layout.row_test);
            for(int i = 0; i<tests.size(); i++){
                Test test = tests.get(i);
                adapter.add(test);
            }

            ListView listView = (ListView)findViewById(R.id.listTest);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Test test = adapter.getItem(position);
                    Intent i = new Intent(getApplicationContext(),EditQuestionActivity.class);
                    i.putExtra("id_t",test.getId_t());
                    i.putExtra("name",test.getName());
                    startActivity(i);
                }
            });
        }catch(CursorIndexOutOfBoundsException | NullPointerException e){
            Log.e("Loading", e.toString());
        }
    }

    class GetTests extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            User u = db.getUser();
            values.put("tag", "getTests");
            values.put("idu","" + u.getIdu());
            try{
                JSONObject jsonObject = jsonParser.makePostCall(values);
                if(jsonObject.getInt("success") == 1) {
                    db.dropTestsEdit();
                    JSONArray tests = jsonObject.getJSONArray("tests");
                    for (int i = 0; i < tests.length(); i++) {
                        JSONObject test = tests.getJSONObject(i);
                        final Test t = new Test();
                        t.setId_t(test.getInt("id_t"));
                        t.setName(test.getString("name"));
                        t.setActive(test.getInt("active"));
                        db.storeTestEdit(t);
                    }
                }
            }catch(JSONException e){
                Log.e("GET STATS", e.toString());
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
