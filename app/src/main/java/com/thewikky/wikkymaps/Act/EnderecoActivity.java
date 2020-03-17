package com.thewikky.wikkymaps.Act;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thewikky.wikkymaps.R;

import java.io.IOException;
import java.util.List;

public class EnderecoActivity extends AppCompatActivity {

    private Button btnVoltarPrincipalE;
    private TextView txtLatitude, txtLongitude, txtEnderecoE, txtCepE, txtBairroE, txtCidade, txtEstado, txtPais;
    private Location location;
    private LocationManager locationManager;
    private double latitude, longitude;
    private Address endereco;
    private final int REQUEST_CODE = 37;
    private int form;
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        setTitle("Endereço");

        btnVoltarPrincipalE = (Button)   findViewById(R.id.btnVoltarPrincipalE);
        txtLatitude         = (TextView) findViewById(R.id.txtLatitudeE);
        txtLongitude        = (TextView) findViewById(R.id.txtLongitudeE);
        txtEnderecoE        = (TextView) findViewById(R.id.txtEnderecoE);
        txtCepE             = (TextView) findViewById(R.id.txtCepE);
        txtBairroE          = (TextView) findViewById(R.id.txtBairroE);
        txtCidade           = (TextView) findViewById(R.id.txtCidadeE);
        txtEstado           = (TextView) findViewById(R.id.txtEstadoE);
        txtPais             = (TextView) findViewById(R.id.txtPaisE);
        form                = 0;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(EnderecoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //Toast.makeText(this, "Llat"+latitude, Toast.LENGTH_SHORT).show();
        }

        it = getIntent();
        form = it.getIntExtra("form", 0);

        if(form==1 || form==11){//vindo do MarkerActivity(1) vindo do ExProvider clickMarcker(11)
            latitude  = it.getDoubleExtra("lat", -12.86946757276573 );
            longitude = it.getDoubleExtra("lon", -38.42561401426792);
            setTitle("Endereço " + it.getStringExtra("name"));
        }else if(form==2){//vindo da ActPrincipal
            setTitle("Endereço da Localiação");
            latitude  = it.getDoubleExtra("latp", -12.86946757276573 );
            longitude = it.getDoubleExtra("lonp", -38.42561401426792);
        }
        else {
            setTitle("Meu Endereço");
            /*latitude  = -12.869555846667673;
            longitude = -38.42561401426792;*/
            if ( myEndereco() ) {
                Toast.makeText(this, "Localização Encontrada.", Toast.LENGTH_SHORT).show();
            }else{
                latitude  =  40.748817;
                longitude = -73.985428;
                Toast.makeText(this, "Verifique se o GPS está Ativado.\nOu você está no Empire State Building?", Toast.LENGTH_SHORT).show();
            }
        }

        try{
            endereco = buscarEndereco(latitude, longitude);
            txtLatitude.setText ( "Latitude...: "  + latitude );
            txtLongitude.setText( "Longitude: " + longitude );
            txtEnderecoE.setText( "Endereço: " + endereco.getAddressLine(0) );
            txtCepE.setText     ( "C.E.P.: " + endereco.getPostalCode() );
            txtBairroE.setText  ( "Bairro: "    + endereco.getSubLocality() );
            txtCidade.setText   ( "Cidade: "    + endereco.getSubAdminArea() );
            txtEstado.setText   ( "Estado: "    + endereco.getAdminArea() );
            txtPais.setText     ( "País: "      + endereco.getCountryName() + " | " + endereco.getCountryCode());
            /*
            getAddressLine -> endereço
            getPostalCode -> C.E.P do endereço
            getSubLocality -> bairro
            getLocaly -> cidade
            getSubAdminArea - > geralmente cidade tbm
            getAdminArea -> estado
            getCountryName -> país
            getCountryCode -> sigla do País
            getFeatureName -> informações complementares, como numero da residencia
            getSubThoroughfare - > tbm pode ser o numero da residencia
             */
        }catch (IOException ex) {
            Log.i("GPS", ex.getMessage());
        }

        btnVoltarPrincipalE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(form==1){
                    Intent it = new Intent(EnderecoActivity.this, MarkerActivity.class);
                    finish();
                    startActivity(it);
                }
                else{ //if(form==2 || form==11)
                    Intent it = new Intent(EnderecoActivity.this, ActPrincipal.class);
                    finish();
                    startActivity(it);
                }
            }
        });
    }

    private Address buscarEndereco(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses.size() > 0){
            address = addresses.get(0);
        }else{
            Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_endereco, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_endereco_locAtual:
                myEndereco();
                break;
            case R.id.action_endereco_locMarc:
                //marcEndereco();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean myEndereco(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(EnderecoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //Toast.makeText(this, "Llat"+latitude, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Sua localização não foi Encontrada", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            endereco = buscarEndereco(latitude, longitude);
            txtLatitude.setText("Latitude...: " + latitude);
            txtLongitude.setText("Longitude: " + longitude);
            txtEnderecoE.setText("Endereço: " + endereco.getAddressLine(0));
            txtCepE.setText("C.E.P.: " + endereco.getPostalCode());
            txtBairroE.setText("Bairro: " + endereco.getSubLocality());
            txtCidade.setText("Cidade: " + endereco.getSubAdminArea());
            txtEstado.setText("Estado: " + endereco.getAdminArea());
            txtPais.setText("País: " + endereco.getCountryName() + " | " + endereco.getCountryCode());
        }catch (IOException ex) {
            Log.i("GPS", ex.getMessage());
        }
        return true;
    }

    private void marcEndereco(){
        if(form==1){
            latitude  = it.getDoubleExtra("lat", -12.86946757276573 );
            longitude = it.getDoubleExtra("lon", -38.42561401426792);
        } else {
            latitude  = -12.869555846667673;
            longitude = -38.42561401426792;
        }
        try{
            endereco = buscarEndereco(latitude, longitude);
            txtLatitude.setText ( "Latitude...: "  + latitude );
            txtLongitude.setText( "Longitude: " + longitude );
            txtEnderecoE.setText( "Endereço: " + endereco.getAddressLine(0) );
            txtCepE.setText     ( "C.E.P.: " + endereco.getPostalCode() );
            txtBairroE.setText  ( "Bairro: "    + endereco.getSubLocality() );
            txtCidade.setText   ( "Cidade: "    + endereco.getSubAdminArea() );
            txtEstado.setText   ( "Estado: "    + endereco.getAdminArea() );
            txtPais.setText     ( "País: "      + endereco.getCountryName() + " | " + endereco.getCountryCode());
        }catch (IOException ex) {
            Log.i("GPS", ex.getMessage());
        }
    }
}
