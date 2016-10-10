package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FragmentManager fm;
    private String testName;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        db = new DatabaseHelper(getApplicationContext());
        user = db.getUser();
        if(user == null){
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
        }else{
            try{
                String active = intent.getExtras().getString("active");
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                Log.e("active",active);
                if (active != null) {
                    if(active.equals("1")){
                        db.changeActive(1);
                    }else{
                        db.changeActive(0);
                    }
                }
            }catch (NullPointerException e){

            }
            TextView userName = (TextView) findViewById(R.id.userName);
            TextView userEmail = (TextView) findViewById(R.id.userEmail);
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
            sendRegId();
            if(user.getActive() > 0){
                GridLayout gridLayout = (GridLayout)findViewById(R.id.noActivatedLayout);
                gridLayout.setVisibility(View.GONE);
            }
        }
    }

    public void sendRegId(){
        final String regId = FirebaseInstanceId.getInstance().getToken();

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                db = new DatabaseHelper(getApplicationContext());
                User user = db.getUser();
                JSONParser jsonParser = new JSONParser();
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "setRegId");
                values.put("regId",regId);
                values.put("id_i","" + user.getIdu());
                try{
                    Log.e("sending",values.toString());
                    JSONObject jsonObject = jsonParser.makePostCall(values);
                    //Log.e("getting",jsonObject.toString());
                    if (jsonObject.getInt("success") == 1) {
                    }else{
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    public void onResume(){
        super.onResume();
        db = new DatabaseHelper(getApplicationContext());
        User user = db.getUser();
        if(user == null){
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
        }else {
            TextView userName = (TextView) findViewById(R.id.userName);
            TextView userEmail = (TextView) findViewById(R.id.userEmail);
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
        }
        Log.e("IS ACTIVE?"," " + user.getActive());
        if(user.getActive() > 0){
            GridLayout gridLayout = (GridLayout)findViewById(R.id.noActivatedLayout);
            gridLayout.setVisibility(View.GONE);
        }
    }

    public void onLayoutListener(View view){
        int id = view.getId();
        user = db.getUser();
        if(user.getActive()>0) {
            switch (id) {
                case R.id.newTest:
                    addTest();
                    break;
                case R.id.myTests:
                    myTests();
                    break;
                case R.id.stats:
                    stats();
                    break;
                case R.id.userLayout:
                    profile();
                    break;
                default:
                    break;
            }
        }else{
            Intent intent = new Intent(getApplicationContext(),ActivateUserScrollingActivity.class);
            startActivity(intent);
        }
    }

    public void addTest(){
        fm = getSupportFragmentManager();
        dialogFragment dFragment = new dialogFragment();
        dFragment.show(fm, "Vytvoriť test");
    }

    public void profile(){
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    public void stats(){
        Intent i = new Intent(getApplicationContext(), StatisticsActivity.class);
        startActivity(i);
    }

    public void myTests(){
        Intent i = new Intent(getApplicationContext(), EditTestActivity.class);
        startActivity(i);
    }

    @SuppressLint("ValidFragment")
    public class dialogFragment extends DialogFragment {
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
                                    Intent i = new Intent(getApplicationContext(),EditTestActivity.class);
                                    startActivity(i);
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
