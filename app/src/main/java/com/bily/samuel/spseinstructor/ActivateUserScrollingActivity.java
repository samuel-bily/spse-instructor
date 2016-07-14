package com.bily.samuel.spseinstructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;

public class ActivateUserScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_user_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView text = (TextView)findViewById(R.id.noActivated);
        text.setText(getText(R.string.no_activated));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "samobily1@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "[Edukviz Instructor] Žiadosť o aktiváciu účtu");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    public void onLogoutClicked(View view){
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.dropTable();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(i);
    }
}
