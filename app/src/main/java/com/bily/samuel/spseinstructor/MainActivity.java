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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.adapter.EditTestAdapter;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FragmentManager fm;
    private String testName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    public void onAddTestButtonClicked(View view){
        fm = getSupportFragmentManager();
        dialogFragment dFragment = new dialogFragment();
        dFragment.show(fm, "Policy");
    }

    public void onProfileButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    public void onStatisticsButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(), StatisticsActivity.class);
        startActivity(i);
    }

    public void onMyTestsButtonClicked(View view){
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
