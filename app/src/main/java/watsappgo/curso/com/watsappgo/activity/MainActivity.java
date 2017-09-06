package watsappgo.curso.com.watsappgo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import watsappgo.curso.com.watsappgo.Adapter.TabAdapter;
import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;
import watsappgo.curso.com.watsappgo.helper.Base64Custom;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.helper.SlidingTabLayout;
import watsappgo.curso.com.watsappgo.model.Contato;
import watsappgo.curso.com.watsappgo.model.Usuario;

public class MainActivity extends AppCompatActivity {

    //criando objeto de autenticação
    private FirebaseAuth usuarioAutenticacao;

    //criando objeto de referencia do banco firebase
    private DatabaseReference firebase;

    private Toolbar toolbar;

    //criando objetos de criação das abas.
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private String identificadorContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WatsAppGo");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //configurando o adapter.
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }


    //metodo que infla no toolbar criado, o menu que foi editado.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    //metodo que captura qual a opção foi clicada no ToolBar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_sobre:
                irTelaSobre();
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //metodo que faz a criação da dialog de adicionar contatos.
    private void abrirCadastroContato(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //Configurações do Dialog
        alertDialog.setTitle("Adicionar Novo Contato");
        alertDialog.setMessage("E-mail do usuário : ");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.ic_group_add);

        //Colocando uma caixa de texto dentro do dialog
        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);

        //Configurando Botões
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String emailContato = editText.getText().toString();

                //verificando se foi digitado algo.
                if (emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this," Digite um e-mail !", Toast.LENGTH_SHORT).show();
                }else {

                    //verifica se o usuario já esta cadastrado no App.
                    identificadorContato = Base64Custom.codificarBase64(emailContato);
                    //recuperando a instancia do Firebase para poder consultar o BD.
                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorContato);
                    //realizzando uma consulta unica no firebase
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                //Recuperar dados do contato a ser adicionado.
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                                //Recuperar identificador usuario logado (base64).
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado =preferencias.getIdentificador();

                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos")
                                                   .child(identificadorUsuarioLogado)
                                                   .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorContato(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                firebase.setValue(contato);
                                Toast.makeText(MainActivity.this," Usuário cadastrado com sucesso !", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(MainActivity.this," Usuário não possui cadastro !", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.create();
        alertDialog.show();

    }

    //metodo que desloga o usuario
    private void deslogarUsuario(){
        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioAutenticacao.signOut();
        voltarTelaLogin();
    }

    //metodo que direciona para a tela/activity "sobre".
    private void irTelaSobre(){
        Intent intent = new Intent(MainActivity.this, TelaSobreActivity.class);
        startActivity( intent );

    }

    //metodo que volta para a tela de login.
    private  void voltarTelaLogin(){

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity( intent );
        finish();

    }

}

//teste de alteração
