package com.example.petwalk.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.petwalk.R;
import com.example.petwalk.models.Usuarios;
import com.example.petwalk.utilerias.LatLngInterpolator;
import com.example.petwalk.utilerias.MarkerAnimation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener,GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final int ROUND = 10;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Marker mCurrLocationMarker;
    private boolean firstTimeFlag = true;
    private GoogleMap mMap;
    public GoogleApiClient googleApiClient;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;
    private MarkerOptions mp;
    private Usuarios user;
    private static final int REQUEST_CODE = 101;

    private ValueEventListener event;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        user = (Usuarios) getActivity().getIntent().getSerializableExtra("user");

        SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.map);
        mUsers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://petwalk-cb147-default-rtdb.firebaseio.com/").child("Users");
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.google_style));
            if (!success) {
                // Handle map style load failure
                Log.e("map_style","map style updated please do check it");
            }
        } catch (Resources.NotFoundException e) {
            // Oops, looks like the map style resource couldn't be found!
            Log.e("map_style","map is not updated yet ... do some other stuff");
        }

        mMap = googleMap;
            mMap.setOnMarkerClickListener(this);
            mMap.setMapType(R.raw.google_style);

            if( event != null )
                mUsers.removeEventListener(event);
            else {
                event = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            Usuarios usuario = s.getValue(Usuarios.class);
                            if (!usuario.getUserid().equals(user.getUserid()) && usuario.getUsertype() == 2) {
                                LatLng location = new LatLng(usuario.getLatitude(), usuario.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(usuario.getUsername())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                );
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mUsers.addValueEventListener(event);
            }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            startCurrentLocationUpdates();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(getContext(), "Google Play Services isn't installed", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void startCurrentLocationUpdates() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        fetchLocation();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && mMap != null) {
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
            if( user.getUsertype() == 1 )
                showMarkers();
                user.setLongitude( currentLocation.getLongitude() );
                user.setLatitude( currentLocation.getLatitude() );
            if( user.getUsertype() == 2 )
                updateLocation(currentLocation);
        }
    };

    private void updateLocation(Location currentLocation) {
        user.setLatitude( currentLocation.getLatitude() );
        user.setLongitude( currentLocation.getLongitude() );
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( user.getUserid(), user );
        mUsers.updateChildren( hashMap );
    }

    private void showMarkers() {
    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Bundle posicion = getArguments();
        if (posicion != null && mMap != null) {
            mp = new MarkerOptions();
            mp.position(new LatLng(posicion.getDouble("latitude"), posicion.getDouble("longitude")));
            mp.title(posicion.getString("name"));
            mMap.addMarker(mp);
        }
        if (mCurrLocationMarker == null) {
            mCurrLocationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            MarkerAnimation.animateMarkerToGB(mCurrLocationMarker, latLng, new LatLngInterpolator.Spherical());
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

}