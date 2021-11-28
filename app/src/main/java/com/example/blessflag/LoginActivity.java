package com.example.blessflag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private EditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginbutton);
        user = findViewById(R.id.etxtname);
        pass = findViewById(R.id.etxtpass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity();
            }
        });

    }

    private void openActivity() {
        if (user.getText().toString().equals("Arturo") && pass.getText().toString().equals("DM3p2021")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Usuario o Contraseña incorrectos, intenta nuevamente", Toast.LENGTH_LONG).show();
        }

    }
}