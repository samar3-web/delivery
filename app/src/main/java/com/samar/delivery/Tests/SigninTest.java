package com.samar.delivery.Tests;


import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.samar.delivery.MainActivity;
import com.samar.delivery.SigninResult;

import org.junit.Test;

public class SigninTest {

    @Test
    public void testSuccessfulLogin() {
        // Créer une instance de MainActivity
        MainActivity activity = new MainActivity();

        // Configurer les données de test
        String email = "samarabbes@gmail.com";
        String password = "12345678";


        // Simuler le clic sur le bouton de connexion
        activity.firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Récupérer l'utilisateur connecté
                FirebaseUser user = authResult.getUser();

                // Vérifier que l'utilisateur est connecté
                assertNotNull(user);
            }
        });
    }
    @Test
    public void testInvalidCredentials() {
        // Given
        MainActivity activity = new MainActivity();
        String username = "invalid_username";
        String password = "invalid_password";

        // When
        activity.onSignin(username, password);

        // Then
        assertEquals(activity.getSigninResult(), SigninResult.FAILED);
    }
}