package com.thewikky.wikkymaps.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Act.ActPrincipal;
import com.thewikky.wikkymaps.Act.EnderecoActivity;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Model.LocationU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class ExProvider2 extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final String TAG = "ExProvider2";
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener valueEventListener;
    private ArrayList<LocationU> locsM;
    private double getLat, getLon;
    private int form;
    private DrawerLayout drawerLayoutPrincipal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);

        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        locsM = new ArrayList<>();

        String uid = user.getUid();
        reference = ConexaoFirebase.getDatabase().child("usuario").child(uid).child("marker");
        callMakers();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled){
            Toast.makeText(getContext(), "GPS Desabilitado. Deve Habilitá-lo", Toast.LENGTH_SHORT).show();
        }
        form = 0;
        Bundle bd = this.getArguments();
        if(bd != null){
            Toast.makeText(getContext(), "Contains", Toast.LENGTH_SHORT).show();
            getLat = bd.getDouble("latf", 0.0);
            getLon = bd.getDouble("lonf", 0.0);
            form = bd.getInt("formf", 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, this);
        }catch (SecurityException e){
            Toast.makeText(getContext(), "Permissão para Localização não Concedida.\nAplicativo não funcional", Toast.LENGTH_SHORT).show();
            }
        Toast.makeText(getContext(), "Resume", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        Toast.makeText(getContext(), "Pause", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(getContext(), "Loc. Alterada", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(getContext(), "Status alterado", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getContext(), "Habilitado", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(getContext(), "Desabilitado", Toast.LENGTH_LONG).show();
        Toast.makeText(getContext(), "É necessário ter o GPS Ativo para que funcione completamente.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMapClickListener(this);//chama onMapClick

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//          Toast.makeText(getContext(), "Prov:"+provider, Toast.LENGTH_SHORT).show();

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
        mMap.setOnMarkerClickListener(this);

        if(form==1){
            LatLng gMarker = new LatLng(getLat, getLon);
            float zoom = 15.5f;
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(gMarker, zoom) );
        }
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
        builder.setNeutralButton("Endereço", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callEndereço(latLng);
            }
        });
        builder.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final String nameM = marker.getTitle();
        final LatLng ll = marker.getPosition();
        double lat = ll.latitude;
        double lon = ll.longitude;
        /*Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), lat+"\n"+lon, Toast.LENGTH_SHORT).show();*/

        for(int i = 0; i < locsM.size(); i++){
            if( locsM.get(i).getNome().equalsIgnoreCase(nameM) ){
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("Marcador");
                    StringBuilder sB = new StringBuilder();
                    sB.append("Nome........: "+nameM+"\n\n");
                    sB.append("Latitude....: "+lat+"\n");
                    sB.append("Longitude.: "  +lon+"\n");
                    sB.append("\n•Buscar Endereço deste Marcador?");
                b.setMessage(sB.toString());
                b.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callEndereçoM(ll, nameM);
                    }
                });
                b.setNegativeButton("Não", null);
                b.show();
                break;
            }
        }

        return false;
    }

    private void callCreateMarker(LatLng latLng){
        final String lat = String.valueOf( latLng.latitude );
        final String lon = String.valueOf( latLng.longitude);

        final EditText input = new EditText( getContext() );
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setHint("Nome do Marcador");
        input.setFilters( new InputFilter[] {new InputFilter.LengthFilter(25)} );

        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
        builder2.setTitle("Criar Marcador");
        builder2.setMessage("Defina o Nome do Marcador");
        builder2.setView(input);
        builder2.setIcon(R.drawable.addlocation18);
        builder2.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString().trim();

                if( name.contains(".") || name.contains("#") || name.contains("$") ||
                    name.contains("[") || name.contains("]") || name.contains("/") ){
                    Toast.makeText(getContext(), "Não pode conter estes caracteres: . , #, $, [, ], /", Toast.LENGTH_SHORT).show();
                }else{
                    createMarker(Double.parseDouble(lat), Double.parseDouble(lon), name);
                    LocationU loc = new LocationU();
                    loc.setNome(name);
                    loc.setLatitude(lat);
                    loc.setLongitude(lon);
                    loc.save(user.getUid());
                }
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

        Toast.makeText(getContext(), "Marcador "+markerO.getTitle()+" criado com Sucesso", Toast.LENGTH_SHORT).show();
    }

    private void callMakers(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locsM.clear();
                for ( DataSnapshot dados : dataSnapshot.getChildren() ) {
                    LocationU lU = dados.getValue(LocationU.class);
                    final double lati  =  Double.parseDouble( lU.getLatitude().trim() );
                    final double longi =  Double.parseDouble( lU.getLongitude().trim() );
                    String nomee = lU.getNome();
                    createMarker2(lati, longi, nomee);
                    locsM.add(lU);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //Toast.makeText(getContext(), "Marcadores carregados.", Toast.LENGTH_SHORT).show();
    }
    private void createMarker2(double latitude, double longitude, String nomeMarker) {
        MarkerOptions markerO = new MarkerOptions();
        LatLng newLL = new LatLng(latitude, longitude);
        markerO.position(newLL);
        markerO.title(nomeMarker);
        markerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerO);
    }

    private void callEndereço(LatLng latLng){
        Double lat = latLng.latitude;
        Double lon = latLng.longitude;
        Intent it = new Intent(getContext(), EnderecoActivity.class);
        it.putExtra("latp", lat);
        it.putExtra("lonp", lon);
        it.putExtra("form", 2);
        startActivity(it);
    }

    private void callEndereçoM(LatLng latLng, String nName){
        Double lat = latLng.latitude;
        Double lon = latLng.longitude;
        Intent it = new Intent(getContext(), EnderecoActivity.class);
        it.putExtra("form", 11);
        it.putExtra("lat", lat);
        it.putExtra("lon", lon);
        it.putExtra("name", nName);
        startActivity(it);
    }
}
