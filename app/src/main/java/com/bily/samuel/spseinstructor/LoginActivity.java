package com.bily.samuel.spseinstructor;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String email;
    private String pass;
    private DatabaseHelper db;
    private ActionProcessButton buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(getApplicationContext());

        buttonLogin = (ActionProcessButton)findViewById(R.id.btnLogIn);
        buttonLogin.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonLogin.setColorScheme( getResources().getColor(R.color.colorLight),getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorDark),getResources().getColor(R.color.colorPrimaryDark));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogin.setProgress(50);
                EditText editPass = (EditText) findViewById(R.id.editPass);
                EditText editEmail = (EditText) findViewById(R.id.editEmail);
                email = editEmail.getText().toString();
                pass = editPass.getText().toString();
                new Login().execute();
            }
        });
    }

    public void onRegisterButtonClicked(View view){
        Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(i);
    }

    public void showSnackbar(String msg){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.clearAnimation();
        view.setLayoutParams(params);
        snack.show();
    }

    class Login extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> values = new HashMap<>();
            values.put("tag", "login");
            values.put("email", email);
            values.put("pass", pass);
            try {
                JSONObject jsonObject = jsonParser.makePostCall(values);
                int success = jsonObject.getInt("success");
                if (success == 1) {
                    JSONObject jsonUser = jsonObject.getJSONObject("user");
                    User user = new User();
                    user.setIdu(jsonUser.getInt("id"));
                    user.setEmail(jsonUser.getString("email"));
                    user.setName(jsonUser.getString("name"));
                    db.storeUser(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            finish();
                            startActivity(i);
                        }
                    });

                } else {
                    final String message = jsonObject.getString("msg");
                    if (!jsonObject.getString("msg").isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar(message);
                                buttonLogin.setProgress(0);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar("Something went wrong.");
                                buttonLogin.setProgress(0);
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
                    buttonLogin.setProgress(0);
                }
            });
            return null;
        }
    }

}
