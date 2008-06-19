package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NumberBox extends LinearLayout{
	private AutoCompleteTextView act;
	private ImageView view_up;
	private ImageView view_down;
	
	public NumberBox(Context c){
		super(c);
		act = new AutoCompleteTextView(c);
		act.setText("0");
		act.setGravity(Gravity.CENTER);
		
		view_up = new ImageView(c);
		view_up.setImageResource(R.drawable.arrow_up);
		view_up.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				int value = Integer.valueOf(act.getText().toString()) + 1;
				act.setText(String.valueOf(value));
			}
		});
		
		view_down = new ImageView(c);
		view_down.setImageResource(R.drawable.arrow_down);
		view_down.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				int value = Integer.valueOf(act.getText().toString()) - 1;
				if (value >= 0){
					act.setText(String.valueOf(value));
				}
			}
		});

		this.addView(act, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT, 90));
		this.addView(view_up, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT, 5));
		this.addView(view_down, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT, 5));
	}
	
	public String getValue(){
		return act.getText().toString();
	}
	
	public void setValue(Object v){
		act.setText(String.valueOf(v));
	}
}
