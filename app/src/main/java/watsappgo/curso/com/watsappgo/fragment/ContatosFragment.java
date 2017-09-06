package watsappgo.curso.com.watsappgo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearSmoothScroller;
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

import watsappgo.curso.com.watsappgo.Adapter.ContatoAdapter;
import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.activity.ConversaActivity;
import watsappgo.curso.com.watsappgo.config.ConfiguracaoFirebase;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView ;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;

    private DatabaseReference firebase;

    private ValueEventListener valueEventListenerContatos;


    public ContatosFragment() {
        // Required empty public constructor
    }

    //utilizando o ciclo de vida do fragmement para que o listener seja utilizado somente quando
    //a tela for criada.
    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    //sobrescrevendo o metodo onStop e utilizando ele para fechar o listener de escuta.
    //isso é feito para economizar recursos.
    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Instanciando objetos.
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Monta listView e Adapter.
        listView = (ListView) view.findViewById(R.id.lv_contatosId);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos
        );*/
        adapter = new ContatoAdapter(getActivity(),contatos);
        listView.setAdapter(adapter);

        //Recuperando contatos do Firebase.
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("contatos")
                    .child( identificadorUsuarioLogado );

        //Listener para recuperar contatos.
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            //metodo que é chamado sempre que o tiver mudanças no nó indicado acima.
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpando a lista de contatos para não deixar duplicar.
                contatos.clear();

                //listar contatos.
                //o metodo getChildren vai pecorrer os filhos dos dados do dataSnapshot
                for(DataSnapshot dados: dataSnapshot.getChildren()) {

                    Contato contato = dados.getValue(Contato.class);
                    //utilizando o array de contatos.
                    contatos.add(contato);
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

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //recupernado dados a serem passados.
                Contato contato = contatos.get(i);

                //enviando dados para a Activity de conversas.
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());


                startActivity(intent);

            }
        });



        return view;
    }

}
