package com.thewikky.wikkymaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thewikky.wikkymaps.Act.EnderecoActivity;
import com.thewikky.wikkymaps.Act.LoginActivity;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;

public class MainActivity extends AppCompatActivity {

    private Button btnAbrirLogin;
    private ImageView imgMapInicial;
    private FirebaseAuth auth;

    final int REQUEST_PERMISSION_CODE = 37;
    final int  CAMERA_PERMISSION_CODE = 38;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = ConexaoFirebase.getAuth();
        auth.signOut();

        btnAbrirLogin = (Button)findViewById(R.id.btnAbrirLogin);
        imgMapInicial = (ImageView)findViewById(R.id.imgMapInicial);

        btnAbrirLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirLogin();
            }
        });

    }//fim onCreate

    private void abrirLogin() {
        Intent it = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(it);
    }

    private void callPermissions(){
        //Permission Location
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("Esta Permissão é Necessária para funcionamento eficaz do Aplicativo.\nFavor, Permitir.", new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
            }
        }else{
            Toast.makeText(this, "Localização pronta", Toast.LENGTH_SHORT).show();
        }

        //Permission Camera
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                callDialog("Esta Permissão é Necessária para funcionamento eficaz do Aplicativo.\nFavor, Permitir.", new String[] {Manifest.permission.CAMERA});
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        }else{
            Toast.makeText(this, "Câmera pronta", Toast.LENGTH_SHORT).show();
        }
    }

    private void callDialog(String msg, final String[] permissions){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Permission");
        b.setMessage(msg);
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSION_CODE);
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.show();
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão não foi concedida. Aplicativo não funcionará eficaz.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                for(int i = 0; i < permissions.length; i++){
                    if ( permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permissão para Localização concecida", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Permissão não foi concedida. Aplicativo não poderá ser Inicializado.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
            case CAMERA_PERMISSION_CODE:
                for(int i = 0; i < permissions.length; i++){
                    if ( permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permissão para Câmera Concedida", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
