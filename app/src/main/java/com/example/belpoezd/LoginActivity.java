package com.example.belpoezd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity {
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1; // Код запроса разрешения
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton loginImButton;
    private ImageView BP_logo_imageView;
    private ImageButton registerButton;
    //private Button googleSignInButton;
    private TextView noAccountTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    //private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Проверка и запрос разрешения на доступ к интернету
        if (!checkInternetPermission()) {
            requestInternetPermission();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ImageButton loginImButton = findViewById(R.id.loginImButton);
        ImageButton registerButton = findViewById(R.id.registerButton);
        //googleSignInButton = findViewById(R.id.googleSignInButton);
        noAccountTextView = findViewById(R.id.noAccountTextView);
        TextView loginTextView;
        loginTextView = findViewById(R.id.loginTextView);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerButton.setVisibility(View.INVISIBLE);
                loginImButton.setVisibility(View.VISIBLE);
                loginImButton.setEnabled(true);
                registerButton.setEnabled(false);
                loginTextView.setVisibility(View.INVISIBLE);

                noAccountTextView.setVisibility(View.VISIBLE);
                noAccountTextView.setEnabled(true);
            }
        });
        noAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noAccountTextView.setVisibility(View.INVISIBLE);
                noAccountTextView.setEnabled(false);
                registerButton.setVisibility(View.VISIBLE);
                loginImButton.setEnabled(false);
                loginImButton.setVisibility(View.INVISIBLE);
                loginTextView.setVisibility(View.VISIBLE);
                loginTextView.setEnabled(true);
            }
        });
        loginImButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Вход не удался. Пожалуйста, проверьте email и пароль.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Пароль должен содержать как минимум 6 символов", Toast.LENGTH_SHORT).show();
                } else {
                    // Проверяем, есть ли уже зарегистрированный пользователь с этим email
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                if (result != null && result.getSignInMethods() != null && result.getSignInMethods().isEmpty()) {
                                    // Этот email не зарегистрирован, можно провести регистрацию
                                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        String userId = firebaseAuth.getCurrentUser().getUid();
                                                        DatabaseReference usersRef = firebaseDatabase.getReference("users");
                                                        usersRef.child(userId).child("email").setValue(email);

                                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Регистрация не удалась", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Ошибка при проверке
                                Toast.makeText(LoginActivity.this, "Ошибка при проверке email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private boolean checkInternetPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestInternetPermission() {
        if (!checkInternetPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение на доступ к интернету предоставлено, выполните вашу логику
            } else {
                // Пользователь отказал в разрешении, выполните необходимые действия
            }
        }
    }
}
