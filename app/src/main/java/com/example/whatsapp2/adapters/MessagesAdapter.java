package com.example.whatsapp2.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.entities.Mensaje;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Mensaje> messages; // Toma la lista de mensajes del Usuario del chat correspondiente
    private int currentUserId; // Usuario actual para diferenciar mensajes enviados y recibidos

    public MessagesAdapter(List<Mensaje> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Mensaje message = messages.get(position);
        holder.bind(message, currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        LinearLayout layout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textMessageContent);
            layout = (LinearLayout) itemView;
        }

        public void bind(Mensaje message, int currentUserId) {
            textContent.setText(message.contenido);
            if (message.usuarioId == currentUserId) {
                layout.setGravity(Gravity.END);
                textContent.setBackgroundColor(0xFFDCF8C6); // Mensajes enviados de color verde claro
            } else {
                layout.setGravity(Gravity.START);
                textContent.setBackgroundColor(0xFFFFFFFF); // Mensajes recibidos de color blanco
            }
        }
    }
}

