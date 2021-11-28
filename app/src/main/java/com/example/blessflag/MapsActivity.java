package com.example.blessflag;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.blessflag.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng mexico = new LatLng(19.3158076, -99.5936336);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Los Hachos").snippet("Hamburguesería").icon(BitmapDescriptorFactory.fromResource(R.drawable.res5)));


        LatLng l2 = new LatLng(19.310121535615977, -99.59985504434815);
        mMap.addMarker(new MarkerOptions().position(l2).title("A la Burger").snippet("Hamburguesería").icon(BitmapDescriptorFactory.fromResource(R.drawable.res2)));


        LatLng l3 = new LatLng(19.30745320534203, -99.5942991339911);
        mMap.addMarker(new MarkerOptions().position(l3).title("Los Cuates").snippet("Hamburguesería").icon(BitmapDescriptorFactory.fromResource(R.drawable.res3)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico,15));
    }
}