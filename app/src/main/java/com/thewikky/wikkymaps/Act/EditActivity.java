package com.thewikky.wikkymaps.Act;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thewikky.wikkymaps.Connection.ConexaoFirebase;
import com.thewikky.wikkymaps.Model.DadosU;
import com.thewikky.wikkymaps.Model.LocationU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private TextView editUid;
    private EditText editNome, editCpf, editDtNasc, editCidNasc;
    private Spinner editEstNasc, editRacaCor;
    private RadioButton editMasc, editFem;
    private EditText editCep, editEndereco, editBairro, editCidade, editEstado, editPais;
    private EditText editTelefone, editEmail;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener valueEventListener;
    private DadosU getDadosU;
    private ArrayList<DadosU> dados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Editar Dados");

        auth = ConexaoFirebase.getAuth();
        user = auth.getCurrentUser();
        dados = new ArrayList<DadosU>();
        getDadosU = new DadosU();

        startComponent();
        callMask();

        reference = ConexaoFirebase.getDatabase().child("usuario").child(user.getUid()).child("dados").child("my");
        callDados();

        editUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "Seu UID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDados(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DadosU dadoU = dataSnapshot.getValue(DadosU.class);
                    completeDados(dadoU);
                    dados.add(dadoU);
                }else{
                    Toast.makeText(EditActivity.this, "Nothing found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_Atualizar:
                if( checkComponent() ){
                    updateDados( user.getUid() );
                    Toast.makeText(this, "Atualização bem sucedida.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("Voltar");
        builder.setMessage("Deseja realmente Voltar? Toda Informação será perdida!");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent it = new Intent(EditActivity.this, InfoActivity.class);
                startActivity(it);
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    private void startComponent(){
        editUid      = (TextView)    findViewById (R.id.editUid);
        editNome     = (EditText)    findViewById (R.id.editNome);
        editCpf      = (EditText)    findViewById (R.id.editCpf);
        editDtNasc   = (EditText)    findViewById (R.id.editDtNasc);
        editCidNasc  = (EditText)    findViewById (R.id.editCidNasc);
        editEstNasc  = (Spinner)     findViewById (R.id.editEstNasc);
        editRacaCor  = (Spinner)     findViewById (R.id.editRacaCor);
        editMasc     = (RadioButton) findViewById (R.id.editMasc);
        editFem      = (RadioButton) findViewById (R.id.editFem);
        editCep      = (EditText)    findViewById (R.id.editCep);
        editEndereco = (EditText)    findViewById (R.id.editEndereco);
        editBairro   = (EditText)    findViewById (R.id.editBairro);
        editCidade   = (EditText)    findViewById (R.id.editCidade);
        editEstado   = (EditText)    findViewById (R.id.editEstado);
        editPais     = (EditText)    findViewById (R.id.editPais);
        editTelefone = (EditText)    findViewById (R.id.editTelefone);
        editEmail    = (EditText)    findViewById (R.id.editEmail);
    }

    private void callMask(){
        //Mascara CEP
        SimpleMaskFormatter smfCep = new SimpleMaskFormatter("NN.NNN-NNN");
        MaskTextWatcher mtw = new SimpleMaskTextWatcher(editCep, smfCep);
        editCep.addTextChangedListener(mtw);
        //Máscara para Data de Nascimento
        SimpleMaskFormatter smfDtNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwDtNasc = new MaskTextWatcher(editDtNasc, smfDtNasc);
        editDtNasc.addTextChangedListener(mtwDtNasc);
        //Máscara para Telefone
        SimpleMaskFormatter smfTelefone = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        MaskTextWatcher mtwTelefone = new MaskTextWatcher(editTelefone, smfTelefone);
        editTelefone.addTextChangedListener(mtwTelefone);
        //Máscara para CPF
        SimpleMaskFormatter smfCpf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtwCpf = new MaskTextWatcher(editCpf, smfCpf);
        editCpf.addTextChangedListener(mtwCpf);
    }

    private void updateDados(String updateUid){
        DadosU nDU      = new DadosU();
        String sexo     = "";
        if(editMasc.isChecked()){
            sexo = "Masculino";
        }else{
            sexo = "Feminino";
        }
        nDU.setUid(updateUid);
        nDU.setNome    ( editNome.getText().toString().toUpperCase());
        nDU.setCpf     ( editCpf.getText().toString());
        nDU.setDtNasc  ( editDtNasc.getText().toString());
        nDU.setCidNasc ( editCidNasc.getText().toString().toUpperCase());
        nDU.setEstNasc ( editEstNasc.getSelectedItemPosition() + String.valueOf( editEstNasc.getSelectedItem().toString().toUpperCase() ) );
        nDU.setRacacor ( String.valueOf( editRacaCor.getSelectedItem().toString().toUpperCase() ) );
        nDU.setSexo    ( sexo );
        nDU.setCep     ( editCep.getText().toString());
        nDU.setEndereco( editEndereco.getText().toString().toUpperCase());
        nDU.setBairro  ( editBairro.getText().toString().toUpperCase());
        nDU.setCidade  ( editCidade.getText().toString().toUpperCase());
        nDU.setEstado  ( editEstado.getText().toString().toUpperCase());
        nDU.setPais    ( editPais.getText().toString().toUpperCase());
        nDU.setTelefone( editTelefone.getText().toString());
        nDU.setEmail   ( editEmail.getText().toString());
        //nDU.update();
        nDU.save(updateUid);
        Toast.makeText(this, "Atualização bem Sucedida.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void callBack(){

    }

    private boolean checkComponent(){
        String checkCpf = "[0-9][0-9][0-9].[0-9][0-9][0-9].[0-9][0-9][0-9]-[0-9][0-9]";
        String checkDtNasc = "[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]";
        String checkCep = "[0-9][0-9].[0-9][0-9][0-9]-[0-9][0-9][0-9]";
        boolean stage = true;
        if( stage ){
            if ( editNome.getText().toString().equals("")  ){
                editNome.requestFocus();
                Toast.makeText(this, "Digite o Nome Completo.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if( editCpf.getText().toString().equals("") || !( editCpf.getText().toString().matches(checkCpf) ) ){
                editCpf.requestFocus();
                Toast.makeText(this, "CPF Inválido.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if( editDtNasc.getText().toString().equals("") || !( editDtNasc.getText().toString().matches(checkDtNasc) ) ){
                editDtNasc.requestFocus();
                Toast.makeText(this, "Data de Nascimento Inválida.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(editCidNasc.getText().toString().equals("") ){
                editCidNasc.setText("Nao Informado");
            }
            if(!( editMasc.isChecked() ) && !( editFem.isChecked() ) ){
                Toast.makeText(this, "Selecione seu Gênero", Toast.LENGTH_SHORT).show();
                return false;
            }
            if( editCep.getText().toString().equals("") || !(editCep.getText().toString().matches(checkCep)) ){
                Toast.makeText(this, "C.E.P Inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            return false;
        }
        return true;
    }

    private void completeDados(DadosU dU){
        getDadosU.setUid( dU.getUid() );
        getDadosU.setNome( dU.getNome() );
        getDadosU.setCpf( dU.getCpf() );
        getDadosU.setDtNasc( dU.getDtNasc() );
        getDadosU.setCidNasc( dU.getCidNasc() );
        getDadosU.setEstNasc( dU.getEstNasc() );
        getDadosU.setRacacor( dU.getRacacor() );
        getDadosU.setSexo( dU.getSexo() );
        getDadosU.setCep( dU.getCep() );
        getDadosU.setEndereco( dU.getEndereco() );
        getDadosU.setBairro( dU.getBairro() );
        getDadosU.setCidade( dU.getCidade() );
        getDadosU.setEstado( dU.getEstado() );
        getDadosU.setPais( dU.getPais() );
        getDadosU.setTelefone( dU.getTelefone() );
        getDadosU.setEmail( dU.getEmail() );
        fillComponent(getDadosU);
        //Toast.makeText(this, "Tudo Atualizado", Toast.LENGTH_SHORT).show();
    }

    private void fillComponent(DadosU dU){
        String conc = "[ " + dU.getUid() + " ]";
        editUid.setText( conc );
        editNome.setText( dU.getNome() );
        editCpf.setText( dU.getCpf() );
        editDtNasc.setText( dU.getDtNasc() );
        editCidNasc.setText( dU.getCidNasc() );

        String sPos ="";
        int pos1 = Integer.parseInt( dU.getEstNasc().substring(0,1) );
        int pos2 = -1;
        try{
            pos2 = Integer.parseInt( dU.getEstNasc().substring(1,2) );
        }catch (NumberFormatException e){
            pos2 = -1;
        }
        if(pos2 >=0 && pos2 <=9)
            sPos = String.valueOf(pos1) + String.valueOf(pos2);
        else
            sPos = String.valueOf(pos1);
        int position = Integer.parseInt(sPos);
        editEstNasc.setSelection(position);

        if(dU.getRacacor().equals("NAO INFORMADO"))
            editRacaCor.setSelection(0);
        if(dU.getRacacor().equals("BRANCO"))
            editRacaCor.setSelection(1);
        if(dU.getRacacor().equals("PRETO"))
            editRacaCor.setSelection(2);
        if(dU.getRacacor().equals("PARDO"))
            editRacaCor.setSelection(3);
        if(dU.getRacacor().equals("AMARELO"))
            editRacaCor.setSelection(4);
        if(dU.getRacacor().equals("INDÍGENA"))
            editRacaCor.setSelection(5);

        editCep.setText( dU.getCep() );
        editEndereco.setText( dU.getEndereco() );
        editBairro.setText( dU.getBairro() );
        editCidade.setText( dU.getCidade() );
        editEstado.setText( dU.getEstado() );
        editPais.setText( dU.getPais() );
        editTelefone.setText( dU.getTelefone() );
        editEmail.setText( dU.getEmail() );
        if( dU.getSexo().equals("Masculino") ){
            editMasc.setChecked(true);
            editFem.setChecked(false);
        }
        if( dU.getSexo().equals("Feminino") ){
            editMasc.setChecked(false);
            editFem.setChecked(true);
        }
    }

}
