package com.thewikky.wikkymaps.Fragment;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMapClickListener(this);//chama onMapClick

        LatLng ssa = new LatLng(-12.984139918479778, -38.49184412509203); //LatLng ssa = new LatLng(-12.98490408, -38.49120677);
        MarkerOptions marker = new MarkerOptions();
        marker.position( ssa );
        marker.title("SSA");
        mMap.addMarker(marker);
        mMap.moveCamera( CameraUpdateFactory.newLatLng(ssa) );
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(getContext(), "Lat:"+latLng.latitude+"\nLng:"+latLng.longitude, Toast.LENGTH_SHORT).show();
    }
}
