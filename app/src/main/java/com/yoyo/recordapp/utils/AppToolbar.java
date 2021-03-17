package com.yoyo.recordapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.yoyo.recordapp.R;


public class AppToolbar extends Toolbar {
    private EditTextListener mEditTextListener;
    private TextView mRightTv;
    private boolean isTitleUpperCase;
    private ImageView mLeftView;
    private ImageView mRightView;
    private TextView mTitleView;
    private ViewGroup mLayout;
    private ImageView mSubRightView;
    private OnRightClickListener mOnRightClickListener;
    private OnSubRightClickListener mOnSubRightClickListener;

    private void setLeftIcon(Drawable rightIcon) {
        mLeftView.setImageDrawable(rightIcon);
    }

    public AppToolbar(Context context) {
        this(context, null);
    }

    public AppToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

        setContentInsetsRelative(0, 0);

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppToolbar, defStyleAttr, 0);

            Drawable rightIcon = a.getDrawable(R.styleable.AppToolbar_rightIcon);
            Drawable rightSubIcon = a.getDrawable(R.styleable.AppToolbar_righSubIcon);
            Drawable leftIcon = a.getDrawable(R.styleable.AppToolbar_leftIcon);
            if (rightIcon != null) {
                setRightIcon(rightIcon);
            }
            if (leftIcon != null) {
                setLeftIcon(leftIcon);
            }
            if (rightSubIcon != null) {
                setRightSbuIcon(rightSubIcon);
            }

            CharSequence titleText = a.getText(R.styleable.AppToolbar_title);
            CharSequence RightText = a.getText(R.styleable.AppToolbar_rightText);
            if (!TextUtils.isEmpty(RightText)) {
                mRightTv.setText(RightText);
            }
            isTitleUpperCase = a.getBoolean(R.styleable.AppToolbar_isTitleUpperCase, false);
            if (isTitleUpperCase) {
                mTitleView.setAllCaps(isTitleUpperCase);
            }
            mTitleView.setText(titleText);

            boolean hideLeftView = a.getBoolean(R.styleable.AppToolbar_isHideLeftView, false);
            setHideLeftView(hideLeftView);

            int background =
                    a.getColor(R.styleable.AppToolbar_BackgroundColor, context.getResources().getColor(R.color.white));
            mLayout.setBackgroundColor(background);

            int titleSize = a.getDimensionPixelSize(R.styleable.AppToolbar_titleSize, 18);
            setTitleTextSize(titleSize);

            int color =a.getColor(R.styleable.AppToolbar_titleColor, context.getResources().getColor(R.color.color333333));
            setTitleColor(color);

            int rightColor = a.getColor(R.styleable.AppToolbar_rightTextColor,
                    context.getResources().getColor(R.color.color333333));
            setRightColor(rightColor);

            a.recycle();
        }
    }

    public void setSubRightViewRes(@DrawableRes int res) {
        if (res != 0) {
            mSubRightView.setImageResource(res);
            mSubRightView.setVisibility(VISIBLE);
        }
    }

    public void setOnRightTvClickListener(OnRightClickListener listener) {
        this.mOnRightClickListener = listener;
        mRightTv.setOnClickListener(listener::onRightClick);
    }

    private void initView() {
        final ViewGroup mView = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.toolbar, this);
        mLeftView = mView.findViewById(R.id.iv_left);
        mRightView = mView.findViewById(R.id.iv_right);
        mTitleView = mView.findViewById(R.id.tvTitle);
        mLayout = mView.findViewById(R.id.tl_toolbar);
        mSubRightView = mView.findViewById(R.id.iv_sub_right);
        mRightTv = mView.findViewById(R.id.tv_right);
        initListener();
    }

    private void setTitleTextSize(int size) {
        mTitleView.setTextSize(size);
    }

    public TextView getTitleView() {
        return mTitleView;
    }
    private void setTitleColor(int color) {
        mTitleView.setTextColor(color);
    }

    private void setRightColor(int color) {
        mRightTv.setTextColor(color);
    }

    public void setRightTvClickable(boolean clickable){
        if (clickable){
            setRightColor(Color.parseColor("#FF9E00"));
        }else {
            setRightColor(Color.parseColor("#333333"));
        }
        mRightTv.setClickable(clickable);
    }

    private void setHideLeftView(boolean b) {
        if (b) {
            mLeftView.setVisibility(GONE);
        }
    }

    public void setRightViewVisible(boolean visible) {
        mRightView.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setTitle(int resId) {
        if (mTitleView != null) {
            mTitleView.setText(resId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    private void setRightIcon(Drawable rightIcon) {
        mRightView.setImageDrawable(rightIcon);
    }

    private void setRightSbuIcon(Drawable rightIcon) {
        mSubRightView.setImageDrawable(rightIcon);
        mSubRightView.setVisibility(VISIBLE);
    }

    public void setRightIcon(@DrawableRes int resId) {
        mRightView.setImageResource(resId);
    }

    public void setRightText(String text){
        mRightTv.setText(text);
    }

    public void setFragment(Fragment fragment) {
    }

    private void initListener() {
        if (mLeftView != null) {
            mLeftView.setOnClickListener(view -> {
                if (view.getContext() instanceof Activity) {
                    ((Activity) view.getContext()).onBackPressed();
                }
            });
        }

        if (mSubRightView != null) {
            mSubRightView.setOnClickListener(view -> {
                if (mOnSubRightClickListener != null) {
                    mOnSubRightClickListener.onSubRightClick(view);
                }
            });
        }
    }

    public interface OnRightClickListener {
        void onRightClick(View view);
    }

    public interface OnSubRightClickListener {
        void onSubRightClick(View view);
    }

    public interface EditTextListener {
        void onTextChanged(CharSequence s);
    }


    public void setOnEditTextListener(EditTextListener listener) {
        this.mEditTextListener = listener;
    }

    public void setOnSubRightClickListener(OnSubRightClickListener listener) {
        this.mOnSubRightClickListener = listener;
    }
    public void setOnRightClickListener(OnRightClickListener listener) {
        this.mOnRightClickListener = listener;
        mRightView.setOnClickListener(view -> mOnRightClickListener.onRightClick(view));
    }
}
