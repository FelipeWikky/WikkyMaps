package com.thewikky.wikkymaps.Act;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Fragment.ExProvider1;
import com.thewikky.wikkymaps.Fragment.ExProvider2;
import com.thewikky.wikkymaps.Fragment.MapsFragment;
import com.thewikky.wikkymaps.Model.DadosU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class ActPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    TextView txtNavHeaderNome, txtNavHeaderEmail, txtNavHeaderUid;
    ImageView imgNavHeader;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private DrawerLayout drawerLayoutPrincipal;
    private DadosU dadosU;
    private ArrayList<DadosU> arrayDados;
    private int form;
    private double gtLat, gtLon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        arrayDados = new ArrayList<>();
        reference = ConexaoFirebase.getDatabase().child("usuario").child(auth.getUid()).child("dados");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawerLayoutPrincipal = (DrawerLayout) findViewById(R.id.drawer_layout);

        Intent it = getIntent();
        gtLat = it.getDoubleExtra("lat", 0.0);
        gtLon = it.getDoubleExtra("lon", 0.0);
        form = it.getIntExtra("form", 0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        txtNavHeaderNome  = (TextView) header.findViewById(R.id.txtNavHeaderNome); //navigationView.findViewById(R.id.txtNavHeaderNome);
        txtNavHeaderEmail = (TextView) header.findViewById(R.id.txtNavHeaderEmail);
        txtNavHeaderUid   = (TextView) header.findViewById(R.id.txtNavHeaderUid);

        txtNavHeaderNome.setText( user.getDisplayName() );
        txtNavHeaderEmail.setText( user.getEmail() );
        txtNavHeaderUid.setText( user.getUid() );

        txtNavHeaderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionResetPassword();
            }
        });
        txtNavHeaderUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy(user.getUid());
            }
        });

        imgNavHeader = (ImageView)findViewById(R.id.imgNavHeader);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.add(R.id.container, new MapsFragment(), "MapsFragment");

        if (form==0) {
            transaction.add(R.id.container, new ExProvider2(), "MapsFragmentV2");
            transaction.commitAllowingStateLoss();
        }else{
            ExProvider2 exp = new ExProvider2();
            Bundle bd = new Bundle();
            bd.putDouble("latf", gtLat);
            bd.putDouble("lonf", gtLon);
            bd.putInt("formf", 1);
            exp.setArguments(bd);
            transaction.add(R.id.container, exp, "MapsFragmentV2");
            transaction.commitAllowingStateLoss();
        }

    }//fim onCreate

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_meuUID:
                auth = ConexaoFirebase.getAuth();
                user = auth.getCurrentUser();
                Snackbar.make(drawerLayoutPrincipal, user.getUid(), Snackbar.LENGTH_LONG).setAction("Copiar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = user.getUid();
                        copy(text);
                    }
                }).show();
                break;
            case R.id.action_alterarNome:
                changeDisplayName();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_exemplobasico:
                showFragment(new MapsFragment(), "MapsFragment");
                break;
            case R.id.nav_provider1:
                showFragment(new ExProvider1(), "Provider 1");
                break;
            case R.id.nav_provider2:
                showFragment(new ExProvider2(), "Provider 2");
                break;
            case R.id.nav_marcadores:
                abrirMarker();
                break;
            case R.id.nav_info:
                abrirInfo();
                break;
            case R.id.nav_myloc:
                myEndereco();
                break;
            case R.id.nav_exit:
                callExit();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment, String name){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment, name);
        transaction.commit();
    }

    private void callExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActPrincipal.this);
        builder.setTitle("Sair");
        builder.setMessage("Deseja realmente Sair? Será Deslogado!");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                auth.signOut();
                Toast.makeText(ActPrincipal.this, "Deslogado com Sucesso.", Toast.LENGTH_SHORT).show();
                abrirLogin();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    private void abrirLogin(){
        finish();
        Intent it = new Intent(ActPrincipal.this, LoginActivity.class);
        startActivity(it);
    }

    private void abrirMarker(){
        //finish();
        Intent it = new Intent(ActPrincipal.this, MarkerActivity.class);
        startActivity(it);
    }

    private void abrirInfo(){
        //finish();
        Intent it = new Intent(ActPrincipal.this, InfoActivity.class);
        startActivity(it);
    }
    private void myEndereco(){
        Intent it = new Intent(ActPrincipal.this, EnderecoActivity.class);
        startActivity(it);
    }

    private void copy(String text){
        ClipboardManager cbm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("uid", text);
        cbm.setPrimaryClip(cd);
        Toast.makeText(ActPrincipal.this, "Copiado para área de transferência", Toast.LENGTH_SHORT).show();
    }

    private void changeDisplayName(){
        user = ConexaoFirebase.getAuth().getCurrentUser();
        AlertDialog.Builder builderN = new AlertDialog.Builder(this);
        builderN.setTitle("Editar Nome Usuário");
        builderN.setMessage("Alterar o Nome de Usuario\nAtualmente ["+user.getDisplayName()+"]");
        final TextView input = new EditText(this);
        input.setHint("Novo Codinome");
        input.setInputType(1);
        builderN.setView(input);
        builderN.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if( !( newName.trim().equals("") ) ) {
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName).build();
                    user.updateProfile(profile);
                    Toast.makeText(ActPrincipal.this, "Nome de Usuário Alterado.", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(ActPrincipal.this, "Deseja um Nome invisível é?", Toast.LENGTH_SHORT).show();
            }
        });
        builderN.setNegativeButton("Voltar", null);
        builderN.show();
    }

    private void questionResetPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActPrincipal.this);
        builder.setTitle("Redefinir Senha");
        builder.setMessage("Deseja Enviar um E-Mail para Redefinir sua Senha?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPassword();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }
    private void resetPassword(){
        final String email = user.getEmail();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if( task.isSuccessful() ){
                    Toast.makeText(ActPrincipal.this, "E-Mail enviado com Sucesso. Verifique seu E-Mail", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ActPrincipal.this, "Houve Algum erro.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
