package com.samar.delivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public FirebaseAuth firebaseAuth;
    // Attribut pour stocker le résultat de la connexion
    private SigninResult signinResult = SigninResult.NONE;
    private EditText emailEditText, passwordEditText;
    private Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePreference = preferences.getString("theme_preference", "system");

        if ("system".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if ("light".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //setTheme(R.style.Theme_Delivery);
        } else if ("dark".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
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

    public void signInWithEmailAndPassword() {
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
    public void onSignin(String username, String password) {
        // Simuler la vérification des informations de connexion
        if (username.equals("valid_username") && password.equals("valid_password")) {
            signinResult = SigninResult.SUCCESS;
        } else {
            signinResult = SigninResult.FAILED;
        }
    }
    public SigninResult getSigninResult() {
        return signinResult;
    }

}


