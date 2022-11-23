// Della Matera Lorenzo 5E

package com.example.ledlamps.launcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ledlamps.utils.JsonHttpRequest;
import com.example.ledlamps.utils.LedLampsUtils;
import com.example.ledlamps.main.MainActivity;
import com.example.ledlamps.R;
import com.example.ledlamps.user.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements JsonHttpRequest.OnSyncReadyListener {

    EditText username, password;
    TextView registrati;
    Button accedi;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        ImageView icon = findViewById(R.id.icon);
        icon.setClipToOutline(true);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        accedi = findViewById(R.id.accedi);
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = username.getText().toString();
                String strPassword = password.getText().toString();
                checkUtente(strUsername, User.md5(strPassword));
            }
        });

        registrati = findViewById(R.id.registrati);
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrati.setTypeface(registrati.getTypeface(), Typeface.BOLD);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registrati.setTypeface(Typeface.DEFAULT);
                    }
                }, 150);
                if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
                    Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You must be connected to cloud to signup", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUtente(String strUsername, String strPassword) {
        if(!LedLampsUtils.getSystemSsid().equals("\"LedLamps\"")) {
            accedi.setText("");
            progress.setVisibility(View.VISIBLE);
            JsonHttpRequest jsonHttpRequest = new JsonHttpRequest(this, true);
            jsonHttpRequest.setOnSyncReadyListener(LoginActivity.this);
            jsonHttpRequest.execute(LedLampsUtils.getHost()+"/login.php?details=true", strUsername, strPassword);
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to cloud to login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSyncReady(JSONObject jobj) {
        try {
            User.setUser(
                    jobj.getInt("idUser"),
                    jobj.getString("username"),
                    jobj.getString("password"),
                    jobj.getString("name"),
                    jobj.getString("surname"),
                    jobj.getString("mail"),
                    jobj.getString("idLamp")
            );
            SharedPreferences sharedPref = getSharedPreferences ("user", 0);
            SharedPreferences.Editor editor = sharedPref.edit ();
            editor.putInt ("idUser", User.getId());
            editor.putString ("username", User.getUsername());
            editor.putString ("password", User.getPassword());
            editor.putString ("name", User.getName());
            editor.putString ("surname", User.getSurname());
            editor.putString ("mail", User.getMail());
            editor.putString ("idLamp", User.getIdLamp());
            editor.apply();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    accedi.setText("Login");
                    progress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Welcome "+User.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        } catch (JSONException e){
            e.printStackTrace();
            onError("JSONException");
        }
    }
    @Override
    public void onError(String error) {
        accedi.setText("Login");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "username o password errati", Toast.LENGTH_SHORT).show();
    }
}