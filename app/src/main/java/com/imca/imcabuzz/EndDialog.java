package com.imca.imcabuzz;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EndDialog extends Dialog {
    public EndDialog(@NonNull final ScoreActivity a) {
        super(a);
        setContentView(R.layout.end_dialog);
        final EditText nameText=findViewById(R.id.end_feedback);
        Button add=findViewById(R.id.feedback_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameText.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(a, "Can't add blank name", Toast.LENGTH_SHORT).show();
                }else{
                    a.end(name);
                    dismiss();
                }
            }
        });
    }
}
