package com.dopra.roadadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class MainMenu extends AppCompatActivity {

//--- Firebase Authentication Elements

    private FirebaseAuth auth = FirebaseAuth.getInstance();

        //This will give us a reference to the root in JSON DB
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        //We're creating a "cdc_roadCondition" location under the rootRef
        DatabaseReference cdc_roadConditionRef = rootRef.child("cdc_roadCondition");

        //We're creating a "cdc_roadCondition" location under the rootRef
        DatabaseReference cac_roadConditionRef = rootRef.child("cac_roadCondition");


//--- Firebase Remote Config

    private FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    //--- UI Elements
    TextView cdc_roadCondition_textView;
    Button cdc_open_button;
    Button cdc_closed_button;

    TextView cac_roadCondition_textView;
    Button cac_open_button;
    Button cac_closed_button;
    
    Toolbar toolbar;

    Boolean isAdminProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setUserId(auth.getCurrentUser().getUid());

        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build());

        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("is_user_admin", true);
        remoteConfig.setDefaults(defaults);

        final Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                updateUI();
            }
        });

        //--- Get UI elements
        cdc_roadCondition_textView = (TextView)findViewById(R.id.cdc_textView_condition);
        cdc_open_button = (Button)findViewById(R.id.cdc_button_open);
        cdc_closed_button = (Button)findViewById(R.id.cdc_button_close);

        cac_roadCondition_textView = (TextView)findViewById(R.id.cac_textView_condition);
        cac_open_button = (Button)findViewById(R.id.cac_button_open);
        cac_closed_button = (Button)findViewById(R.id.cac_button_close);
        
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            auth.signOut();
            startActivity(new Intent(MainMenu.this, Login.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

//------- CAMINO DEL CUADRADO --------

        //--- This will be triggered each time that the "roadCondition" changes
        cdc_roadConditionRef.addValueEventListener(new ValueEventListener() {

            //--- For success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                cdc_roadCondition_textView.setText(status);
            }


            //--- For the errors
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cdc_open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdc_roadConditionRef.setValue("ABIERTO");
            }
        });

        cdc_closed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdc_roadConditionRef.setValue("CERRADO");
            }
        });

//------ CAMINO DE LAS ALTAS CUMBRES -------

        //--- This will be triggered each time that the "roadCondition" changes
        cac_roadConditionRef.addValueEventListener(new ValueEventListener() {

            //--- For success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                cac_roadCondition_textView.setText(status);
            }


            //--- For the errors
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cac_open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cac_roadConditionRef.setValue("ABIERTO");
            }
        });

        cac_closed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cac_roadConditionRef.setValue("CERRADO");
            }
        });
    }

    private void updateUI() {

        isAdminProfile = remoteConfig.getBoolean("is_user_admin");

        if (!isAdminProfile) {

            cdc_open_button.setVisibility(View.INVISIBLE);
            cdc_closed_button.setVisibility(View.INVISIBLE);
            cac_open_button.setVisibility(View.INVISIBLE);
            cac_closed_button.setVisibility(View.INVISIBLE);

        }
    }
}


