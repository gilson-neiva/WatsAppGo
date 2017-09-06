package watsappgo.curso.com.watsappgo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import watsappgo.curso.com.watsappgo.Adapter.ConversaAdapter;
import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.activity.ConversaActivity;
import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;
import watsappgo.curso.com.watsappgo.helper.Base64Custom;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Conversa;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {


    private ListView listView ;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;

    private DatabaseReference firebase;

    private ValueEventListener valueEventListenerConversas;


    public ConversasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Instanciando objetos.
        conversas = new ArrayList<>();

        //Monta listView e Adapter.
        listView = (ListView) view.findViewById(R.id.lv_conversas);

        adapter = new ConversaAdapter(getActivity(),conversas);
        listView.setAdapter(adapter);

        //Recuperando contatos do Firebase.
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child( idUsuarioLogado );

        //Listener para recuperar contatos.
        valueEventListenerConversas = new ValueEventListener() {
            @Override
            //metodo que é chamado sempre que o tiver mudanças no nó indicado acima.
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpando a lista de contatos para não deixar duplicar.
                conversas.clear();

                //listar contatos.
                //o metodo getChildren vai pecorrer os filhos dos dados do dataSnapshot
                for(DataSnapshot dados: dataSnapshot.getChildren()) {

                    Conversa conversa = dados.getValue(Conversa.class);
                    //utilizando o array de contatos.
                    conversas.add(conversa);
                }

                //notificando o adpater que teve alterações na lista de contatos.
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //criando evento de clique na lista de contatos para abrir a conversa.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //recupernado dados a serem passados.
                Conversa conversa = conversas.get(i);

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //enviando dados para a Activity de conversas.
                intent.putExtra("nome",conversa.getNome());
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email",email);

                startActivity(intent);

            }
        });

        return view;
    }

    //utilizando o ciclo de vida do fragmement para que o listener seja utilizado somente quando
    //a tela for criada.
    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);
    }

    //sobrescrevendo o metodo onStop e utilizando ele para fechar o listener de escuta.
    //isso é feito para economizar recursos.
    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }

}