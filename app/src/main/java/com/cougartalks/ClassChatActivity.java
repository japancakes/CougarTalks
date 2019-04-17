package com.cougartalks;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ClassChatActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, ClassNameRef, ClassMessageKeyRef;

    private String currentClassName, currentUserID, currentUserName, curDate, curTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chat);


        currentClassName = getIntent().getExtras().get("className").toString();
        Toast.makeText(ClassChatActivity.this, currentClassName, Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ClassNameRef = FirebaseDatabase.getInstance().getReference().child("Classes").child(currentClassName);


        InitializeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMsgToDatabase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        ClassNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void InitializeFields()
    {
        mToolbar = (Toolbar) findViewById(R.id.class_chat_layout_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentClassName);

        SendMessageButton = (ImageButton) findViewById(R.id.class_send_message_button);
        userMessageInput = (EditText) findViewById(R.id.class_input_message);
        displayTextMessages = (TextView) findViewById(R.id.class_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.class_scroll_view);
    }

    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void SendMsgToDatabase()
    {
        String message = userMessageInput.getText().toString();
        String msgKey = ClassNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Enter a message before hitting send", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat curDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            curDate = curDateFormat.format(date.getTime());

            Calendar time = Calendar.getInstance();
            SimpleDateFormat curTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            curTime = curTimeFormat.format(time.getTime());

            HashMap<String, Object> classMessageKey = new HashMap<>();
            ClassNameRef.updateChildren(classMessageKey);

            ClassMessageKeyRef = ClassNameRef.child(msgKey);

            HashMap<String, Object> msgInfoMap = new HashMap<>();
                msgInfoMap.put("name", currentUserName);
                msgInfoMap.put("message", message);
                msgInfoMap.put("date", curDate);
                msgInfoMap.put("time", curTime);

            ClassMessageKeyRef.updateChildren(msgInfoMap);


        }
    }

    private void displayMessages(DataSnapshot dataSnapshot)
    {
        Iterator it = dataSnapshot.getChildren().iterator();

        while(it.hasNext())
        {
            String chatDate = (String)((DataSnapshot)it.next()).getValue();
            String chatMessage = (String)((DataSnapshot)it.next()).getValue();
            String chatName = (String)((DataSnapshot)it.next()).getValue();
            String chatTime = (String)((DataSnapshot)it.next()).getValue();

            displayTextMessages.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "\t" + chatDate + "\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }

    }
}
