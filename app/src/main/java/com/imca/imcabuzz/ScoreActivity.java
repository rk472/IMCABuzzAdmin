package com.imca.imcabuzz;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity {
    private String score,wicket;
    private DatabaseReference matchRef,curRef;
    private String bowlTeam,batTeam,matchId,bat1Key,bat2Key,bowlKey;
    private TextView battingText,bowlingText;
    private TextView overText,ballText;
    private TextView scoreText,wicketText;
    private TextView bat1Text,bat2Text,bowlText;
    private int ball=0;
    private TextView score1Text,score2Text;
    private String lastBallId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        curRef=FirebaseDatabase.getInstance().getReference().child("current");
        battingText=findViewById(R.id.batting_team_name);
        bowlingText=findViewById(R.id.bowling_team_name);
        overText=findViewById(R.id.total_over);
        ballText=findViewById(R.id.total_ball);
        scoreText=findViewById(R.id.total_score);
        wicketText=findViewById(R.id.total_wicket);
        bat1Text=findViewById(R.id.batsman1_name);
        bat2Text=findViewById(R.id.batsman2_name);
        bowlText=findViewById(R.id.bowler_name);
        RecyclerView list = findViewById(R.id.over_score_list);
        score1Text=findViewById(R.id.batsman1_score);
        score2Text=findViewById(R.id.batsman2_score);
        FirebaseRecyclerOptions<Over> options=new FirebaseRecyclerOptions.Builder<Over>()
                .setQuery(curRef.child("over"),Over.class)
                .build();
        FirebaseRecyclerAdapter<Over,OverViewHolder> f=new FirebaseRecyclerAdapter<Over, OverViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OverViewHolder holder, int position, @NonNull Over model) {
                holder.setRun(model.getRun());
            }

            @NonNull
            @Override
            public OverViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new OverViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.score_over_row,viewGroup,false));
            }
        };
        f.startListening();
        list.setAdapter(f);
        list.setHasFixedSize(true);
        list.setLayoutManager(new GridLayoutManager(ScoreActivity.this,4));
        curRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bat1Key=dataSnapshot.child("batsman1").getValue().toString();
                bat2Key=dataSnapshot.child("batsman2").getValue().toString();
                bowlKey=dataSnapshot.child("bowler").getValue().toString();
                batTeam=dataSnapshot.child("batting").getValue().toString();
                bowlTeam=dataSnapshot.child("bowling").getValue().toString();
                battingText.setText(batTeam);
                bowlingText.setText(bowlTeam);
                ball=Integer.parseInt(dataSnapshot.child("ball").getValue().toString());
                ballText.setText(Integer.toString(ball%6));
                overText.setText(Integer.toString(ball/6));
                matchId=dataSnapshot.child("id").getValue().toString();
                matchRef=FirebaseDatabase.getInstance().getReference().child("matches").child(matchId);
                matchRef.child(batTeam).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        score=dataSnapshot.child("score").getValue().toString();
                        wicket=dataSnapshot.child("wicket").getValue().toString();
                        scoreText.setText(score);
                        wicketText.setText(wicket);
                        String batsman1=dataSnapshot.child("batting").child(bat1Key).child("name").getValue(String.class);
                        String batsman2=dataSnapshot.child("batting").child(bat2Key).child("name").getValue(String.class);
                        String score1="";
                        String score2="";
                        if(dataSnapshot.child("batting").child(bat1Key).hasChild("score"))
                        score1=dataSnapshot.child("batting").child(bat1Key).child("score").getValue().toString();
                        if(dataSnapshot.child("batting").child(bat2Key).hasChild("score"))
                        score2=dataSnapshot.child("batting").child(bat2Key).child("score").getValue().toString();
                        bat1Text.setText(batsman1);
                        bat2Text.setText(batsman2);
                        score1Text.setText(score1);
                        score2Text.setText(score2);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                matchRef.child(bowlTeam).child("bowling").child(bowlKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("name"))
                        bowlText.setText(dataSnapshot.child("name").getValue().toString());
                        matchRef.child(bowlTeam).child("bowling").child(bowlKey).removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void addScore(final int s, final boolean first){
        curRef.child("ball").setValue(ball+1)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                matchRef.child(batTeam).child("score").setValue(Integer.parseInt(score)+s)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            matchRef.child(batTeam).child("batting").child(first?bat1Key:bat2Key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    matchRef.child(batTeam).child("batting").child(first?bat1Key:bat2Key).removeEventListener(this);
                                    String score=dataSnapshot.child("score").getValue().toString();
                                    matchRef.child(batTeam).child("batting").child(first?bat1Key:bat2Key)
                                            .child("score").setValue(Integer.parseInt(score)+s)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    matchRef.child(bowlTeam).child("bowling").child(bowlKey)
                                                            .child("score").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            matchRef.child(bowlTeam).child("bowling").child(bowlKey)
                                                                    .child("score").removeEventListener(this);
                                                            String score=dataSnapshot.getValue().toString();
                                                            matchRef.child(bowlTeam).child("bowling").child(bowlKey)
                                                                    .child("score").setValue(Integer.parseInt(score)+s)
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    lastBallId=curRef.child("over").push().getKey();
                                                                    curRef.child("over").child(lastBallId).child("run").setValue(Integer.toString(s))
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(ScoreActivity.this, "run added", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
            }
        });
    }
    public void score1(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),1).show();
    }
    public void score2(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),2).show();
    }
    public void score3(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),3).show();
    }
    public void score4(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),4).show();
    }
    public void score6(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),6).show();
    }
    public void score0(View view) {
        new BatsmanSelectDialog(this,bat1Text.getText().toString(),bat2Text.getText().toString(),0).show();
    }
    public void scoreNB(View view) {

    }
    public void addExtra(final String text, final int wide,final int sc){
        final int s=wide+sc;
        matchRef.child(batTeam).child("score").setValue(Integer.parseInt(score)+s)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra").removeEventListener(this);
                        String score=dataSnapshot.getValue().toString();
                        matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra")
                                .setValue(Integer.parseInt(score)+s)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                lastBallId=curRef.child("over").push().getKey();
                                curRef.child("over").child(lastBallId).child("run")
                                        .setValue(sc+text)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if(text.equalsIgnoreCase("by")) {
                                            curRef.child("ball").setValue(ball + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ScoreActivity.this, "By added", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else Toast.makeText(ScoreActivity.this, "Wide added", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public void scoreWD(View view) {
        new RunDialog(this,"wd",1).show();
    }
    public void scoreBY(View view) {
        new RunDialog(this,"by",0).show();
    }
    public void scoreOV(View view) {
        if(lastBallId==null)
            Toast.makeText(this, "can't add overthrow run", Toast.LENGTH_SHORT).show();
        else
            new OverthrowRunDialog(this).show();

    }
    public void addOverThrow(final int run){
        matchRef.child(batTeam).child("score").setValue(Integer.parseInt(score)+run)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra").removeEventListener(this);
                                String score=dataSnapshot.getValue().toString();
                                matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("extra")
                                        .setValue(Integer.parseInt(score)+run)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        curRef.child("over").child(lastBallId).child("run").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                curRef.child("over").child(lastBallId).child("run").removeEventListener(this);
                                                String r=dataSnapshot.getValue().toString();
                                                curRef.child("over").child(lastBallId).child("run").setValue(r+"+OV"+run).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ScoreActivity.this, "run added", Toast.LENGTH_SHORT).show();
                                                        lastBallId=null;
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }
    public void changeInnings(final String bat1, final String bat2, String bowler){
        final String bowl=batTeam;
        final String bat=bowlTeam;
        final String newBowlKey=matchRef.child(bowl).child("bowling").push().getKey();
        Map <String,Object> m=new HashMap<>();
        m.put("name",bowler);
        m.put("score",0);
        m.put("extra",0);
        m.put("wicket",0);

        matchRef.child(bowl).child("bowling").child(newBowlKey).updateChildren(m)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,Object> m=new HashMap<>();
                m.put("name",bat2);
                m.put("score",0);
                m.put("out",false);
                final String newBat1Key=matchRef.child(bat).child("batting").push().getKey();
                matchRef.child(bat).child("batting").child(newBat1Key).updateChildren(m)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String,Object> m=new HashMap<>();
                        m.put("name",bat1);
                        m.put("score",0);
                        m.put("out",false);
                        final String newBat2Key=matchRef.child(bat).child("batting").push().getKey();
                        matchRef.child(bat).child("batting").child(newBat2Key).updateChildren(m)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final Map <String,Object> m=new HashMap<>();
                                m.put("batsman1",newBat1Key);
                                m.put("batsman2",newBat2Key);
                                m.put("bowler",newBowlKey);
                                curRef.updateChildren(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,Object> m=new HashMap<>();
                                        m.put("batting",bat);
                                        m.put("bowling",bowl);
                                        curRef.updateChildren(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                curRef.child("ball").setValue(0)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ScoreActivity.this, "Innings changed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }
    public void endInnings(View view) {
        new InningsChangeDialog(this).show();
    }
    public void endMatch(View view) {

    }
    public void end(String feedback){
        matchRef.child("conclusion").setValue(feedback)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                curRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void batsman1Out(View view) {
        new BatsmanAddDialog(this,bat1Key,"batsman1").show();

    }
    public void addBatsman(final String name, String key, final String batsman){
        matchRef.child(batTeam).child("batting").child(key)
                .child("out").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final String newKey=matchRef.child(batTeam).child("batting").push().getKey();
                Map<String,Object> m=new HashMap<>();
                m.put("name",name);
                m.put("score",0);
                m.put("out",false);
                matchRef.child(batTeam).child("batting").child(newKey).updateChildren(m)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        curRef.child(batsman).setValue(newKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                curRef.child("over").push().child("run").setValue("W").addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        matchRef.child(batTeam).child("wicket").setValue(Integer.toString(Integer.parseInt(wicket)+1))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                curRef.child("ball").setValue(ball+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        matchRef.child(bowlTeam).child("bowling").child(bowlKey).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                matchRef.child(bowlTeam).child("bowling").child(bowlKey).removeEventListener(this);
                                                                String wicket=dataSnapshot.child("wicket").getValue().toString();
                                                                matchRef.child(bowlTeam).child("bowling").child(bowlKey).child("wicket")
                                                                        .setValue(Integer.parseInt(wicket)+1)
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ScoreActivity.this, "Batsman changed successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void batsman2Out(View view) {
        new BatsmanAddDialog(this,bat2Key,"batsman2").show();
    }
    void addBowler(String name){
        final String newKey=matchRef.child(bowlTeam).child("bowling").push().getKey();
        Map<String,Object> m=new HashMap<>();
        m.put("name",name);
        m.put("score",0);
        m.put("extra",0);
        m.put("wicket",0);
        matchRef.child(bowlTeam).child("bowling").child(newKey).updateChildren(m)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                curRef.child("bowler").setValue(newKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        curRef.child("over").removeValue().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                lastBallId=null;
                                Toast.makeText(ScoreActivity.this, "Bowler changed successful", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ScoreActivity.this, "Bowler changed successful", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void overUp(View view) {
        if(ball%6 == 0){
            new BowlerAddDialog(this).show();
        }else{
            Toast.makeText(this, "6 balls not bowled yet", Toast.LENGTH_SHORT).show();
        }
    }
}
