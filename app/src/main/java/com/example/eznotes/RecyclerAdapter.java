package com.example.eznotes;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter {

    private View.OnClickListener myClickListener;
    private View.OnLongClickListener myLongClickListener;

    public void setClickListener(View.OnClickListener callback) {
        myClickListener = callback;
    }

    public void setLongClickListener(View.OnLongClickListener callback) {
        myLongClickListener = callback;
    }


    private ArrayList<String> titles;
    private ArrayList<String> description;
    private ArrayList<Long> dateModified;
    private int resource;
    private Context myContext;


    public RecyclerAdapter(ArrayList<String> titles, ArrayList<String> description, ArrayList<Long> dateModified, int resource, Context myContext) {
        super();
        this.titles = titles;
        this.description = description;
        this.dateModified = dateModified;
        this.resource = resource;
        this.myContext = myContext;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public ArrayList<Long> getDateModified() {
        return dateModified;
    }

    public void setDateModified(ArrayList<Long> dateModified) {
        this.dateModified = dateModified;
    }

    public void pushNote(int position, String title, String desc, long date){

        this.titles.remove(position);
        this.description.remove(position);
        this.dateModified.remove(position);

        this.titles.add(0,title);
        this.description.add(0, desc);
        this.dateModified.add(0, date);


        notifyDataSetChanged();
    }

    public void pushNote(String title, String desc, long date){
        this.titles.add(0,title);
        this.description.add(0,desc);
        this.dateModified.add(0,date);

        notifyDataSetChanged();
    }

    public void deleteSelected(ArrayList<Integer> myList){

        //If an item is directly deleted using the indexes in
        // myList then title and description list will change
        // effectively disrupting the order of the lists

        for (int i=0; i<myList.size();i++) {
            int j = myList.get(i);
            titles.set(j,"");
            description.set(j,"");
            dateModified.set(j,new Long(0));
        }


        for (int i =0;i<titles.size();){
            if (titles.get(i).equals("") && description.get(i).equals("") && dateModified.get(i) == 0){
                titles.remove(i);
                description.remove(i);
                dateModified.remove(i);
            }
            else {
                i++;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View myCell = LayoutInflater.from(viewGroup.getContext()).inflate(i,viewGroup,false);

        myCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onClick(v);
            }
        });

        myCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                myLongClickListener.onLongClick(v);

                return true;
            }
        });

        return new NotesViewHolder(myCell);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((NotesViewHolder)viewHolder).bindData(titles.get(i),description.get(i));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.cell;
    }
}

