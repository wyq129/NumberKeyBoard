package com.example.aiiage.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aiiage.keyboard.view.ClickableEditText;
import com.example.aiiage.keyboard.view.KeyboardView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wangyanqin
 */
public class SecondNumberActivity extends Activity implements View.OnClickListener,
        KeyboardAdapter.OnKeyboardClickListener, ClickableEditText.DrawableRightListener {
    private final String TAG = "SecondNumberActivity";
    ClickableEditText videoIdEt;
    /**
     * Custom key board
     */
    KeyboardView keyboardView;

    /**
     * Handle touch event to change the position of curator
     */
    GestureDetector mDetector;
    float startPointEvent, stopPointEvent;
    int curatorIndex;
    Drawable mDrawable;
    /**
     * Update start position
     */
    private boolean refreshStartPoint = true;
    private int mScreenWidth = 0;
    private List<String> keyboardNumbers;
    private boolean isTextChanging = false;
    /**
     * Get the start and stop position:
     * 1. Start position: the first event of 'ACTION_MOVE', because of 'ACTION_DOWN' event is gone
     * 2. Stop position: the last event of 'ACTION_MOVE'
     */
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "MotionEvent.ACTION_DOWN");
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, "MotionEvent.ACTION_UP");
                refreshStartPoint = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (refreshStartPoint) {
                    startPointEvent = motionEvent.getX(0);
                    curatorIndex = videoIdEt.getSelectionStart();
                }
                refreshStartPoint = false;
                stopPointEvent = motionEvent.getX();
                Log.d(TAG, "MotionEvent.ACTION_MOVE");
            }
            return mDetector.onTouchEvent(motionEvent);
        }
    };
    /**
     * Insert whitespace at every three number
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int i2) {
            if (isTextChanging) {
                isTextChanging = false;
                return;
            }
            isTextChanging = true;
            String result = "";
            String newStr = charSequence.toString();
            newStr = newStr.replace(" ", "");
            int index = 0;
            while ((index + 3) < newStr.length()) {
                result += (newStr.substring(index, index + 3) + " ");
                index += 3;
            }
            result += (newStr.substring(index, newStr.length()));
            int i = videoIdEt.getSelectionStart();
            videoIdEt.setText(result);
            try {
                if (i % 4 == 0 && before == 0) {
                    if (i + 1 <= result.length()) {
                        videoIdEt.setSelection(i + 1);
                    } else {
                        videoIdEt.setSelection(result.length());
                    }
                } else if (before == 1 && i < result.length()) {
                    videoIdEt.setSelection(i);
                } else if (before == 0 && i < result.length()) {
                    videoIdEt.setSelection(i);
                } else {
                    videoIdEt.setSelection(result.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Require window feature
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_second_number);
        initViews();
    }

    private void initViews() {
        // int views
        videoIdEt = (ClickableEditText) findViewById(R.id.ai_long_et_video_code);
        keyboardView = (KeyboardView) findViewById(R.id.tv_key);
        mDrawable = getResources().getDrawable(R.drawable.ic_search);
        // set view properties
        videoIdEt.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
        // set listeners
        videoIdEt.setDrawableRightListener(this);
        videoIdEt.addTextChangedListener(watcher);
        findViewById(R.id.ai_long_tv_confirm).setOnClickListener(this);
        videoIdEt.setOnClickListener(this);
        keyboardView.setOnKeyBoardClickListener(this);
        keyboardView.recyclerView.setOnTouchListener(onTouchListener);
        // keyboard setting
        initGestureDetector();
        keyboardNumbers = keyboardView.getKeyboardWords();
        enableSystemKeyboard();
    }

    // ------------------------ Handle keyboard touch event start ------------------------

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ai_long_tv_confirm:
                Toast.makeText(this, "确定，点击成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ai_long_et_video_code:
                if (!keyboardView.isVisible()) {
                    keyboardView.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Keyboard number key onclick listener
     *
     * @param view     The number key
     * @param holder   The number adapter holder
     * @param position The number key position
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onKeyClick(View view, RecyclerView.ViewHolder holder, int position) {
        String editText = videoIdEt.getText().toString();
        switch (position) {
            case 9:
                break;
            default:
                //获取当前光标位置
                int index = videoIdEt.getSelectionStart();
                if (index != videoIdEt.getText().length()) {
                    //在光标处插入数字
                    String inputEditText = keyboardNumbers.get(position);
                    Log.d(TAG, "inputEditText:" + inputEditText);
                    videoIdEt.getText().insert(index, inputEditText);
                } else {
                    videoIdEt.setText(videoIdEt.getText().toString().trim() + keyboardNumbers.get(position));
                    videoIdEt.setSelection(videoIdEt.getText().length());
                }
                if (videoIdEt.getText().length() > 0) {
                    //设置按钮颜色以及右删除图标为可见
                    findViewById(R.id.ai_long_tv_confirm).setBackgroundResource(R.drawable.bg_determine_text);
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_delete1);
                    videoIdEt.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, drawable, null);
                }
                break;
        }
    }

    // ------------------------ Handle keyboard touch event start ------------------------

    /**
     * 1. Calculate the distance between start position and stop position
     * 2. Get the direction of gesture
     * 3. Set a new cursor position of EditText by the distance and direction
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void initGestureDetector() {
        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                Log.d(TAG, "onDown");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                Log.d(TAG, "onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                Log.d(TAG, "onShowPress");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                mScreenWidth = displayMetrics.widthPixels;
                float x = stopPointEvent - startPointEvent;
                float newPercent = Math.abs(x) / mScreenWidth;
                int range = (int) ((videoIdEt.getText().length()) * newPercent);
                int newIndex;
                if (x > 0) {
                    newIndex = curatorIndex + range;
                    if (curatorIndex == videoIdEt.getText().length()) {
                        videoIdEt.setSelection(curatorIndex);
                    }
                    if (newIndex > videoIdEt.getText().length()) {
                        videoIdEt.setSelection(videoIdEt.getText().length());
                    } else {
                        videoIdEt.setSelection(newIndex);
                    }
                } else {
                    newIndex = curatorIndex - range;
                    if (curatorIndex == 0) {
                        videoIdEt.setSelection(curatorIndex);
                    }
                    if (newIndex < 0) {
                        videoIdEt.setSelection(0);
                    } else {
                        videoIdEt.setSelection(newIndex);
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                Log.d(TAG, "onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                // startPointEvent=motionEvent1;
                Log.d(TAG, "onFling: " + "motionEvent:" + motionEvent + "motionEvent1" + motionEvent1);
                return false;
            }
        });
    }

    /**
     * Click on delete key of keyboard
     *
     * @param view     Clicked key
     * @param holder   Clicked adapter holder
     * @param position Clicked position
     */
    @Override
    public void onDeleteClick(View view, RecyclerView.ViewHolder holder, int position) {
        int currentIndex = videoIdEt.getSelectionStart();
        if (currentIndex > 0) {
            videoIdEt.getText().delete(currentIndex - 1, currentIndex);
            if (videoIdEt.getText().length() == 0) {
                videoIdEt.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
                findViewById(R.id.ai_long_tv_confirm).setBackgroundResource(R.drawable.bg_determine_disable);
            }
        }
    }

    /**
     * Disable system keyboard: API > 10
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void enableSystemKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(videoIdEt, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * On click edit text right delete view
     */
    @Override
    public void onDrawableRightClick(View view) {
        switch (view.getId()) {
            case R.id.ai_long_et_video_code:
                videoIdEt.getText().clear();
                videoIdEt.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
                findViewById(R.id.ai_long_tv_confirm).setBackgroundResource(R.drawable.bg_determine_disable);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (keyboardView.isVisible()) {
            keyboardView.dismiss();
            return;
        }
        super.onBackPressed();
    }
}