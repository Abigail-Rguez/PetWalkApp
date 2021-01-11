package com.example.petwalk.ui.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petwalk.R;
import com.example.petwalk.models.PetModel;
import com.example.petwalk.models.Usuarios;
import com.example.petwalk.models.WalksModel;
import com.example.petwalk.utilerias.SessionManager;
import com.example.petwalk.utilerias.ShakeDetector;
import com.example.petwalk.utilerias.ShakeService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServiceFragment extends Fragment {

    private Usuarios user;
    private ArrayList<Usuarios> list;
    private AdaptadorUsuarios rAdapter = null;
    private FirebaseDatabase fd;
    private SessionManager sm;

    private SensorManager mSensorManager;
    private Sensor mAccelometer;
    private ShakeDetector mShakeDetector;
    private DatabaseReference dr, drWalk, drInfoWalk;
    private ValueEventListener event = null;
    private ValueEventListener eventWalk = null, eventInfoWalk = null;
    private WalksModel walksModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);
        Intent intent = new Intent(getContext(), ShakeService.class);
        getActivity().startService(intent);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fd = FirebaseDatabase.getInstance();

        dr = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Users");
        drInfoWalk = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Users");
        drWalk = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Walks");


        user = (Usuarios) getActivity().getIntent().getSerializableExtra("user");
        sm = new SessionManager(getContext());

        mSensorManager = (SensorManager) getActivity().getSystemService( Context.SENSOR_SERVICE );
        mAccelometer = mSensorManager
                .getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> {
            if (!sm.checkServices())
                getWalkerData();
            else
                getInfoWalks();
        });
    }

    private void getWalkerData() {
        if( event != null ){
            dr.removeEventListener(event);
        }else {
            fd = FirebaseDatabase.getInstance();

            event = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Usuarios usuario = ds.getValue(Usuarios.class);
                        if (usuario.getUsertype() == 2) {
                            list.add(usuario);
                        }
                    }
                    ListView listaPets = getView().findViewById(R.id.list_services);
                    rAdapter = new AdaptadorUsuarios(getContext());
                    listaPets.setAdapter(rAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            dr.addValueEventListener(event);
        }

    }

    private void getInfoWalks() {
        if( event != null ){
            drWalk.removeEventListener(event);
        }else {
            fd = FirebaseDatabase.getInstance();
            eventWalk = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        WalksModel walk = ds.getValue(WalksModel.class);
                        if (walk.getIdDuenio().equals( user.getUserid() ) && walk.getState() == 1 ) {
                            Toast.makeText( getContext(), walk.getIdPaseador(), Toast.LENGTH_SHORT).show();
                            walksModel = walk;
                            break;
                        }
                    }

                    if( walksModel != null ){

                        eventInfoWalk = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                list = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Usuarios usuario = ds.getValue( Usuarios.class );
                                    if( usuario.getUserid().equals( walksModel.getIdPaseador() ) ){
                                        list.add( usuario );
                                    }
                                }
                                ListView listaPets = getView().findViewById(R.id.list_services);
                                rAdapter = new AdaptadorUsuarios(getContext());
                                listaPets.setAdapter(rAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };

                        drInfoWalk.addValueEventListener( eventInfoWalk );

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            drWalk.addValueEventListener(eventWalk);
        }

    }

    class AdaptadorUsuarios extends ArrayAdapter {

        Context _context;
        AdaptadorUsuarios( Context context ){
            super(context, R.layout.pets_info, list);
            _context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View item = inflater.inflate( R.layout.info_prov, parent, false );

            byte[] decodedString = Base64.decode(list.get(position).getUserphoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView iv = item.findViewById(R.id.profile_img_prov);
            iv.setImageBitmap(decodedByte);

            TextView textView1 = item.findViewById( R.id.full_name_prov );
            textView1.setText(list.get(position).getUsername() );
            textView1 = item.findViewById( R.id.price_prov );
            textView1.setText(list.get(position).getPrice() );
            textView1 = item.findViewById( R.id.about_prov );
            textView1.setText(list.get(position).getAboutme() );
            Button botonAceptar = item.findViewById(R.id.btn_acept_prov);
            if( sm.checkServices() ){ botonAceptar.setText("Finalizar"); }
            botonAceptar.setOnClickListener(v -> {
                if( !sm.checkServices() ) {
                    DatabaseReference dr = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Pets");
                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PetModel pet = null;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                pet = ds.getValue(PetModel.class);
                                if (pet.getDuenio().equals(user.getUserid())) {
                                    break;
                                }
                            }
                            WalksModel walk = new WalksModel();

                            walk.setIdPaseador(list.get(position).getUserid());
                            walk.setIdPet(pet.getPet_id());
                            Date fecha = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            walk.setFecha(formatter.format(fecha));
                            walk.setTotal(list.get(position).getPrice());
                            walk.setState(1);

                            DatabaseReference walks = fd.getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Walks");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            String idWalk = walks.push().getKey();
                            walk.setIdWalk(idWalk);
                            walk.setIdDuenio(user.getUserid());
                            hashMap.put(idWalk, walk);

                            walks.updateChildren(hashMap).addOnSuccessListener( v -> {
                                sm.enableService();
                                getInfoWalks();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    walksModel.setState( 2 );
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put( walksModel.getIdWalk(), walksModel );
                    drWalk.updateChildren( hashMap ).addOnSuccessListener(v2 -> {
                        sm.disableService();
                        getWalkerData();
                    });
                }

            });
            return item;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener( mShakeDetector, mAccelometer, SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.registerListener( mShakeDetector, mAccelometer, SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(mShakeDetector);
    }


}
