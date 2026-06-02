package com.example.debugapkdisplaypageflip;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private static final float TARGET_REFRESH_RATE_HZ = 60.0f;

    private final int[] backgroundColors = new int[] {
            Color.RED,
            Color.YELLOW,
            Color.BLUE
    };
    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (!autoRefreshRunning) {
                return;
            }

            showNextColor();
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    private FrameLayout rootLayout;
    private int colorIndex = 0;
    private boolean autoRefreshRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        configureFullscreenWindow();

        rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setOnClickListener(view -> showNextColor());
        configureRootViewFor60Hz();

        setContentView(rootLayout);
        hideSystemUi();
        updateBackgroundColor();
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
    protected void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUi();
        }
    }

    private void showNextColor() {
        colorIndex = (colorIndex + 1) % backgroundColors.length;
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        rootLayout.setBackgroundColor(backgroundColors[colorIndex]);
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

    private void configureRootViewFor60Hz() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            rootLayout.setFrameRate(
                    TARGET_REFRESH_RATE_HZ,
                    Surface.FRAME_RATE_COMPATIBILITY_FIXED_SOURCE
            );
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
}
