package com.alc4.ghanastartupsgps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.internal.Util;

import es.dmoral.toasty.Toasty;


public class Signup extends AppCompatActivity {
    private EditText name,etEmail,etphone;
    private EditText cpassword,password;
    final int PERMS_REQUEST_CODE = 4444;
    int flag=0;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private String UserType;
    private ScrollView user;
    private LinearLayout linear;

    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // Views
        name = findViewById(R.id.lastname);
        etEmail = findViewById(R.id.email);
        etphone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);



        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference();
        // [END initialize_auth]*/


//        new CountDownTimer(1500, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//
//                if(noPermissions())
//                    requestPermission();
//
//            }
//
//        }.start();



    }



//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        boolean allowed = true;
//        switch (requestCode) {
//            case PERMS_REQUEST_CODE:
//                for (int res : grantResults)
//                    allowed = allowed && res == PackageManager.PERMISSION_GRANTED;
//                break;
//            default:
//                allowed = false;
//                break;
//
//
//        }
//        if (allowed)
//            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
//        else {
//            Toast.makeText(getApplicationContext(), " Permission Denied!!!", Toast.LENGTH_SHORT).show();
//
//        }
//            /*if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                Toast.makeText(getApplicationContext(), " Storage Warning not permitted...", Toast.LENGTH_SHORT).show();
//            else if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS))
//                Toast.makeText(getApplicationContext(), "SMS Warning not permitted...", Toast.LENGTH_SHORT).show(); */
//    }
//


//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        Utils.showProgressDialog(this,"a moment ...");

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signup.this, " Failed to Sign Up.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        Utils.hideProgressDialog(Signup.this);
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
     String mylname = name.getText().toString();
        if (TextUtils.isEmpty(mylname)) {
            name.setError("Name can't be empty.");
            name.requestFocus();
            return false;
        } else {
            name.setError(null);
        } String myemail = etEmail.getText().toString();
        if (TextUtils.isEmpty(myemail)) {
            etEmail.setError("Email can't be empty..");
            etEmail.requestFocus();
            return false;
        } else {
            etEmail.setError(null);
        } String mphone = etphone.getText().toString();
        if (TextUtils.isEmpty(mphone)) {
            etphone.setError("Phone # can't be empty..");
            etphone.requestFocus();
            return false;
        } else if(mphone.length()<10) {
            etphone.setError("Enter correct phone #.");
            etphone.requestFocus();
            return false;
        }else {
            etphone.setError(null);
        } String mypassword = password.getText().toString();
        if (TextUtils.isEmpty(mypassword)) {
            password.setError("Password can't be empty..");
            password.requestFocus();
            return false;
        } else if(mypassword.length()<6) {
            password.setError("Password must be more than 4 characters");
            password.requestFocus();
            return false;
        } else {
            password.setError(null);
        }
        String mycpassword = cpassword.getText().toString();
        if (TextUtils.isEmpty(mycpassword)) {
            cpassword.setError("Cornfirm password");
            name.requestFocus();
            return false;
        } else if (!mypassword.equals(mycpassword)) {
            cpassword.setError("Password do not match");
            cpassword.requestFocus();
            return false;
        } else {
            cpassword.setError(null);
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
        Utils.hideProgressDialog(Signup.this);
        if (user != null) {
            onBackPressed();
          Database dbUser=new Database(
                    name.getText().toString(),
                    etphone.getText().toString(),
                    user.getUid()
            );

            mref.child("gps").push().setValue(dbUser);
            startActivity(new Intent(Signup.this,Login.class));
        }
    }




    @Override
    public void onStop() {
        super.onStop();
       Utils.hideProgressDialog(this);
    }

    public void CreateAccount(View view) {
        if (Utils.haveNetworkConnection(this))
            createAccount(etEmail.getText().toString(), password.getText().toString());
else
            Toasty.error(this, "No internet connection!", Toast.LENGTH_SHORT, true).show();

        // String sepnames[] = nam1.split(":");
        //Toast.makeText(getApplicationContext(),sepnames[0]+" and "+sepnames[1],Toast.LENGTH_LONG).show();
    }

    public void GoToLogin(View view) {
        finish();
    }

    public void nnnn(View view) {
        Toasty.info(this, "Pre!", Toast.LENGTH_SHORT, true).show();

    }
}
