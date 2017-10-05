package com.dopra.roadadvisor;

//USER VERSION

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainMenu extends AppCompatActivity {

//--- Firebase Authentication Elements

    private FirebaseAuth auth = FirebaseAuth.getInstance();

        // This will give us a reference to the root in RT Database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Creates "cdc_roadCondition" location under the rootRef
        DatabaseReference cdc_roadConditionRef = rootRef.child("cdc_roadCondition");

        // Creates "cac_roadCondition" location under the rootRef
        DatabaseReference cac_roadConditionRef = rootRef.child("cac_roadCondition");

        // Creates "users" location under the rootRef
        DatabaseReference usersRef = rootRef.child("users");



//--- UI Elements

    TextView cdc_roadCondition_textView;
    Button cdc_open_button;
    Button cdc_closed_button;

    TextView cac_roadCondition_textView;
    Button cac_open_button;
    Button cac_closed_button;

    ProgressBar cdc_spinner;
    ProgressBar cac_spinner;

    ImageView cdc_background;
    ImageView cac_background;

    Toolbar toolbar;


//--- TAGS

    private final String CDC_TAG = "CDC Update --> ";
    private final String CAC_TAG = "CAC Update --> ";
    private final String UID = "UID --> ";
    private final String DEVICE = "deviceToken --> ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        // Initialize User Interface
        init_UI();

        // Creates a reference to the UID under "users" location
        DatabaseReference uidRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Creates "deviceToken" location under the UID
        DatabaseReference deviceTokenRef = uidRef.child("deviceToken");

        // Update the value of the deviceToken
        deviceTokenRef.setValue(FirebaseInstanceId.getInstance().getToken().toString());

        // Logs
        Log.d(UID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.d(DEVICE, FirebaseInstanceId.getInstance().getToken().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();

    //------- CAMINO DEL CUADRADO --------

        //--- This will be triggered each time that the "roadCondition" changes
        cdc_roadConditionRef.addValueEventListener(new ValueEventListener() {

            //--- If success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String prev_status = cdc_roadCondition_textView.getText().toString();
                String status = dataSnapshot.getValue(String.class);
                cdc_spinner.setVisibility(View.GONE);
                cdc_roadCondition_textView.setText(status);
                cdc_roadCondition_textView.setVisibility(View.VISIBLE);

                // This will change the background to Black and White
                if (status.equals("CERRADO") && prev_status.equals("ABIERTO")){
                    ((TransitionDrawable) cdc_background.getDrawable()).startTransition(1000);
                }

                // This will change back the background to color
                else if (status.equals("ABIERTO") && prev_status.equals("CERRADO")) {
                    ((TransitionDrawable) cdc_background.getDrawable()).reverseTransition(1000);
                }
            }


            //--- For Errors
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(CDC_TAG, databaseError.getDetails());
            }
        });


    //------ CAMINO DE LAS ALTAS CUMBRES -------

        //--- This will be triggered each time that the "roadCondition" changes
        cac_roadConditionRef.addValueEventListener(new ValueEventListener() {

            //--- For success
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String prev_status = cac_roadCondition_textView.getText().toString();
                String status = dataSnapshot.getValue(String.class);
                cac_spinner.setVisibility(View.GONE);
                cac_roadCondition_textView.setText(status);
                cac_roadCondition_textView.setVisibility(View.VISIBLE);

                // This will change the background to Black and White
                if (status.equals("CERRADO") && prev_status.equals("ABIERTO")){
                    ((TransitionDrawable) cac_background.getDrawable()).startTransition(1000);
                }

                // This will change back the background to color
                else if (status.equals("ABIERTO")  && prev_status.equals("CERRADO")) {
                    ((TransitionDrawable) cac_background.getDrawable()).reverseTransition(1000);
                }
            }


            //--- For the errors
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(CAC_TAG, databaseError.getDetails());
            }
        });

    }


//--- AUXILIAR FUNCTIONS

    private void init_UI() {

        //--- Get UI elements
        cdc_roadCondition_textView = (TextView)findViewById(R.id.cdc_textView_condition);
        cdc_roadCondition_textView.setVisibility(View.GONE);
        cdc_open_button = (Button)findViewById(R.id.cdc_button_open);
        cdc_closed_button = (Button)findViewById(R.id.cdc_button_close);
        cdc_spinner = (ProgressBar) findViewById(R.id.progressBar_cdc);
        cdc_background = (ImageView) findViewById(R.id.cdc_background_iv);

        cac_roadCondition_textView = (TextView)findViewById(R.id.cac_textView_condition);
        cac_roadCondition_textView.setVisibility(View.GONE);
        cac_open_button = (Button)findViewById(R.id.cac_button_open);
        cac_closed_button = (Button)findViewById(R.id.cac_button_close);
        cac_spinner = (ProgressBar) findViewById(R.id.progressBar_cac);
        cac_background = (ImageView) findViewById(R.id.cac_background_iv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //Hide the Buttons (this is the main diference between Admin and Users
        cdc_open_button.setVisibility(View.INVISIBLE);
        cdc_closed_button.setVisibility(View.INVISIBLE);
        cac_open_button.setVisibility(View.INVISIBLE);
        cac_closed_button.setVisibility(View.INVISIBLE);

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

        //--- Seetings Action
        if (id == R.id.action_settings) {
            return true;
        }

        //--- Logout Action
        if (id == R.id.action_logout) {

            auth.signOut();
            startActivity(new Intent(MainMenu.this, Login.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


