package com.example.project_g.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PromptDialog {
    private String title;
    private OnInputListener listener;
    private EditText inputX;
    private EditText inputY;
    
    public PromptDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public PromptDialog setOnInputListener(OnInputListener listener) {
        this.listener = listener;
        return this;
    }
    
    public Dialog build(Context context) {
        inputX = new EditText(context);
        inputY = new EditText(context);
        inputX.setHint("X");
        inputY.setHint("Y");
        LinearLayout inputContainer = new LinearLayout(context);
        inputContainer.setOrientation(LinearLayout.VERTICAL);
        inputContainer.addView(inputX);
        inputContainer.addView(inputY);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(inputContainer);
        builder.setPositiveButton("OK", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onInput(inputX.getText().toString(), inputY.getText().toString());
            }
        });
        return builder.create();
    }
    
    public interface OnInputListener {
        public void onInput(String x, String y);
    }
}
