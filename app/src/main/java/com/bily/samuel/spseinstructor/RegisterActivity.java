package com.bily.samuel.spseinstructor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String pass;
    private DatabaseHelper db;
    private FragmentManager fm;
    private ActionProcessButton buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(getApplicationContext());

        buttonRegister = (ActionProcessButton)findViewById(R.id.btnSignIn);
        buttonRegister.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonRegister.setColorScheme(getResources().getColor(R.color.colorLight), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorDark));
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
                if(checkBox.isChecked()) {
                    EditText editName = (EditText) findViewById(R.id.regName);
                    EditText editEmail = (EditText) findViewById(R.id.regEmail);
                    EditText editPass = (EditText) findViewById(R.id.regPass);
                    name = editName.getText().toString();
                    email = editEmail.getText().toString();
                    pass = editPass.getText().toString();
                    if (!pass.matches("") && !name.matches("") && !email.matches("")) {
                        Register registration = new Register();
                        registration.execute();
                        buttonRegister.setProgress(50);
                    } else {
                        showToast("Please enter your credentials");
                    }
                }else{
                    showToast("You need to accept privacy policy.");
                }
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
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void onPolicyButton(View view){
        fm = getSupportFragmentManager();
        dialogFragment dFragment = new dialogFragment();
        dFragment.show(fm, "Policy");

    }

    public void onLoginButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
    }

    @SuppressLint("ValidFragment")
    public class dialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_policy, container, false);
            getDialog().setTitle("Privacy policy");
            return rootView;
        }
    }

    class Register extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String,String> values = new HashMap<>();
            values.put("tag","register");
            values.put("email", email);
            values.put("name", name);
            values.put("pass", pass);
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                Log.e("getting",jsonObject.toString());
                if (jsonObject.getInt("success") == 1){
                    Log.e("getting","Sucess = 1");
                    JSONObject jsonUser = jsonObject.getJSONObject("user");
                    User user = new User();
                    user.setIdu(jsonUser.getInt("id"));
                    user.setEmail(jsonUser.getString("email"));
                    user.setName(jsonUser.getString("name"));
                    db.storeUser(user);
                    Log.e("getting","Intent");
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                } else {
                    final String message = jsonObject.getString("msg");
                    if (!jsonObject.getString("msg").isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(message);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Ste offline");
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonRegister.setProgress(0);
                }
            });

            return null;
        }
    }

}
