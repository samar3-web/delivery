package com.samar.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private EditText emailEditText, passwordEditText;
    private Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));
        // Initialiser FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            Toast.makeText(getApplicationContext(), currentUser.getEmail() + " is connected", Toast.LENGTH_LONG).show();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            finish();
        }

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signInButton = findViewById(R.id.cirLoginButton);

        signInButton.setOnClickListener(view -> signInWithEmailAndPassword());
    }

    private void signInWithEmailAndPassword() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Vérifier si les champs sont vides
        if (email.isEmpty()) {
            emailEditText.setError("Veuillez entrer votre adresse e-mail");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Veuillez entrer votre mot de passe");
            return;
        }

        // Connexion de l'utilisateur avec l'adresse e-mail et le mot de passe
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Connexion réussie
                        Log.d("SignInActivity", "signInWithEmailAndPassword:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        // Naviguer vers l'écran principal de votre application
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Échec de la connexion
                        Log.w("SignInActivity", "signInWithEmailAndPassword:failure", task.getException());
                        // Afficher un message d'erreur à l'utilisateur
                        Toast.makeText(MainActivity.this, "Échec de la connexion. Vérifiez vos informations d'identification.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

