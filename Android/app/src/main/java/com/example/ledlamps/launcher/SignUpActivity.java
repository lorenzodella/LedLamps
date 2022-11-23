// Della Matera Lorenzo 5E

package com.example.ledlamps.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;
import com.example.ledlamps.R;
import com.example.ledlamps.user.User;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements
        View.OnClickListener,
        JsonHttpRequest.OnSyncReadyListener {

    EditText name, surname, mail, username, password, password_conf, idLamp;
    Button signup;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        name = findViewById(R.id.nome);
        name.requestFocus();
        surname = findViewById(R.id.cognome);
        mail = findViewById(R.id.mail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password_conf = findViewById(R.id.password_conf);
        idLamp = findViewById(R.id.idLamp);

        signup = findViewById(R.id.registrati);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String strNome = name.getText().toString();
        String strCognome = surname.getText().toString();
        String strMail = mail.getText().toString();
        String strUsername = username.getText().toString();
        String strPassword = password.getText().toString();
        String strPasswordConf = password_conf.getText().toString();
        String strIdLamp = idLamp.getText().toString().trim();

        if(strNome.trim().isEmpty()) {
            name.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strCognome.trim().isEmpty()) {
            surname.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strMail.trim().isEmpty()) {
            mail.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strUsername.trim().isEmpty()) {
            username.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strPassword.trim().isEmpty()) {
            password.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strIdLamp.isEmpty()) {
            idLamp.requestFocus();
            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
        else if(strIdLamp.length() != 40) {
            idLamp.requestFocus();
            Toast.makeText(getApplicationContext(), "IdLamp must be 40 characters long", Toast.LENGTH_SHORT).show();
        }
        else if(strPassword.length()<8) {
            password.requestFocus();
            Toast.makeText(getApplicationContext(), "Your password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }
        else if(!strPassword.equals(strPasswordConf)){
            password_conf.requestFocus();
            Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
        }
        else {
            signup.setText("");
            progress.setVisibility(View.VISIBLE);

            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(this, true);
            jsonHttpRequest.setOnSyncReadyListener(SignUpActivity.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/signup.php",
                    strNome,
                    strCognome,
                    strMail,
                    strUsername,
                    User.md5(strPassword),
                    strIdLamp
            );
        }
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        signup.setText("Signup");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Signed up successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onError(String error) {
        signup.setText("Signup");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }
}