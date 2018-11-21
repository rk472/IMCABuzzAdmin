package com.imca.imcabuzz;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BatsmanSelectDialog extends Dialog {
    public BatsmanSelectDialog(@NonNull final ScoreActivity a, String bat1, String bat2, final int score) {
        super(a);
        setContentView(R.layout.batsman_select);
        TextView bat1Text=findViewById(R.id.select_1);
        TextView bat2Text=findViewById(R.id.select_2);
        bat1Text.setText(bat1);
        bat2Text.setText(bat2);
        bat1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.addScore(score,true);
                dismiss();
            }
        });
        bat2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.addScore(score,false);
                dismiss();
            }
        });
    }
}
