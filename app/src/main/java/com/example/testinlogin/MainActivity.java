package com.example.testinlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView sign_up_textView;
    private EditText editTextEmail, editTextPassword;
    private Button login_button;
    private ProgressBar login_progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference
        mAuth = FirebaseAuth.getInstance();
        mStore  = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.login_email_editText);
        editTextPassword = findViewById(R.id.login_password_editText);

        sign_up_textView = findViewById(R.id.sign_up_textView);
        sign_up_textView.setOnClickListener(this);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        login_progressBar = findViewById(R.id.login_progressBar);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_up_textView){
            Intent intent = new Intent(MainActivity.this, SignUpAcivity.class);
            startActivity(intent);
        }
        else {
            if(v.getId() == R.id.login_button){
                loginUser();
            }
        }
    }

    private void loginUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Please put an email!");
            editTextEmail.requestFocus();
            return;
        }else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Por favor insira um email válido!");
                editTextEmail.requestFocus();
                return;
            }
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        else {
            if(password.length() < 6){
                editTextPassword.setError("Palavra-passe deve conter no mínimo 6 caracteres!");
                editTextPassword.requestFocus();
                return;
            }
        }

        //Set progress bar visibility to visible
        login_progressBar.setVisibility(View.VISIBLE);

        loginFireBase(email, password);
    }

    private void loginFireBase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                String userID = mAuth.getUid();


                mStore.collection("users").document(userID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String type_of_user = document.get("tipo_utilizador").toString();

                                if(type_of_user.equals("Administrador")){
                                    login_progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(MainActivity.this, AdminHomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    login_progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Log.d("LOGGER", "No such document");
                                login_progressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Erro de login!", Toast.LENGTH_SHORT).show();
                            Log.d("LOGGER", "get failed with ", task.getException());
                            login_progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });



            }else{
                Toast.makeText(MainActivity.this, "Dados inválidos!", Toast.LENGTH_LONG).show();
                login_progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}