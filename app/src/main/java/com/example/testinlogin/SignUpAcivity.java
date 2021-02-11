package com.example.testinlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SignUpAcivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView login_textView;
    private EditText editTextEmail, editTextPassword;
    private Button signUp_Button;
    private ProgressBar sign_up_progressBar;
    private List<QueryDocumentSnapshot> academicCommunityMembers;
    private FirebaseFirestore mStore;
    private String school, name, type_of_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mStore  = FirebaseFirestore.getInstance();

        academicCommunityMembers = this.getAcademicCommunityMembers();
        editTextEmail = findViewById(R.id.sign_up_email_editText);
        editTextPassword = findViewById(R.id.sign_up_password_editText);
        signUp_Button = findViewById(R.id.signup_button);
        login_textView = findViewById(R.id.login_textView);
        sign_up_progressBar = findViewById(R.id.sign_up_progress_bar);

        signUp_Button.setOnClickListener(this);
        login_textView.setOnClickListener(this);

    }

    private List<QueryDocumentSnapshot> getAcademicCommunityMembers() {
        List<QueryDocumentSnapshot> list = new ArrayList<>();

        mStore.collection("membrosIPBeja")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document);
                            }
                        } else {
                            //Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return list;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signup_button){
            registerUser();
        }
        else {
            if(v.getId() == R.id.login_textView){
                Intent intent = new Intent(SignUpAcivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void registerUser() {



        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

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

        sign_up_progressBar.setVisibility(View.VISIBLE);

        if(userBelongsToAcademicCommunity(email)){
            createFireBaseUser(email, password);
        }else {
            Toast.makeText(this, "Utilizador inválido! Insira novamente!", Toast.LENGTH_SHORT).show();
            sign_up_progressBar.setVisibility(View.INVISIBLE);
        }


    }

    private void createFireBaseUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    User user = new User(email, school, name, type_of_user);

                    mStore.collection("users").document(mAuth.getCurrentUser().getUid()).set(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpAcivity.this, "Usuário foi registado!", Toast.LENGTH_SHORT).show();
                                sign_up_progressBar.setVisibility(View.INVISIBLE);

                                if(type_of_user.equals("Administrador")){
                                    Intent intent = new Intent(SignUpAcivity.this, AdminHomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(SignUpAcivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {
                                Toast.makeText(SignUpAcivity.this, "Erro ao registar usuário!", Toast.LENGTH_SHORT).show();
                                sign_up_progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignUpAcivity.this, "Erro ao registar utilizador!", Toast.LENGTH_SHORT).show();
                    sign_up_progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private boolean userBelongsToAcademicCommunity(String email) {

        for (int i=0; i < academicCommunityMembers.size(); i++){
            String academicUserEmail = academicCommunityMembers.get(i).get("email").toString();
            if(email.equals(academicUserEmail)){
                school = academicCommunityMembers.get(i).get("escola").toString();
                name = academicCommunityMembers.get(i).get("nome").toString();
                type_of_user = academicCommunityMembers.get(i).get("tipo_utilizador").toString();
                return true;
            }
        }

        return false;
    }
}