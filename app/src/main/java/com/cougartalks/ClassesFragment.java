package com.cougartalks;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClassesFragment extends Fragment
{
    private View classFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_classes = new ArrayList<>();

    private DatabaseReference ClassRef;

    public ClassesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        classFragmentView = inflater.inflate(R.layout.fragment_classes, container, false);

        ClassRef = FirebaseDatabase.getInstance().getReference().child("Classes");

        InitializeFields();

        RetrieveAndDisplayClasses();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String currentClassName = parent.getItemAtPosition(position).toString();

                Intent classChatIntent = new Intent(getContext(), ClassChatActivity.class);
                classChatIntent.putExtra("className", currentClassName);
                startActivity(classChatIntent);
            }
        });


        return classFragmentView;
    }


    private void InitializeFields()
    {
        list_view = (ListView) classFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, list_of_classes);
        list_view.setAdapter(arrayAdapter);

    }

    private void RetrieveAndDisplayClasses()
    {
        ClassRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator it = dataSnapshot.getChildren().iterator();

                while(it.hasNext())
                {
                    set.add(((DataSnapshot)it.next()).getKey());
                }

                list_of_classes.clear();
                list_of_classes.addAll(set);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


}
