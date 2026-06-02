package com.example.debugapkdisplaypageflip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final float TARGET_REFRESH_RATE_HZ = 60.0f;
    private static final float LINE_WIDTH_PX = 6.0f;
    private static final float LINE_SPEED_PX_PER_FRAME = 12.0f;

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (!autoRefreshRunning) {
                return;
            }

            movingLineView.advanceFrame();
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    private MovingLineView movingLineView;
    private boolean autoRefreshRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        configureFullscreenWindow();

        movingLineView = new MovingLineView(this);
        movingLineView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        movingLineView.setOnClickListener(view -> movingLineView.advanceFrame());
        setContentView(movingLineView);
        hideSystemUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        startAutoRefresh();
    }

    @Override
    protected void onPause() {
        stopAutoRefresh();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUi();
        }
    }

    private void startAutoRefresh() {
        if (autoRefreshRunning) {
            return;
        }

        autoRefreshRunning = true;
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    private void stopAutoRefresh() {
        autoRefreshRunning = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    private void configureFullscreenWindow() {
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.preferredRefreshRate = TARGET_REFRESH_RATE_HZ;
        window.setAttributes(layoutParams);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        }
    }

    private void hideSystemUi() {
        View decorView = getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    private static final class MovingLineView extends View {
        private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float lineCenterX = LINE_WIDTH_PX / 2.0f;

        MovingLineView(Context context) {
            super(context);
            setBackgroundColor(Color.BLACK);
            setWillNotDraw(false);
            setFocusable(true);
            setClickable(true);
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(LINE_WIDTH_PX);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeCap(Paint.Cap.SQUARE);
        }

        void advanceFrame() {
            int width = getWidth();
            if (width > 0) {
                lineCenterX += LINE_SPEED_PX_PER_FRAME;
                if (lineCenterX > width + LINE_WIDTH_PX) {
                    lineCenterX = LINE_WIDTH_PX / 2.0f;
                }
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLACK);
            canvas.drawLine(lineCenterX, 0.0f, lineCenterX, getHeight(), linePaint);
        }
    }
}
