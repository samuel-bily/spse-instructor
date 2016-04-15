package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.EditQuestionAdapter;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.Question;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditQuestionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentManager fm;
    private DatabaseHelper db;
    private ActionProcessButton button;
    private EditQuestionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetQuestions getQuestions;
    private String name;
    private int id_t;
    private int active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        db = new DatabaseHelper(getApplicationContext());
        Intent i = getIntent();
        name = i.getStringExtra("name");
        id_t = i.getIntExtra("id_t",0);
        active = i.getIntExtra("active",0);
        setTitle(name);
        loadDataToListView();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuiz);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getQuestions = new GetQuestions();
        getQuestions.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_question, menu);
        MenuItem publish = menu.findItem(R.id.publish);
        MenuItem nopublish = menu.findItem(R.id.nopublish);
        if(active==0){
            publish.setEnabled(true);
        }else{
            nopublish.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit_name:
                editName();
                return true;
            case R.id.publish:
                publish();
                return true;
            case R.id.nopublish:
                noPublish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void publish(){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "publish");
                values.put("id_t", "" + id_t);
                Log.e("Sending", values.toString());
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

    public void noPublish(){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "noPublish");
                values.put("id_t", "" + id_t);
                Log.e("Sending", values.toString());
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

    public void editName(){
        fm = getSupportFragmentManager();
        ChangeTestName changeName = new ChangeTestName();
        changeName.show(fm, "Zmena mena");
    }

    @Override
    public void onRefresh() {
        getQuestions = new GetQuestions();
        getQuestions.execute();
    }

    public void loadDataToListView(){
        try{
            final ArrayList<Question> questions = db.getQuestionsEdit(id_t);
            adapter = new EditQuestionAdapter(getApplicationContext(),R.layout.row_question);
            for(int i = 0; i<questions.size(); i++){
                Question q = questions.get(i);
                adapter.add(q);
            }

            ListView listView = (ListView)findViewById(R.id.listQuestions);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Question q = adapter.getItem(position);
                    Intent i = new Intent(getApplicationContext(),EditQuestionActivity.class);
                    i.putExtra("id_t",q.getId());
                    i.putExtra("name",q.getName());
                    startActivity(i);
                }
            });
        }catch(CursorIndexOutOfBoundsException | NullPointerException e){
            Log.e("Loading", e.toString());
        }
    }


    class GetQuestions extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "getQuestions");
            values.put("id_t","" + id_t);

            try{
                JSONObject jsonObject = jsonParser.makePostCall(values);
                if(jsonObject.getInt("success") == 1) {
                    db.dropQuestionsEdit(id_t);
                    JSONArray tests = jsonObject.getJSONArray("questions");
                    for (int i = 0; i < tests.length(); i++) {
                        JSONObject question = tests.getJSONObject(i);
                        Question q = new Question();
                        q.setId(question.getInt("id_q"));
                        q.setName(question.getString("text"));
                        q.setRight(question.getInt("id_o"));
                        q.setIdT(id_t);
                        db.storeQuestionsEdit(q);
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

    @SuppressLint("ValidFragment")
    public class ChangeTestName extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_change_name, container, false);
            getDialog().setTitle("Premenovať test");
            EditText editText = (EditText)rootView.findViewById(R.id.fragName);
            editText.setText(name);
            button = (ActionProcessButton)rootView.findViewById(R.id.btnLogIn);
            button.setMode(ActionProcessButton.Mode.ENDLESS);
            button.setColorScheme(getResources().getColor(R.color.colorLight), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.setProgress(50);
                    final EditText editName = (EditText)rootView.findViewById(R.id.fragName);
                    name = editName.getText().toString();
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            JSONParser jsonParser = new JSONParser();
                            HashMap<String, String> values = new HashMap<>();
                            values.put("tag","changeTestName");
                            values.put("name", name);
                            try {
                                JSONObject jsonObject = jsonParser.makePostCall(values);
                                if (jsonObject.getInt("success") == 1) {
                                    //db.changeName(name);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(rootView,"Ste offline.",Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                    button.setProgress(0);
                }
            });
            return rootView;
        }
    }
}
