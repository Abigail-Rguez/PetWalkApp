package com.example.petwalk.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.petwalk.R;
import com.example.petwalk.models.Usuarios;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private Usuarios user = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (Usuarios) getIntent().getSerializableExtra("userCurrent");
        if( user.getUsertype() == 1 )
            setContentView(R.layout.fragment_profile_user);
        else
            setContentView(R.layout.fragment_gallery);

        Toolbar tool = findViewById(R.id.toolbar2);
        setSupportActionBar(tool);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/");

        byte[] decodedString = Base64.decode(user.getUserphoto(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ImageView iv = findViewById(R.id.profile_image);
        iv.setImageBitmap(decodedByte);
        ((EditText)findViewById(R.id.show_phone)).setText(user.getUserphone());
        ((EditText)findViewById(R.id.show_mail)).setText(user.getUsermail());
        ((TextView)findViewById(R.id.textViewName)).setText(user.getUsername());
        EditText about = findViewById(R.id.about_me);
        if(about != null)
            about.setText(user.getAboutme());

        Button btnSave = findViewById(R.id.button2);
        if( btnSave != null ) {
            btnSave.setOnClickListener(v -> {

                progressBar.setVisibility(View.VISIBLE);

                EditText helpEdit = findViewById(R.id.show_phone);
                user.setUserphone(helpEdit.getText().toString());
                helpEdit = findViewById(R.id.show_mail);
                user.setUsermail(helpEdit.getText().toString());
                DatabaseReference child = databaseReference.child("Users");

                Map<String, Object> person_update = new HashMap<>();
                person_update.put(user.getUserid(), user);
                child.updateChildren(person_update)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_LONG).show();
                        });

            });
        }

        Button btnProv = findViewById(R.id.btnSaveProv);
        if( btnProv != null ) {
            btnProv.setOnClickListener(osl -> {
                EditText helpEdit = findViewById(R.id.show_phone);
                user.setUserphone(helpEdit.getText().toString());
                helpEdit = findViewById(R.id.show_mail);
                user.setUsermail(helpEdit.getText().toString());
                helpEdit = findViewById(R.id.about_me);
                user.setAboutme(helpEdit.getText().toString());

                DatabaseReference child = databaseReference.child("Users");

                Map<String, Object> person_update = new HashMap<>();
                person_update.put(user.getUserid(), user);
                child.updateChildren(person_update)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_LONG).show();
                        });
            });
        }

    }

}

