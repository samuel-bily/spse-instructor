package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private FragmentManager fm;
    private EditTestAdapter adapter;
    private String testName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_test);
        db = new DatabaseHelper(getApplicationContext());
        setTitle("Moje testy");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm = getSupportFragmentManager();
                AddTest fragment = new AddTest();
                fragment.show(fm, "Vytvoriť test");
            }
        });

        loadDataToListView();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuiz);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setOnRefreshListener(this);
        //noinspection ResourceAsColor
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);

        getTests = new GetTests();
        getTests.execute();
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
        getTests = new GetTests();
        getTests.execute();
    }

    @Override
    public void onResume(){
        super.onResume();
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
                    i.putExtra("active", test.getActive());
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
                    if(tests.length() == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Zatial nemáte žiadne štatistiky");
                            }
                        });
                    }
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

    @SuppressLint("ValidFragment")
    public class AddTest extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_add_test, container, false);
            getDialog().setTitle("Vytvoriť test");

            (rootView.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText name = (EditText)rootView.findViewById(R.id.testNameEdit);
                    testName = name.getText().toString();
                    new AsyncTask<Void,Void,Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {
                            db = new DatabaseHelper(getApplicationContext());
                            User user = db.getUser();
                            JSONParser jsonParser = new JSONParser();
                            HashMap<String, String> values = new HashMap<>();
                            values.put("tag", "setTest");
                            values.put("name",testName);
                            values.put("id_i","" + user.getIdu());
                            try{
                                Log.e("sending",values.toString());
                                JSONObject jsonObject = jsonParser.makePostCall(values);
                                Log.e("getting",jsonObject.toString());
                                if (jsonObject.getInt("success") == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getTests = new GetTests();
                                            getTests.execute();
                                        }
                                    });
                                    dismiss();
                                }else{
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }
            });
            return rootView;
        }

    }
}
