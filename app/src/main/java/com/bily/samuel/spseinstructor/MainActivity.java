package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FragmentManager fm;

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
        /**
        buttonLogout = (ActionProcessButton)findViewById(R.id.btnLogIn);
        buttonLogout.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonLogout.setColorScheme( getResources().getColor(R.color.colorLight),getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorDark),getResources().getColor(R.color.colorPrimaryDark));
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogout.setProgress(50);
                db.dropTable();
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                finish();
                startActivity(i);
            }
        });**/
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
                    EditText number = (EditText) rootView.findViewById(R.id.questionNumber);
                    Intent i = new Intent(getApplicationContext(),AddTest.class);
                    i.putExtra("name",name.getText().toString());
                    i.putExtra("number",Integer.parseInt(number.getText().toString()));
                    startActivity(i);
                }
            });
            return rootView;
        }

    }

}
