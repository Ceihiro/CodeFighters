package com.codefighters.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.codefighters.R;

public class SplashActivity extends Activity {

    private VideoView   videoView;
    private View        fadeOverlay;
    private boolean     hasTransitioned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Fullscreen, no title, keep screen on ──────────────────────────────
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        // ── Root: pure black background ───────────────────────────────────────
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        // ── Video centered, aspect-ratio preserved (letterbox / pillarbox) ────
        //    Use RelativeLayout so we can center the VideoView without stretching
        RelativeLayout videoContainer = new RelativeLayout(this);
        videoContainer.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        );
        videoContainer.setLayoutParams(containerParams);

        videoView = new VideoView(this);
        // Center the VideoView inside the RelativeLayout; it will keep its own
        // aspect ratio because VideoView does NOT scale beyond its measured size.
        RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT   // fill height, width auto
        );
        vp.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(vp);
        videoContainer.addView(videoView);

        root.addView(videoContainer);

        // ── Full-screen fade overlay (starts transparent, fades to black) ─────
        fadeOverlay = new View(this);
        fadeOverlay.setBackgroundColor(Color.BLACK);
        fadeOverlay.setAlpha(0f);
        fadeOverlay.setVisibility(View.INVISIBLE);
        root.addView(fadeOverlay, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        setContentView(root);

        // ── Wire up video ─────────────────────────────────────────────────────
        try {
            Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.c0d3_4u_intro
            );
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(false);
                    videoView.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    fadeOutThenLaunch();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    launchGame();   // skip fade on error — just get to game
                    return true;
                }
            });

            // Tap anywhere to skip intro
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fadeOutThenLaunch();
                }
            });

        } catch (Exception e) {
            launchGame();
        }
    }

    // ── Fade to black (350 ms) then launch ───────────────────────────────────
    private void fadeOutThenLaunch() {
        if (hasTransitioned) return;

        fadeOverlay.setVisibility(View.VISIBLE);
        fadeOverlay.setAlpha(0f);

        ObjectAnimator fade = ObjectAnimator.ofFloat(fadeOverlay, "alpha", 0f, 1f);
        fade.setDuration(350);
        fade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                launchGame();
            }
        });
        fade.start();
    }

    // ── Start AndroidLauncher with NO slide animation ─────────────────────────
    private synchronized void launchGame() {
        if (!hasTransitioned) {
            hasTransitioned = true;
            try {
                if (videoView != null && videoView.isPlaying()) videoView.stopPlayback();
            } catch (Exception ignored) {}

            Intent intent = new Intent(SplashActivity.this, AndroidLauncher.class);
            startActivity(intent);

            // Suppress the default slide-in transition — instant black-to-game
            overridePendingTransition(0, 0);

            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (videoView != null && videoView.isPlaying()) videoView.pause();
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (videoView != null && !hasTransitioned) videoView.start();
        } catch (Exception ignored) {}
    }
}
