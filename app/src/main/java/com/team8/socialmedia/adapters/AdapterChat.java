package com.team8.socialmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.team8.socialmedia.R;
import com.team8.socialmedia.models.ModelChat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts: row_chat_left.xml for receiver for receiver, row_Chat_right.xml for sender
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();
        String type = chatList.get(position).getType();

        //convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
        System.out.println("text" + message );
         if ("text".equals(type)){
             //text message
             myHolder.messageTv.setVisibility(View.VISIBLE);
             myHolder.messageIv.setVisibility(View.GONE);

             myHolder.messageTv.setText(message);
         }
         else {
             //text message
             myHolder.messageTv.setVisibility(View.GONE);
             myHolder.messageIv.setVisibility(View.VISIBLE);

             Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(myHolder.messageIv);
         }

        //set data
        myHolder.messageTv.setText(message);
        myHolder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(myHolder.profileIv);
        } catch (Exception e) {
        }

        if (myHolder.messageLayout != null) {
            myHolder.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    TextView messageTextView = new TextView(context);
                    messageTextView.setText("Are you sure you want to delete this message?");
                    messageTextView.setTextSize(16);
                    messageTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    messageTextView.setGravity(Gravity.CENTER);
                    builder.setView(messageTextView);

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Your delete logic here
                            String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            int position = myHolder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                String msgTimeStamp = chatList.get(position).getTimeStamp();
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
                                Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            if (ds.child("sender").getValue().equals(myUID)) {
                                                ds.getRef().removeValue();

//                                                HashMap<String, Object> hashMap = new HashMap<>();
//                                                hashMap.put("message", "This message was deleted...");
//                                                ds.getRef().updateChildren(hashMap);

                                                Toast.makeText(context, "Message deleted...", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "You can delete only your message...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle onCancelled
                                    }
                                });
                            }
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
                }
            });
        } else {
            // Handle the case where myHolder.messageLayout is null
            // Log an error, show a toast, or take appropriate action
            Toast.makeText(context, "Error: messageLayout is null", Toast.LENGTH_SHORT).show();
        }

        //set seen /delivered status of message
        if (position == chatList.size() - 1) {
            if (chatList.get(position).isSeen()) {
                myHolder.isSeenTv.setText("Seen");
            } else {
                myHolder.isSeenTv.setText("Delivered");
            }
        } else {
            myHolder.isSeenTv.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    //View holder class
    class MyHolder extends RecyclerView.ViewHolder {
        ImageView profileIv, messageIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            messageIv = itemView.findViewById(R.id.messageIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

}
