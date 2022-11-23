// Della Matera Lorenzo 5E

package com.example.ledlamps.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ledlamps.R;
import com.example.ledlamps.launcher.LoginActivity;

public class UserActivity extends AppCompatActivity {

    private Button exit;
    private ImageView icon;
    private EditText username, name, surname, mail, idLamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        username.setText(User.getUsername());
        name = findViewById(R.id.nome);
        name.setText(User.getName());
        surname = findViewById(R.id.cognome);
        surname.setText(User.getSurname());
        mail = findViewById(R.id.mail);
        mail.setText(User.getMail());
        idLamp = findViewById(R.id.idLamp);
        idLamp.setText(String.valueOf(User.getIdLamp()));

        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
    }

    private void exit(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("user", 0).edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        finishAffinity();
                        startActivity(intent);
                    }
                }).show();
    }
}