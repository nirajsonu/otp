package com.neeraj.otp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Button sendotp,verify;
    private EditText mobile_no;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String otp_code;
    private final static int RC_SIGN_IN=1;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mobile_no = findViewById(R.id.etPhoneNumber);
        sendotp = findViewById(R.id.btnLogin);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        firebaseAuth=FirebaseAuth.getInstance();
        signInButton=findViewById(R.id.google_login);
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions geo=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,geo);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        verificationStateChangedCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                sendotp.setText(e.getMessage() + "");
                progressDialog.dismiss();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            otp_code=s;
                Intent intent = new Intent(getBaseContext(), verifictaion.class);
                intent.putExtra("otp_code", otp_code);
                startActivity(intent);
            }
        };
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobilenumer="+91"+mobile_no.getText().toString();
                if(!mobilenumer.equalsIgnoreCase(""))
                {
                    verifyMobileNumber(mobilenumer);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Please provide the number",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void verifyMobileNumber(String mobilenumer) {
        progressDialog.show();
    PhoneAuthProvider.getInstance().verifyPhoneNumber(mobilenumer,60,TimeUnit.SECONDS,this,verificationStateChangedCallbacks);
    }
    private void signIn()
    {
        Intent i=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            GoogleSignInAccount acc=completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In sucessfully",Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e)
        {
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Sucessfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    // updateUI(user);
                    Intent i = new Intent(MainActivity.this, sucess.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    // updateUI(null);
                }
            }
        });
    }
}

