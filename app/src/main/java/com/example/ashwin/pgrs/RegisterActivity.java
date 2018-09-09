package com.example.ashwin.pgrs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity
{

    EditText name;
    EditText email;
    EditText mobile;
    EditText password;
    EditText confirmPassword;
    Button register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        name = findViewById(R.id.name_id);
        email = findViewById(R.id.email_id);
        mobile = findViewById(R.id.mobile_id);
        password = findViewById(R.id.password_id);
        confirmPassword = findViewById(R.id.confirm_password_id);
        register = findViewById(R.id.register_id);
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(password.getText().toString().equals(confirmPassword.getText().toString())))
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!",
                            Toast.LENGTH_SHORT).show();
                } else if(name.getText().toString().equals("")||email.getText().toString().equals("")||mobile.getText().toString().equals("")||password.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Enter all details.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    funct();
                    Toast.makeText(getApplicationContext(), "Sign Up Successful!",
                            Toast.LENGTH_SHORT).show();
                    startLoginActivity();
                }
            }
        });

    }
    public void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void funct()
    {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(),
                password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Sign Up failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
