package com.example.foodorderserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email,password;
    private Button bSignIn;
    private TextView tvSignUp;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);

        bSignIn = findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(this);

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.bSignIn:
                SignInMethod();
                break;
            case R.id.tvSignUp:
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    // Sign In Method .......

    private void SignInMethod()
    {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (Email.isEmpty())
        {
            email.setError("E-mail is Empty");
            password.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            email.setError("Enter A Valid E-mail");
            password.requestFocus();
            return;
        }
        else if (Password.isEmpty())
        {
            email.setError("Password is Empty");
            password.requestFocus();
            return;
        }
        else if (Password.length()<6)
        {
            email.setError("Password length should be 6 charecters");
            password.requestFocus();
            return;
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            signIn(Email,Password);
        }
    }

    // FireBase SignIn....

    private void signIn(String email , String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful())
                        {
                            checkConnection();

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            checkConnection();
        }
    }

    // Check Internet Connection ......

    public void checkConnection()
    {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null!=activeNetwork)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                OpenHomeActivity();
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                OpenHomeActivity();
            }
        }
        else
        {
            Toast.makeText(this , "No Internet Connection" , Toast.LENGTH_SHORT).show();
        }
    }

    // Open Home Activity.......

    private void OpenHomeActivity()
    {
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
