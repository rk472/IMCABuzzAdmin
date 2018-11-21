package com.imca.imcabuzz;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BowlerAddDialog extends Dialog {
    public BowlerAddDialog(@NonNull final ScoreActivity a) {
        super(a);
        setContentView(R.layout.bowler_dialog);
        final EditText nameText=findViewById(R.id.new_bowler_name);
        Button add=findViewById(R.id.new_bowler_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameText.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(a, "Can't add blank name", Toast.LENGTH_SHORT).show();
                }else{
                    a.addBowler(name);
                    dismiss();
                }
            }
        });
    }
}
