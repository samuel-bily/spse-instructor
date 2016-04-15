package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditQuestionActivity extends AppCompatActivity {

    private FragmentManager fm;
    private DatabaseHelper db;
    private ActionProcessButton button;
    private String name;
    private int id_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        id_t = i.getIntExtra("id_t",0);
        setTitle(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit_name:
                editName();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editName(){
        fm = getSupportFragmentManager();
        ChangeTestName changeName = new ChangeTestName();
        changeName.show(fm, "Zmena mena");
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
