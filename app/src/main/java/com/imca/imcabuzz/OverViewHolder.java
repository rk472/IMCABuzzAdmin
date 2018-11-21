package com.imca.imcabuzz;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class OverViewHolder extends RecyclerView.ViewHolder {
    private TextView runText;
    public OverViewHolder(@NonNull View itemView) {
        super(itemView);
        runText=itemView.findViewById(R.id.row_run);
    }
    public void setRun(String run){
        runText.setText(run);
    }
}
