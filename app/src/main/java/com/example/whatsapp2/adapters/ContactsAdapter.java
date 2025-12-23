package com.example.whatsapp2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.entities.Usuario;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Usuario> contacts;
    private OnContactClickListener listener;

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
        holder.bind(contact, listener);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
        }

        public void bind(final Usuario contact, final OnContactClickListener listener) {
            textName.setText(contact.nombre);
            itemView.setOnClickListener(v -> listener.onContactClick(contact));
        }
    }
}
