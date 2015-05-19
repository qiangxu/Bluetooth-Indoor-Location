package com.example.project_g.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class MultiSelectDialog {
    private String title;
    private ArrayList<String> list;
    private boolean[] selected;
    
    private OnSelectedListener listener;
    
    public MultiSelectDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public MultiSelectDialog addItem(String item) {
        if(this.list == null)
            this.list = new ArrayList<String>();
        this.list.add(item);
        return this;
    }
    
    public MultiSelectDialog setOnClickListener(OnSelectedListener listener) {
        this.listener = listener;
        return this;
    }
    
    public Dialog build(Context context) {
        Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        DialogInterface.OnMultiChoiceClickListener mutiListener =   
            new DialogInterface.OnMultiChoiceClickListener() {
                  
                @Override  
                public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                    selected[which] = isChecked;
                }
            };
        String[] itemList = new String[this.list.size()];
        this.selected = new boolean[this.list.size()];
        for (int i = 0; i < itemList.length; i++) {
            itemList[i] = this.list.get(i).toString();
        }
        builder.setMultiChoiceItems(itemList, selected, mutiListener);
        DialogInterface.OnClickListener btnListener =   
            new DialogInterface.OnClickListener() {
                @Override  
                public void onClick(DialogInterface dialogInterface, int which) {
                    if(listener != null)
                        listener.onSelected(selected);
                }
            };
        builder.setPositiveButton("OK", btnListener);
        return builder.create();
    }
    
    public interface OnSelectedListener {
        public void onSelected(boolean[] selection);
    }
}
