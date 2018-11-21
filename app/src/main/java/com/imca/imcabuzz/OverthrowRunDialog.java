package com.imca.imcabuzz;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

public class OverthrowRunDialog extends Dialog {
    public OverthrowRunDialog(final ScoreActivity a) {
        super(a);
        setContentView(R.layout.run_dialog);
        TextView zeroText=findViewById(R.id.dialog_zero);
        TextView oneText=findViewById(R.id.dialog_one);
        TextView twoText=findViewById(R.id.dialog_two);
        TextView threeText=findViewById(R.id.dialog_three);
        TextView fourText=findViewById(R.id.dialog_four);
        TextView sixText=findViewById(R.id.dialog_six);
        zeroText.setVisibility(View.GONE);
        sixText.setVisibility(View.GONE);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t=(TextView)view;
                int run=Integer.parseInt(t.getText().toString());
                dismiss();
                a.addOverThrow(run);
            }
        };
        zeroText.setOnClickListener(listener);
        oneText.setOnClickListener(listener);
        twoText.setOnClickListener(listener);
        threeText.setOnClickListener(listener);
        fourText.setOnClickListener(listener);
        sixText.setOnClickListener(listener);
    }
}
