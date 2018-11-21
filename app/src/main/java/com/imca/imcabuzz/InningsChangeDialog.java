package com.imca.imcabuzz;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InningsChangeDialog extends Dialog {
    public InningsChangeDialog(@NonNull final ScoreActivity a) {
        super(a);
        setContentView(R.layout.new_innings_dialog);
        final EditText bat1Text=findViewById(R.id.new_innings_batsman1);
        final EditText bat2Text=findViewById(R.id.new_innings_batsman2);
        final EditText bowlText=findViewById(R.id.new_innings_bowler);
        Button add=findViewById(R.id.new_innings_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bat1=bat1Text.getText().toString();
                String bat2=bat2Text.getText().toString();
                String bowl=bowlText.getText().toString();
                if(bat1.isEmpty() || bowl.isEmpty() || bat2.isEmpty()){
                    Toast.makeText(a, "Can't add blank name", Toast.LENGTH_SHORT).show();
                }else{
                    a.changeInnings(bat1,bat2,bowl);
                    dismiss();
                }
            }
        });
    }
}
