package com.imca.imcabuzz;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;

class RunDialog extends Dialog {
    RunDialog(@NonNull final ScoreActivity a, final String name, final int s) {
        super(a);
        setContentView(R.layout.run_dialog);
        TextView zeroText=findViewById(R.id.dialog_zero);
        TextView oneText=findViewById(R.id.dialog_one);
        TextView twoText=findViewById(R.id.dialog_two);
        TextView threeText=findViewById(R.id.dialog_three);
        TextView fourText=findViewById(R.id.dialog_four);
        TextView sixText=findViewById(R.id.dialog_six);
        sixText.setVisibility(GONE);
        if(!name.equalsIgnoreCase("wd"))
            zeroText.setVisibility(GONE);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t=(TextView)view;
                String score=t.getText().toString();
                a.addExtra(name,s,Integer.parseInt(score));
                dismiss();
            }
        };
        zeroText.setOnClickListener(listener);
        oneText.setOnClickListener(listener);
        twoText.setOnClickListener(listener);
        threeText.setOnClickListener(listener);
        fourText.setOnClickListener(listener);
    }
}
