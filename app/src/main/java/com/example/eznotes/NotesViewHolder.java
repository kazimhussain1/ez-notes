package com.example.eznotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NotesViewHolder extends RecyclerView.ViewHolder {


    TextView myTitleText;
    TextView myDescriptionText;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        myTitleText = (TextView) itemView.findViewById(R.id.cell_titleText);
        myDescriptionText = (TextView) itemView.findViewById(R.id.cell_descriptionText);

    }

    public void bindData(String title, String desc){

        myTitleText.setText(title);
        myDescriptionText.setText(desc);
        itemView.setBackground(itemView.getContext().getDrawable(R.drawable.cell_bg));
}



}
