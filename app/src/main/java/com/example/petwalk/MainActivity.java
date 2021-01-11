package com.example.petwalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petwalk.models.Usuarios;
import com.example.petwalk.ui.Registro;
import com.example.petwalk.utilerias.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText editText;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = new SessionManager(getApplicationContext());
        sm.checkLogin();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase
                .getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/")
                .child("Users");

        Button btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(this);

        Button btnLog = findViewById(R.id.btnLog);
        btnLog.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent action_intent = null;
        switch (v.getId()) {
            case R.id.btnReg:
                action_intent = new Intent(this, Registro.class);
                startActivity(action_intent);
                break;
            case R.id.btnLog:
                login();
                break;
        }
    }

    private void login() {

        String user = ((EditText)findViewById(R.id.userText)).getText().toString();
        String pass = ((EditText)findViewById(R.id.passText)).getText().toString();

        if( user.equals("") || pass.equals("") ){
            Toast.makeText(this, "Correo o contraseña incorrectas", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            ValueEventListener value = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        Usuarios currentUser = data.getValue(Usuarios.class);
                                        if (currentUser.getUsermail().equals(user)) {

                                            Intent intent = new Intent(MainActivity.this, Home.class);
                                            intent.putExtra("user", currentUser);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            sm.createLoginSession(user);
                                            startActivity(intent);

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            databaseReference.addValueEventListener(value);
                        } else {

                            Toast.makeText(this, "Problemas al iniciar", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }
}
