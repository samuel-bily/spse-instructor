package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddQuestionActivity extends AppCompatActivity {

    private ActionProcessButton button;

    private int id_t;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setTitle("Nová otázka");

        Intent i = getIntent();
        id_t = i.getIntExtra("id_t",0);

        questionEdit = (EditText)findViewById(R.id.questionEdit);
        optionEdit0 = (EditText)findViewById(R.id.optionEdit0);
        optionEdit1 = (EditText)findViewById(R.id.optionEdit1);
        optionEdit2 = (EditText)findViewById(R.id.optionEdit2);
        optionEdit3 = (EditText)findViewById(R.id.optionEdit3);

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
                        values.put("tag", "addQuestion");
                        values.put("id_t","" + id_t);
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
                                showToast("Pridané!");
                                finish();
                            }else{
                                showToast("Ste offline");
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
}
