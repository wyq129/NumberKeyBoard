package com.example.aiiage.keyboard.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.aiiage.keyboard.KeyboardAdapter;
import com.example.aiiage.keyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang.yq@aiiage.com
 */
public class KeyboardView extends RelativeLayout {
    public RecyclerView recyclerView;
    private List<String> keyboardWords;
    private KeyboardAdapter adapter;
    private Animation animationIn;
    private Animation animationOut;
    private RelativeLayout rl_back;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_key_board, this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { // 点击关闭键盘
                dismiss();
            }
        });
        initData();
        initView();
        initAnimation();
    }

    /**
     * 填充数据
     */
    private void initData() {
        keyboardWords = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (i < 9) {
                keyboardWords.add(String.valueOf(i + 1));
            } else if (i == 9) {
                keyboardWords.add("");
            } else if (i == 10) {
                keyboardWords.add("0");
            } else {
                keyboardWords.add("");
            }
        }
    }

    /**
     * 设置适配器
     */
    private void initView() {
        int spanCount = 12;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spanCount));
        adapter = new KeyboardAdapter(getContext(), keyboardWords);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化动画效果
     */
    private void initAnimation() {
        animationIn = AnimationUtils.loadAnimation(getContext(), R.anim.keyboard_in);
        animationOut = AnimationUtils.loadAnimation(getContext(), R.anim.keyboard_out);
    }

    /**
     * 弹出软键盘
     */
    public void show() {
        startAnimation(animationIn);
        setVisibility(VISIBLE);
    }

    /**
     * 关闭软键盘
     */
    public void dismiss() {
        if (isVisible()) {
            startAnimation(animationOut);
            setVisibility(GONE);
        }
    }

    /**
     * 判断软键盘的状态
     */
    public boolean isVisible() {
        if (getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }

    public void setOnKeyBoardClickListener(KeyboardAdapter.OnKeyboardClickListener listener) {
        adapter.setOnKeyboardClickListener(listener);
    }

    public List<String> getKeyboardWords() {
        return keyboardWords;
    }

    public RelativeLayout getRlBack() {
        return rl_back;
    }

    /**
     * item分割线
     */
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.bottom = space;
            outRect.top = space;
            outRect.right = space;
        }
    }

}
