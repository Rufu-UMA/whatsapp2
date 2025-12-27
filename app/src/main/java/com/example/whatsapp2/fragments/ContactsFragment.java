package com.example.whatsapp2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.activities.ChatActivity;
import com.example.whatsapp2.adapters.ContactsAdapter;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.dao.ChatDao;
import com.example.whatsapp2.database.entities.Usuario;
import java.util.List;
import java.util.concurrent.Executors;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { // Se ejecuta cuando se dibuja en pantalla
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewContacts); // Busca la lista visual en el layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadContacts(); // Llama a la función para cargar los contactos desde la base de datos

        return view;
    }

    public void loadContacts() {
        if (getContext() == null) return;
        Executors.newSingleThreadExecutor().execute(() -> {
            ChatDao dao = AppBaseDeDatos.getDatabase(getContext()).chatDao();
            List<Usuario> users = dao.getContacts(); // Obtiene solo los contactos (excluyendo al usuario actual)

            List<Usuario> finalUsers = users;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter = new ContactsAdapter(finalUsers, contact -> {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("CONTACT_ID", contact.id); // Se le envía a chat activity el id y nombre del contacto seleccionado
                        intent.putExtra("CONTACT_NAME", contact.nombre);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }
}
