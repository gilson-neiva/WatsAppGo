package watsappgo.curso.com.watsappgo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import watsappgo.curso.com.watsappgo.Adapter.MensagemAdapter;
import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;
import watsappgo.curso.com.watsappgo.helper.Base64Custom;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Conversa;
import watsappgo.curso.com.watsappgo.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    //dados do destinatário.
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerMensagem;

    private EditText editMensagem;
    private ImageButton btMensagem;
    private ListView listView;

    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //Recuperando dados do usuário que esta logado utilizando as preferencias.
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        //recuperando dados do fragmento de contatos.
        //o obj do tipo bundle é utilizando para passar dados entre uma act e outra.
        Bundle extra = getIntent().getExtras();

        //testando para ver se veio informações
        if (extra!=null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //Configurando a tollBar.
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Montando e Configurando a listView e adapter de conversas.
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this,mensagens);
        listView.setAdapter(adapter);

        //Recuperando as mensagens do firebase.
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("mensagens")
                    .child(idUsuarioRemetente)
                    .child(idUsuarioDestinatario);

        //Criando listener de mensagens.
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpando as mensagens
                mensagens.clear();

                //percorrendo todos os "filhos da referencia do firebase
                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);


        //Envia Mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this ,"Digite uma mensagem para enviar!",Toast.LENGTH_SHORT).show();
                }else{
                    //Realizando tratamento para salvar mensagem no Firebase.

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //salvando mensagem para o remetente.
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);
                    if (!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar mensagem, tente novamente!",Toast.LENGTH_SHORT).show();
                    }else{
                        //salvando mensagem para o destinatario.
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
                        if (!retornoMensagemDestinatario){
                            Toast.makeText(ConversaActivity.this,"Problema ao salvar mensagem ao destinatário, tente novamente!",Toast.LENGTH_SHORT).show();
                        }

                    }

                    //salvando conversa para o remetente.
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar conversa, tente novamente!",Toast.LENGTH_SHORT).show();
                    }else{
                        //salvando conversa  para o destinatario.
                        conversa = new Conversa();
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario,idUsuarioRemetente,conversa);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this,"Problema ao salvar conversa ao destinatário, tente novamente!",Toast.LENGTH_SHORT).show();
                        }

                    }

                    //salvando conversa para o destinatatio.

                    editMensagem.setText("");

                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {
            //recuperando e modelando a instancia do Firebase.
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");

            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //criando metodo que salva as conversas.
    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            //recuperando e modelando a instancia do Firebase.
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    //removendo/fechando o listener de mensagens após o fechamento da act.
    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}

