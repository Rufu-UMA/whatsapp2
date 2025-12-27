package com.example.whatsapp2.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.database.entities.Usuario;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Usuario> contacts;
    private OnContactClickListener listener;
    private int currentUserId = 1; // ID del usuario actual

    public interface OnContactClickListener {
        void onContactClick(Usuario contact);
    }

    public ContactsAdapter(List<Usuario> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Usuario contact = contacts.get(position);
        holder.bind(contact, listener, currentUserId);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textLastMessage;
        TextView textDate;
        ImageView imageProfile;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            textDate = itemView.findViewById(R.id.textDate);
            imageProfile = itemView.findViewById(R.id.imageProfile);
        }

        public void bind(final Usuario contact, final OnContactClickListener listener, int currentUserId) {
            textName.setText(contact.nombre);

            // Cargar imagen de perfil
            if (contact.fotoPerfil != null && !contact.fotoPerfil.isEmpty()) {
                try {
                    imageProfile.setImageURI(Uri.parse(contact.fotoPerfil));
                } catch (Exception e) {
                    imageProfile.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                imageProfile.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // Cargar último mensaje de forma asíncrona
            Executors.newSingleThreadExecutor().execute(() -> {
                Mensaje lastMessage = AppBaseDeDatos.getDatabase(itemView.getContext())
                        .chatDao().getLastMessage(currentUserId, contact.id);
                
                itemView.post(() -> {
                    if (lastMessage != null) {
                        textLastMessage.setText(lastMessage.contenido);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                        textDate.setText(sdf.format(new Date(lastMessage.HoraEnvio)));
                    } else {
                        textLastMessage.setText("");
                        textDate.setText("");
                    }
                });
            });

            itemView.setOnClickListener(v -> listener.onContactClick(contact));
        }
    }
}
