package com.thewikky.wikkymaps.Act;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Connection.LocationAdapter;
import com.thewikky.wikkymaps.Model.LocationU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class MarkerActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private ArrayAdapter<LocationU> adapter, adapter2;
    private ArrayList<LocationU> locs, locs2;
    private ListView listView;
    private Button btnFecharTelaMarker;
    private SearchView searchViewM;
    private LocationU thisLocU;
    private int form;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        setTitle("Meus Marcadores");

        btnFecharTelaMarker = (Button) findViewById(R.id.btnFecharTelaMarker);
        listView = (ListView)findViewById(R.id.lstMarker);
        locs = new ArrayList<>();
        locs2 = new ArrayList<>();
        adapter = new LocationAdapter(this, locs);
        adapter2 = new LocationAdapter(this, locs2);
        listView.setAdapter(adapter);
        form = 0;

        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        reference = ConexaoFirebase.getDatabase().child("usuario").child(user.getUid()).child("marker");
        //reference = ConexaoFirebase.getDatabase().child("usuario").child(auth.getUid()).child("marker");
        callMarker();

        btnFecharTelaMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaPrincipal();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LocationU locU = locs.get(position);
                //deleteMarker(locU);
                callOptions(position);
            }
        });
    }//fim onCreate

    @Override
    protected void onStart() {
        super.onStart();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marker, menu);

        MenuItem item = menu.findItem(R.id.action_marker_search);
        SearchView search = (SearchView)item.getActionView();
        searchViewM = search;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if ( !(newText.equals("")) ) {
                    form = 1;
                    int count = adapter.getCount();
                    locs2.clear();
                    for(int i=0; i<count; i++){
                        thisLocU = adapter.getItem(i);
                        if( thisLocU.getNome().toUpperCase().contains(newText.trim().toUpperCase()) ){
                            locs2.add(thisLocU);
                            //Toast.makeText(MarkerActivity.this, "Tiene", Toast.LENGTH_SHORT).show();
                        }
                    }
                    listView.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();

                }else{
                    form = 2;
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_marker_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callMarker(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locs.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    LocationU lU = data.getValue(LocationU.class);
                    locs.add(lU);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }

    private void callOptions(final int pos){
        final LocationU locU;
        if(form==1){
            locU = locs2.get(pos);
        }else{
            locU = locs.get(pos);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Marcador " + locU.getNome() + "");
        builder.setMessage("Algumas Opções disponíveis deste Marcador");
        builder.setNeutralButton("Mapa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoMarker(Double.parseDouble(locU.getLatitude()) , Double.parseDouble(locU.getLongitude()), String.valueOf(locU.getNome() ));
            }
        });
        builder.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMarker(locU);
            }
        });
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editMarker(locU, pos);
            }
        });
        builder.show();
    }

    private void gotoMarker(final Double lat, final Double lon, final String name){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Opções");
        b.setMessage("Ir para o Mapa ou Buscar Endereço?");
        b.setPositiveButton("Ir para Mapa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                irParaMapa(lat, lon);
            }
        });
        b.setNeutralButton("Buscar Endereço", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buscarEndereco(lat, lon, name);
            }
        });
        b.show();
    }
    private void irParaMapa(Double lat, Double lon){
        LatLng latLon = new LatLng(lat, lon);
        Intent it = new Intent(MarkerActivity.this, ActPrincipal.class);
        it.putExtra("lat", lat);
        it.putExtra("lon", lon);
        it.putExtra("form", 1);
        startActivityForResult(it, RESULT_OK);
        this.finish();
    }
    private void buscarEndereco(final Double lat, final Double lon, final String name) {
        Intent it = new Intent(MarkerActivity.this, EnderecoActivity.class);
        it.putExtra("form", 1);
        it.putExtra("lat", lat);
        it.putExtra("lon", lon);
        it.putExtra("name", name);
        startActivityForResult(it, RESULT_OK);
    }

    private void editMarker(final LocationU locU, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Marcador "+locU.getNome());
        builder.setMessage("Qual informação deseja Editar?");
        builder.setPositiveButton("Longitude", null);
        builder.setNegativeButton("Latitude", null);
        builder.setNeutralButton("Nome", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editInfoMarker(pos, 1);
            }
        });
        builder.show();
    }
    private void editInfoMarker(final int pos, int edit){
        final LocationU loc;
        if (form==1) {
            loc = locs2.get(pos);
        }else{
            loc = locs.get(pos);
        }
        switch (edit) {
            case 1://nome
                final EditText input = new EditText(this);
                input.setInputType(1);
                input.setFilters( new InputFilter[] {new InputFilter.LengthFilter(25)} );
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setView(input);
                b.setTitle("Nome Marcador");
                b.setMessage("Digite o novo Nome do Marcador.\nAtualmente ["+loc.getNome()+"]");
                b.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nName = input.getText().toString().trim();
                        loc.setOldNome( loc.getNome() );
                        loc.setNome(nName);
                        loc.edit2(user.getUid());
                        Toast.makeText(MarkerActivity.this, "Nome do Marcador Alterado.", Toast.LENGTH_SHORT).show();
                    }
                });
                b.setNeutralButton("Cancelar", null);
                b.show();

                break;
            case 2://latitude

                break;
            case 3://longitude

                break;
        }

    }

    private void deleteMarker(final LocationU locU){
        String nameMarker = locU.getNome();
        AlertDialog.Builder builder = new AlertDialog.Builder(MarkerActivity.this);
        builder.setTitle("Excluir Marcador");
        builder.setMessage("Deseja realmente excluir o Marcador "+nameMarker+"?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference = ConexaoFirebase.getDatabase().child("usuario").child(user.getUid()).child("marker");
                reference.child( locU.getNome().toString() ).removeValue();
                Toast.makeText(MarkerActivity.this, "Exclusão Efetuada", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    private void abrirTelaPrincipal(){
        Intent it = new Intent(MarkerActivity.this, ActPrincipal.class);
        startActivity(it);
        finish();
    }

}
