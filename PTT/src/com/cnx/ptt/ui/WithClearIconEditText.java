package com.cnx.ptt.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class WithClearIconEditText extends EditText {
	private Drawable dRight;
	private Rect rBounds;

	public WithClearIconEditText(Context context) {
		super(context);
		initEditText();
	}

	public WithClearIconEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initEditText();
	}

	public WithClearIconEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initEditText();
	}
	/**
	 * 初始化edittext 组件
	 */
	private void initEditText() {
		setEditTextDrawable();
		//对内容添加监听
		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setEditTextDrawable();
			}
		});
	}
	/**
	 * 控制图片的显示
	 * 显示顺序  left, top, right, bottom
	 */
	private void setEditTextDrawable() {
		if (getText().toString().length() == 0) {
			setCompoundDrawables(null, null, null, null);
		} else {
			setCompoundDrawables(null, null, this.dRight, null);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		this.dRight = null;
		this.rBounds = null;
	}
	/**
	 * 显示在右侧出现的图片
	 */
	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		if (right != null)  
            this.dRight = right;  
        super.setCompoundDrawables(left, top, right, bottom);
		
	}
	 /** 
     * 添加触摸事件 点击之后 出现 清空editText的效果 
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {  
        if ((this.dRight != null) && (paramMotionEvent.getAction() == 1)) {  
            this.rBounds = this.dRight.getBounds();  
            int i = (int) paramMotionEvent.getRawX();// 距离屏幕的距离  
            // int i = (int) paramMotionEvent.getX();//距离边框的距离  
            if (i > getRight() - 3 * this.rBounds.width()) {  
                setText("");  
                paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);  
            }  
        }  
        return super.onTouchEvent(paramMotionEvent);  
    }  
}
