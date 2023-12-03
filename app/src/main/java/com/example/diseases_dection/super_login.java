package com.example.diseases_dection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class superuser extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText user_name = findViewById(R.id.editText_username);
        EditText password = findViewById(R.id.editText_pwd);
        Button login_btn = findViewById(R.id.button_login);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(user_name.getText().toString(), password.getText().toString());
            }
        });
    }

    public void login(String user_name, String password) {
        if (user_name.equals("admin") && password.equals("12345")) {
            Intent intent = new Intent(superuser.this, ViewImagesActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(superuser.this, "Wrong Credential", Toast.LENGTH_SHORT).show();
        }
    }
}


