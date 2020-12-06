package com.neeraj.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verifictaion extends AppCompatActivity {
    PinView pinView;
    Button verify;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String otp_code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifictaion);
        pinView=findViewById(R.id.pin);
        verify=findViewById(R.id.verify);
        firebaseAuth=FirebaseAuth.getInstance();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=pinView.getText().toString();
                otp_code=getIntent().getStringExtra("otp_code");
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otp_code,code);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(verifictaion.this, "Code verifed", Toast.LENGTH_LONG).show();
                            Intent i=new Intent(verifictaion.this,sucess.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(verifictaion.this, "Enter correct code", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //Toast.makeText(otp.this,"code error",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
