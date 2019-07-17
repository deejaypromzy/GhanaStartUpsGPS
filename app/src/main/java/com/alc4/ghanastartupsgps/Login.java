package com.alc4.ghanastartupsgps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {
    private static final int PERMS_REQUEST_CODE =123456 ;
    // UI references.
    private EditText mEmailView,mPasswordView;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private String userType;
    private SharedPreferences pref;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);


        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mref= FirebaseDatabase.getInstance().getReference();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mfirebaseDatabase.getReference();
        // [END initialize_auth]

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
//            Intent intent = new Intent(Login.this, Start.class);
//            intent.putExtra("user", userType);
//            startActivity(intent);
//            finish();

            if (pref.getString("userType", null)!=null){
                Intent intent = new Intent(Login.this, Start.class);
                intent.putExtra("user", pref.getString("userType", null));
                startActivity(intent);
                finish();
            }
        }
    }

    private void signIn(String email, String password) {
            if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Login.this, "Login failed.",
//                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END sign_in_with_email]
    }
    private boolean validateForm() {
        String email = mEmailView.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email can't be empty.");
            mEmailView.requestFocus();
            return false;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailView.getText().toString().trim()).matches()) {
        mEmailView.setError("Enter correct email.");
            mEmailView.requestFocus();
            return false;
        } else {
            mEmailView.setError(null);
        }
        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password can't be empty.");
            mPasswordView.requestFocus();
            return false;
        } else {
            mPasswordView.setError(null);
        }
        return true;
    }
    boolean noPermissions() {
        int res = 0;
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.INTERNET, Manifest.permission.CALL_PHONE};

        for (String s : permissions) {
            res = checkCallingOrSelfPermission(s);
            if (!(res == PackageManager.PERMISSION_GRANTED))
                return true;
        }
        return false;
    }


    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            if (pref.getString("userType", null)!=null){
                Intent intent = new Intent(Login.this, Start.class);
                intent.putExtra("user", pref.getString("userType", null));
                startActivity(intent);
                finish();
            }else {
                String key = mref.push().getKey();
                mref.child("gps").child(Objects.requireNonNull(key)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showData(dataSnapshot);
                        Intent intent = new Intent(Login.this, Start.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



        }

    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            Database post = dataSnapshot.getValue(Database.class);
          userType="nome";
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("userType",userType);
            editor.apply();

            Log.d("userType",userType);
        }

    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(("Please wait..."));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }



      public void GoToSignup(View view) {
     startActivity(new Intent(Login.this,Signup.class));
    }


    public void Signinmeton(View view) {
        if (Utils.haveNetworkConnection(this))
        signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
        else
            Toasty.error(this, "No internet connection!", Toast.LENGTH_SHORT, true).show();
    }


}

