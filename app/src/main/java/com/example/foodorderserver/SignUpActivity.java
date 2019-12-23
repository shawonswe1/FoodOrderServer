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

import com.example.foodorderserver.model.AdminInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name,email,phone,password;
    private Button bSignUp;
    private TextView tvSignIn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userDataSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        userDataSubmit = FirebaseDatabase.getInstance().getReference().child("Food Order").child("Admin");

        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        phone = findViewById(R.id.etPhone);
        password = findViewById(R.id.etPassword);

        bSignUp = findViewById(R.id.bSignUp);
        bSignUp.setOnClickListener(this);

        tvSignIn = findViewById(R.id.tvSignIn);
        tvSignIn.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.bSignUp:
                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

                if (null!=activeNetwork)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        SignUpMethod();
                    }
                    else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        SignUpMethod();
                    }
                }
                else
                {
                    Toast.makeText(this , "No Internet Connection" , Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tvSignIn:
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void SignUpMethod()
    {
        final String Name = name.getText().toString().trim();
        String Email = email.getText().toString().trim();
        final String Phone = phone.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (Name.isEmpty())
        {
            name.setError("Name is Empty");
            email.requestFocus();
            return;
        }
        else if (Email.isEmpty())
        {
            email.setError("E-mail is Empty");
            phone.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            email.setError("Enter A Valid E-mail");
            phone.requestFocus();
            return;
        }
        else if (Phone.isEmpty())
        {
            phone.setError("Phone is Empty");
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

            // final String ID = userDataSubmit.push().getKey();
            final AdminInfo userInfo = new AdminInfo(Name,Email,Password);

            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful())
                            {
                                userDataSubmit.child(Phone).setValue(userInfo);

                                // Update Profile Name
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(Name).build();
                                user.updateProfile(profileChangeRequest);
                                //......

                                Toast.makeText(SignUpActivity.this, "Seccessfully Sign Up", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    Toast.makeText(SignUpActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(SignUpActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
}
