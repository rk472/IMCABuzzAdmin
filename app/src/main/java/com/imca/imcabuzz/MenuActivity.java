package com.imca.imcabuzz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private Spinner team1,team2;
    private DatabaseReference matchRef,curRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        team1=findViewById(R.id.team1);
        team2=findViewById(R.id.team2);
        matchRef=FirebaseDatabase.getInstance().getReference().child("matches");
        curRef=FirebaseDatabase.getInstance().getReference().child("current");
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
                if(dataSnapshot.hasChild("current")){
                    startActivity(new Intent(MenuActivity.this,ScoreActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void start(View view) {
        String title=team1.getSelectedItem().toString()+" vs "+team2.getSelectedItem().toString();
        final String key=matchRef.push().getKey();
        matchRef.child(key).child("title").setValue(title).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,Object> m=new HashMap<>();
                m.put("id",key);
                m.put("bowling",team1.getSelectedItem().toString());
                m.put("batting",team2.getSelectedItem().toString());
                m.put("batsman1","na");
                m.put("batsman2","na");
                m.put("bowler","na");
                m.put("ball",0);
                curRef.updateChildren(m).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final Map <String,Object> m=new HashMap<>();
                        m.put("score",0);
                        m.put("wicket",0);
                        matchRef.child(key).child(team2.getSelectedItem().toString()).updateChildren(m)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                matchRef.child(key).child(team1.getSelectedItem().toString()).updateChildren(m)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent i=new Intent(MenuActivity.this,BatsmanBowlerActivity.class);
                                        i.putExtra("key",key);
                                        i.putExtra("team1",team1.getSelectedItem().toString());
                                        i.putExtra("team2",team2.getSelectedItem().toString());
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
