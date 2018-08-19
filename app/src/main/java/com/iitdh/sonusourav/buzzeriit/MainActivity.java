package com.iitdh.sonusourav.buzzeriit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "BuzzerIIt";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    //And also a Firebase Auth object
    FirebaseAuth mAuth;
    private KenBurnsView kenBurnsView ;
    SignInButton signInButton;
    private CircleImageView quizImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);
        signInButton=findViewById(R.id.sign_in_button);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.translate);
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout);

                signInButton.setVisibility(View.VISIBLE);
                quizImage.setVisibility(View.VISIBLE);
                signInButton.startAnimation(shake);
                quizImage.startAnimation(fadeIn);

            }
        }, 2000);

        kenBurnsView = (KenBurnsView) findViewById(R.id.image);
        quizImage=findViewById(R.id.quiz_image);

        kenBurnsView.animate().setStartDelay(1000).setDuration(2500).alpha(0.25f).start();

        RandomTransitionGenerator generator = new RandomTransitionGenerator(20000,  new AccelerateDecelerateInterpolator());
        kenBurnsView.setTransitionGenerator(generator); //set new transition on kenburns view
        kenBurnsView.setTransitionListener(onTransittionListener());

        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            Toast.makeText(MainActivity.this, "User Signed In" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private KenBurnsView.TransitionListener onTransittionListener() {
        return new KenBurnsView.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }
        };
    }
}
