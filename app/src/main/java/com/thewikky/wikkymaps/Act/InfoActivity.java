package com.thewikky.wikkymaps.Act;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Model.DadosU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private TextView  txtUid2, txtNome2, txtCpf2, txtDtNasc2;
    private TextView  txtCidNasc2, txtEstNasc2, txtRacaCor2, txtSexo2;
    private TextView txtEndereco2, txtBairro2, txtCidade2, txtEstado2, txtPais2, txtTelefone2, txtEmail2;
    private Button btnVoltarTelaInicial2;
    private SearchView searchViewM;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener valueEventListener;
    private ArrayList<DadosU> dados;
    private DadosU getDadosU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("Seus Dados");

        startComponent();
        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        dados = new ArrayList<DadosU>();
        getDadosU = new DadosU();

        reference = ConexaoFirebase.getDatabase().child("usuario").child(user.getUid()).child("dados").child("my");
        callDados();

        btnVoltarTelaInicial2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent it = new Intent(InfoActivity.this, ActPrincipal.class);
                startActivity(it);
            }
        });

        txtUid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy( txtUid2.getText().toString() );
            }
        });
    }//fim onCreate

    private void callDados(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DadosU dadoU = dataSnapshot.getValue(DadosU.class);
                    insertDados(dadoU);
                    dados.add(dadoU);
                }else{
                    Toast.makeText(InfoActivity.this, "Nothing found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(InfoActivity.this, "Insira seus Dados clicando no Botão \"Editar(Lápis)\" ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //reference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //reference.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_info_editar:
                finish();
                Intent it = new Intent(InfoActivity.this, EditActivity.class);
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startComponent() {
        txtUid2               = (TextView) findViewById(R.id.txtUid2);
        txtNome2              = (TextView) findViewById(R.id.txtNome2);
        txtCpf2               = (TextView) findViewById(R.id.txtCpf2);
        txtDtNasc2            = (TextView) findViewById(R.id.txtDtNasc2);
        txtCidNasc2           = (TextView) findViewById(R.id.txtCidNasc2);
        txtEstNasc2           = (TextView) findViewById(R.id.txtEstNasc2);
        txtRacaCor2           = (TextView) findViewById(R.id.txtRacaCor2);
        txtSexo2              = (TextView) findViewById(R.id.txtSexo2);
        txtEndereco2          = (TextView) findViewById(R.id.txtEndereco2);
        txtBairro2            = (TextView) findViewById(R.id.txtBairro2);
        txtCidade2            = (TextView) findViewById(R.id.txtCidade2);
        txtEstado2            = (TextView) findViewById(R.id.txtEstado2);
        txtPais2              = (TextView) findViewById(R.id.txtPais2);
        txtTelefone2          = (TextView) findViewById(R.id.txtTelefone2);
        txtEmail2             = (TextView) findViewById(R.id.txtEmail2);
        btnVoltarTelaInicial2 = (Button)   findViewById(R.id.btnVoltarTelaInicial2);
    }

    private void copy(String text){
        ClipboardManager cbm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("uid", text);
        cbm.setPrimaryClip(cd);
        Toast.makeText(InfoActivity.this, "Copiado para área de transferência", Toast.LENGTH_SHORT).show();
    }

    private void insertDados(DadosU dadosU){
        String opt[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};//10 opções
        String estNasc = dadosU.getEstNasc();
        StringBuilder fim = new StringBuilder();
        int sizeEst = estNasc.length();
        boolean confirm = false;
        if( estNasc.substring(0,1).equals("1") || estNasc.substring(0,1).equals("2") ){
            for(int i = 0; i < 10; i++){
                if( estNasc.substring(1,2).equals(opt[i]) ){
                    fim.append( estNasc.substring(2,4) );
                    confirm = true;
                    break;
                }
            }
        }
        if(confirm==false && !(estNasc.equals("0NAO INFORMADO")) )
            fim.append( estNasc.substring(1,3) );
        else
            fim.append( estNasc.substring(1, sizeEst) );

        txtUid2.setText     ( "UID: "            + dadosU.getUid() );
        txtNome2.setText    ( "Nome            : " + dadosU.getNome() );
        txtCpf2.setText     ( "CPF               : " + dadosU.getCpf() );
        txtDtNasc2.setText  ( "Dt.Nasc.       : " + dadosU.getDtNasc() );
        txtCidNasc2.setText ( "Cid.Nasc.     : " + dadosU.getCidNasc() );
        txtEstNasc2.setText ( "Est.Nasc.     : " + fim.toString() );
        txtRacaCor2.setText ( "Raça/Cor     : " + dadosU.getRacacor() );
        txtSexo2.setText    ( "Gênero         : " + dadosU.getSexo() );
        txtEndereco2.setText( "Endereço     : " + dadosU.getEndereco() );
        txtBairro2.setText  ( "Bairro           : " + dadosU.getBairro() );
        txtCidade2.setText  ( "Cidade         : " + dadosU.getCidade() );
        txtEstado2.setText  ( "Estado         : " + dadosU.getEstado() );
        txtPais2.setText    ( "País             : " + dadosU.getPais() );
        txtTelefone2.setText( "Telefone     : " + dadosU.getTelefone() );
        txtEmail2.setText   ( "E-Mail         : " + dadosU.getEmail() );
    }
}
