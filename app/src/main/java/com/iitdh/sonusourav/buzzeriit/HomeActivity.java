package com.iitdh.sonusourav.buzzeriit;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    CircleImageView imageView;
    TextView textName, textEmail;
    FirebaseAuth mAuth;
    ImageButton buzzerButton;
    private FirebaseDatabase firebaseInstance;
    private DatabaseReference databaseReference;
    String date;
    int number=0;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public static String PREFS_NAME = "pref";
    String qsNumber;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();


        pref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);
        buzzerButton=findViewById(R.id.buzzer);
        final FirebaseUser user = mAuth.getCurrentUser();
        firebaseInstance = FirebaseDatabase.getInstance();
        databaseReference = firebaseInstance.getReference("BuzzerTime");
        if(user != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(imageView);

            textName.setText(user.getDisplayName());
            textEmail.setText(user.getEmail());
        }else
        {
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            finish();
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.buzzersound);



        buzzerButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        mp.start();
                        ++number;




                        editor.putString("number", number+"");
                        editor.apply();

                        assert user != null;
                         qsNumber=pref.getString("number",0+"");




                       databaseReference.child("QuestionNumber"+qsNumber).child(Objects.requireNonNull(user.getDisplayName())).setValue(ServerValue.TIMESTAMP);

                        databaseReference.child("QuestionNumber"+qsNumber).child(Objects.requireNonNull(user.getDisplayName())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Log.d("datasnapshot", Objects.requireNonNull(dataSnapshot.getValue()).toString());
                                String longV = dataSnapshot.getValue().toString();
                                long millisecond = Long.parseLong(longV);

                                DateFormat df = new SimpleDateFormat("d MMM yyyy, hh:mm:ss::SS a", Locale.US);
                                date = df.format(new Date(millisecond));

                                databaseReference.child("QuestionNumber"+qsNumber).child(Objects.requireNonNull(user.getDisplayName())).setValue(date);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext(),"Network error!",Toast.LENGTH_SHORT).show();
                            }


                        });




                        break;
                    }
                    case MotionEvent.ACTION_UP:


                        // Your action here on button click

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        HomeActivity.super.onBackPressed();
                    }
                }).create().show();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

}
