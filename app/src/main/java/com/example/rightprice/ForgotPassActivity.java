package com.example.rightprice;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

//Class for handling when the user forgets their password
public class ForgotPassActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button nextBtn, backBtn;

    /*
    This method handles the creation of the forgot password interface
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        nextBtn = (Button) findViewById(R.id.nextbtn);
        backBtn = (Button) findViewById(R.id.backbtn);

        backBtn.setVisibility(View.INVISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail();
            }
        });


    }

    /*
    this method sends a new temporary passcode to the user's email for a user to reset their account with if they
    forget their password
     */
    protected void sendEmail() {
        EditText emailText = (EditText) findViewById(R.id.userEmail);
        String email = emailText.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        try {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SEND EMAIL", "Email sent.");
                                Toast.makeText(ForgotPassActivity.this, "Email Sent.",
                                        Toast.LENGTH_SHORT).show();
                                nextBtn.setVisibility(View.INVISIBLE);
                                backBtn.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(ForgotPassActivity.this, "Email Not Sent.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(ForgotPassActivity.this, "Please enter all information.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
