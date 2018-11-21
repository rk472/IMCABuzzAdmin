package com.imca.imcabuzz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BatsmanBowlerActivity extends AppCompatActivity {
    private EditText bat1Text,bat2Text,bowlText;
    private DatabaseReference matchRef,curRef;
    private String matchKey,bowlTeam,batTeam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batsman_bowler);
        bat1Text=findViewById(R.id.batsman1);
        bat2Text=findViewById(R.id.batsman2);
        bowlText=findViewById(R.id.bowler);
        matchKey=getIntent().getStringExtra("key");
        matchRef=FirebaseDatabase.getInstance().getReference().child("matches").child(matchKey);
        curRef=FirebaseDatabase.getInstance().getReference().child("current");
        bowlTeam=getIntent().getStringExtra("team1");
        batTeam=getIntent().getStringExtra("team2");
    }

    public void startMatch(View view) {
        String bat1=bat1Text.getText().toString();
        final String bat2=bat2Text.getText().toString();
        final String bowl=bowlText.getText().toString();
        if(bat1.isEmpty() || bat2.isEmpty() || bowl.isEmpty()){
            Toast.makeText(this, "You must fill all the fields", Toast.LENGTH_SHORT).show();
        }else{
            final String bat1Key=matchRef.child(batTeam).child("batting").push().getKey();
            Map<String,Object> m=new HashMap<>();
            m.put("name",bat1);
            m.put("score",0);
            m.put("out",false);
            matchRef.child(batTeam).child("batting").child(bat1Key).updateChildren(m)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final String bat2Key=matchRef.child(batTeam).child("batting").push().getKey();
                    Map<String,Object> m=new HashMap<>();
                    m.put("name",bat2);
                    m.put("score",0);
                    m.put("out",false);
                    matchRef.child(batTeam).child("batting").child(bat2Key).updateChildren(m)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BatsmanBowlerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            final String bowlKey=matchRef.child(bowlTeam).child("bowling").push().getKey();
                            Map<String,Object> m=new HashMap<>();
                            m.put("name",bowl);
                            m.put("score",0);
                            m.put("extra",0);
                            m.put("wicket",0);
                            matchRef.child(bowlTeam).child("bowling").child(bowlKey).updateChildren(m)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String,Object> m=new HashMap<>();
                                    m.put("batsman1",bat1Key);
                                    m.put("batsman2",bat2Key);
                                    m.put("bowler",bowlKey);
                                    curRef.updateChildren(m).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(BatsmanBowlerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent i=new Intent(BatsmanBowlerActivity.this,ScoreActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BatsmanBowlerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BatsmanBowlerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
