package com.example.logchat.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logchat.databinding.ItemContainerReceivedMessageBinding;
import com.example.logchat.databinding.ItemContainerSentMessageBinding;
import com.example.logchat.models.ChatMessage;

import java.util.List;

/**
 * Adapter for managing and displaying chat messages in a RecyclerView.
 * Handles both sent and received messages with distinct view types.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages; // List of chat messages to display
    private final Bitmap receiverProfileImage; // Profile image of the receiver
    private final String sendId; // ID of the current user (sender)
    public static final int VIEW_TYPE_SENT = 1; // Constant for sent message view type
    public static final int VIEW_TYPE_RECEIVED = 2; // Constant for received message view type

    // Constructor for the ChatAdapter
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String sendId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.sendId = sendId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            // Inflate the layout for sent messages
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            // Inflate the layout for received messages
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Determine the view type and bind data accordingly
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size(); // Return the number of messages in the list
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on whether the message was sent or received
        if (chatMessages.get(position).senderId.equals(sendId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // ViewHolder class for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding; // Binding for sent message layout

        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        // Bind data to the sent message view
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message); // Set message text
            binding.textDateTime.setText(chatMessage.dateTime); // Set message timestamp
        }
    }

    // ViewHolder class for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding; // Binding for received message layout

        public ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        // Bind data to the received message view
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message); // Set message text
            binding.textDateTime.setText(chatMessage.dateTime); // Set message timestamp
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage); // Set profile image
            } else {
                // Handle case where receiver profile image is null (e.g., set a placeholder image)
                binding.imageProfile.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }
}
