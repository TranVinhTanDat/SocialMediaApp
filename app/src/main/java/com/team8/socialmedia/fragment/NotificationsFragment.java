package com.team8.socialmedia.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team8.socialmedia.R;
import com.team8.socialmedia.models.AdapterNotification;
import com.team8.socialmedia.models.ModelNotification;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    RecyclerView notificationRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notifications=new ArrayList<>();
    private AdapterNotification adapterNotification;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);

        //init recyclerview
        notificationRv = view.findViewById(R.id.notificationRv);

        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotificaiton();
        return view;
    }

    private void getAllNotificaiton() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelNotification model = ds.getValue(ModelNotification.class);

                            //add to list
                            notifications.add(model);
                        }
                        //adapter
                        adapterNotification = new AdapterNotification(getActivity(), notifications);
                        //set to recyclerview
                        notificationRv.setAdapter(adapterNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}