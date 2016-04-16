package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.Option;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditOptionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionProcessButton button;
    private DatabaseHelper db;
    private GetOptions getOptions;
    private int id_q;
    private int rightIdO;
    private String name;
    private String option0;
    private String option1;
    private String option2;
    private String option3;
    private EditText questionEdit;
    private EditText optionEdit0;
    private EditText optionEdit1;
    private EditText optionEdit2;
    private EditText optionEdit3;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_option);
        db = new DatabaseHelper(getApplicationContext());
        Intent i = getIntent();
        id_q = i.getIntExtra("id_q",0);
        rightIdO = i.getIntExtra("id_o",0);
        name = i.getStringExtra("name");
        questionEdit = (EditText)findViewById(R.id.questionEdit);
        optionEdit0 = (EditText)findViewById(R.id.optionEdit0);
        optionEdit1 = (EditText)findViewById(R.id.optionEdit1);
        optionEdit2 = (EditText)findViewById(R.id.optionEdit2);
        optionEdit3 = (EditText)findViewById(R.id.optionEdit3);
        radioButton = (RadioButton)findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton)findViewById(R.id.radioButton4);

        loadDataToView();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeQuiz);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);
        getOptions = new GetOptions();
        getOptions.execute();

        button = (ActionProcessButton)findViewById(R.id.btnLogIn);
        button.setMode(ActionProcessButton.Mode.ENDLESS);
        button.setColorScheme(getResources().getColor(R.color.colorLight), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup);
                switch(group.getCheckedRadioButtonId()) {
                    case R.id.radioButton:
                        rightIdO = 0;
                        break;
                    case R.id.radioButton2:
                        rightIdO = 1;
                        break;
                    case R.id.radioButton3:
                        rightIdO = 2;
                        break;
                    case R.id.radioButton4:
                        rightIdO = 3;
                        break;
                }
                option0 = optionEdit0.getText().toString();
                option1 = optionEdit1.getText().toString();
                option2 = optionEdit2.getText().toString();
                option3 = optionEdit3.getText().toString();
                name = questionEdit.getText().toString();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        JSONParser jsonParser = new JSONParser();
                        HashMap<String, String> values = new HashMap<>();
                        values.put("tag", "setQuestion");
                        values.put("id_q","" + id_q);
                        values.put("name", name);
                        values.put("option0",option0);
                        values.put("option1",option1);
                        values.put("option2",option2);
                        values.put("option3",option3);
                        values.put("checked","" + rightIdO);
                        try{
                            Log.e("sending",values.toString());
                            JSONObject jsonObject = jsonParser.makePostCall(values);
                            Log.e("getting",jsonObject.toString());
                            if (jsonObject.getInt("success") == 1) {
                                //db.changeName(name);
                                finish();
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

    }

    public void loadDataToView(){
        try{
            questionEdit.setText(name);
            final ArrayList<Option> options = db.getOptionsEdit(id_q);

            for(int i = 0; i<options.size(); i++){
                Option o = options.get(i);
                if(o.getId_o() == rightIdO){
                    int type = o.getType();
                    setRadioButton(type);
                }
                switch(o.getType()){
                    case 0:
                        optionEdit0.setText(o.getName());
                        break;
                    case 1:
                        optionEdit1.setText(o.getName());
                        break;
                    case 2:
                        optionEdit2.setText(o.getName());
                        break;
                    case 3:
                        optionEdit3.setText(o.getName());
                        break;
                }

            }
        }catch(CursorIndexOutOfBoundsException | NullPointerException e){
            Log.e("Loading", e.toString());
        }
    }

    public void setRadioButton(int type){

        switch(type){
            case 0:
                radioButton.setChecked(true);
                break;
            case 1:
                radioButton2.setChecked(true);
                break;
            case 2:
                radioButton3.setChecked(true);
                break;
            case 3:
                radioButton4.setChecked(true);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getOptions = new GetOptions();
        getOptions.execute();
    }

    class GetOptions extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "getOptions");
            values.put("id_q","" + id_q);
            try{
                JSONObject jsonObject = jsonParser.makePostCall(values);
                Log.e("Getting", jsonObject.toString());
                if(jsonObject.getInt("success") == 1) {
                    db.dropOptionsEdit(id_q);
                    JSONArray options = jsonObject.getJSONArray("options");
                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        Option o = new Option();
                        o.setName(option.getString("name"));
                        o.setId_o(option.getInt("id_o"));
                        o.setType(option.getInt("type"));
                        o.setId_q(id_q);
                        db.storeOptionEdit(o);
                    }
                }
            }catch(JSONException e){
                Log.e("GET STATS", e.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadDataToView();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            return null;
        }
    }

}
