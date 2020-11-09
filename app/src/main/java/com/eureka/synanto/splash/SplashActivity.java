package com.eureka.synanto.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.eureka.synanto.LoginActivity;
import com.eureka.synanto.MainActivity;
import com.eureka.synanto.R;

import static com.eureka.synanto.utility.Functions.getSession;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSession(this).getString("userID", null) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setAnimation();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        KenBurnsView mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mKenBurns.setImageResource(R.drawable.bg_synanto);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSession(this).getString("userID", null) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setAnimation() {
        //TextView: Appointment Organizer and Map Navigator
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();

        //Synanto Logo Square Picture
        findViewById(R.id.imagelogo).setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        findViewById(R.id.imagelogo).startAnimation(anim);

        //Double tap to start Main Activity
        ObjectAnimator scaleXAnimationDtap = ObjectAnimator.ofFloat(findViewById(R.id.double_tap), "scaleX", 1.0F, 1.0F);
        scaleXAnimationDtap.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimationDtap.setDuration(2200);

        ObjectAnimator scaleYAnimationDtap = ObjectAnimator.ofFloat(findViewById(R.id.double_tap), "scaleY", 1.0F, 1.0F);
        scaleYAnimationDtap.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimationDtap.setDuration(2200);

        ObjectAnimator alphaAnimationDtap = ObjectAnimator.ofFloat(findViewById(R.id.double_tap), "alpha", 0.0F, 1.0F);
        alphaAnimationDtap.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimationDtap.setDuration(2200);

        AnimatorSet animatorSetDtap = new AnimatorSet();
        animatorSetDtap.play(scaleXAnimationDtap).with(scaleYAnimationDtap).with(alphaAnimationDtap);
        animatorSetDtap.setStartDelay(1000);
        animatorSetDtap.start();
    }


    public void toLoginActivity(View view) {
        Intent toLoginPage = new Intent(this, LoginActivity.class);
        startActivity(toLoginPage);
        finish();
    }
}
