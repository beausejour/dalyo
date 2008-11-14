package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import com.penbase.dma.R;
import android.content.Context;
import android.widget.ImageButton;

public class PictureBoxView extends ImageButton {
	public PictureBoxView(Context context) {
		super(context);
		this.setImageResource(R.drawable.camera);
	}
}
