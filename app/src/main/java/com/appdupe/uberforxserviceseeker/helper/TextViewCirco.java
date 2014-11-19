package com.appdupe.uberforxserviceseeker.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class TextViewCirco extends TextView {

	public TextViewCirco(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont();

	}

	public TextViewCirco(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont();
	}

	public TextViewCirco(Context context) {
		super(context);
		setCustomFont();
	}

	private void setCustomFont() {
		super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
				"fonts/ROBOTOLIGHT.TTF"));
	}

}
