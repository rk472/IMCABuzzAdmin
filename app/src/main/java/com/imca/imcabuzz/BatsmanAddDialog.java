package com.imca.imcabuzz;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BatsmanAddDialog extends Dialog {
    public BatsmanAddDialog(@NonNull final ScoreActivity a, final String id,final String bat) {
        super(a);
        setContentView(R.layout.batsman_dialog);
        final EditText nameText=findViewById(R.id.new_batsman_name);
        Button add=findViewById(R.id.new_batsman_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameText.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(a, "Can't add blank name", Toast.LENGTH_SHORT).show();
                }else{
                    a.addBatsman(name,id,bat);
                    dismiss();
                }
            }
        });
    }
}
