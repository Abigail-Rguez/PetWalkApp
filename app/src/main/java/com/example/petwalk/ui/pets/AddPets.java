package com.example.petwalk.ui.pets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.petwalk.R;
import com.example.petwalk.models.PetModel;
import com.example.petwalk.models.Usuarios;
import com.example.petwalk.ui.Registro;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddPets extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageButton profile = null;
    private Usuarios user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_pet);

        profile = findViewById(R.id.imageButton);

        user = (Usuarios) getIntent().getSerializableExtra("usuario");

        Toolbar mytool = findViewById(R.id.my_toolbar);
        setSupportActionBar(mytool);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Spinner spinner = findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this,R.array.razas_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner2 = findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
                .createFromResource(this,R.array.sizes_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        Button btn = findViewById(R.id.btnAddPet);
        btn.setOnClickListener(v -> {
            String[] datos = new String[5];
            datos[0] = ((Spinner) findViewById(R.id.spinner1)).getSelectedItem().toString();
            datos[1] = ((Spinner) findViewById(R.id.spinner2)).getSelectedItem().toString();
            datos[2] = ((EditText)findViewById(R.id.edit_name)).getText().toString();
            datos[3] = ((EditText)findViewById(R.id.edit_age)).getText().toString();
            datos[4] = ((EditText)findViewById(R.id.edit_color)).getText().toString();

            int band = 0;
            for (String dato: datos) {
                if( dato.equals("") ) band++;
            }
            if( band != 0 ){
                Toast.makeText(getApplicationContext(), "Debes llenar todos los campos", Toast.LENGTH_SHORT);
            }else{
                PetModel pet = new PetModel();

                pet.setPet_age( datos[3] );
                pet.setPet_color(datos[4]);
                pet.setPet_name(datos[2]);
                pet.setPet_raza(datos[0]);
                pet.setPet_size(datos[1]);
                Bitmap image = ((BitmapDrawable) profile.getDrawable()).getBitmap();
                pet.setPet_photo(Registro.encode_save_Image(image));

                savePet(pet);

            }

        });

    }

    private void savePet(PetModel pet) {

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Pets");

        String idPet = dr.push().getKey();
        pet.setPet_id( idPet );

        pet.setDuenio( user.getUserid() );

        HashMap<String, Object> hm = new HashMap<>();
        hm.put(idPet, pet);

        dr.updateChildren(hm)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Agregado exitosamente", Toast.LENGTH_LONG);
                    finish();
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profile.setImageBitmap(imageBitmap);
        }
    }

}
