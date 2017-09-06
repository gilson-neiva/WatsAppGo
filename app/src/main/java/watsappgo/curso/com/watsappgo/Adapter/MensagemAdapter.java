package watsappgo.curso.com.watsappgo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import watsappgo.curso.com.watsappgo.R;
import watsappgo.curso.com.watsappgo.helper.Preferencias;
import watsappgo.curso.com.watsappgo.model.Contato;
import watsappgo.curso.com.watsappgo.model.Mensagem;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private ArrayList<Mensagem> mensagens;
    private Context context;


    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.mensagens = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        //verifica se a lista de contatos esta vazia.
        if (mensagens != null ){

            //Recupera dados do usuario remetente.
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRementene = preferencias.getIdentificador();

            //inicializando objeto para montagem da view.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Recuperando a mensagem.
            Mensagem mensagem = mensagens.get(position);

            //o objeto inflater permite a manipulação de um XML , convertendo ele em uma view.
            //Montando a view a partir do XML
            if (idUsuarioRementene.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita,parent,false);
            }else{
                view = inflater.inflate(R.layout.item_mensagem_esquerda,parent,false);
            }

            //recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());

        }

        return view;
    }
}
