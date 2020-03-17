package com.thewikky.wikkymaps.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExProvider1 extends SupportMapFragment implements OnMapReadyCallback,
                                                                GoogleMap.OnMapClickListener,
                                                                GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final String TAG = "ExProvider1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMapClickListener(this);//chama onMapClick

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Toast.makeText(getContext(), "Prov:"+provider, Toast.LENGTH_SHORT).show();

            mMap.setMyLocationEnabled(true);
        }catch (SecurityException ex){
            Log.e(TAG, "Error: "+ex);
            Toast.makeText(getContext(), "Erro:"+ex, Toast.LENGTH_SHORT).show();
        }

        LatLng ssa = new LatLng(-12.984139918479778, -38.49184412509203);
        MarkerOptions marker = new MarkerOptions();
        marker.position( ssa );
        marker.title("SSA");
        mMap.addMarker(marker);
        mMap.moveCamera( CameraUpdateFactory.newLatLng(ssa) );
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        Toast.makeText(getContext(), "Lat:"+latLng.latitude+"\nLng:"+latLng.longitude, Toast.LENGTH_SHORT).show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Criar Marcador");
        builder.setMessage("Deseja Criar um marcador nesta Posição?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callCreateMarker(latLng);
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
        return false;
    }


    private void callCreateMarker(LatLng latLng){
        final double lat = latLng.latitude;
        final double lon = latLng.longitude;
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
        builder2.setTitle("Criar Marcador");
        builder2.setMessage("Defina o Nome do Marcador");
        builder2.setView(input);
        builder2.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                createMarker(lat, lon, name);
            }
        });
        builder2.setNegativeButton("Cancelar", null);
        builder2.show();
    }
    private void createMarker(double latitude, double longitude, String nomeMarker) {
        MarkerOptions markerO = new MarkerOptions();
        LatLng newLL = new LatLng(latitude, longitude);
        markerO.position(newLL);
        markerO.title(nomeMarker);
        markerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerO);
        Toast.makeText(getContext(), "Marcador "+nomeMarker+" criado com Sucesso", Toast.LENGTH_SHORT).show();
    }
}
