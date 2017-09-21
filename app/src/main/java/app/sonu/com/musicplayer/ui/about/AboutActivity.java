package app.sonu.com.musicplayer.ui.about;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;

import app.sonu.com.musicplayer.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 31/8/17.
 */

public class AboutActivity extends AppCompatActivity{

    @BindView(R.id.elasticDragDismissLayout)
    ElasticDragDismissFrameLayout elasticDragDismissLayout;

    @BindView(R.id.colorView)
    View colorView;

    int colors[] = {
            Color.parseColor("#ffffff"),
            Color.parseColor("#eeeeee"),
            Color.parseColor("#eeeeee"),
            Color.parseColor("#ECEFF1")};

    ValueAnimator colorAnimation = ValueAnimator
            .ofObject(new ArgbEvaluator(),
                    colors[0],
                    colors[1],
                    colors[2],
                    colors[3],
                    colors[0]);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        elasticDragDismissLayout.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset,
                               float elasticOffsetPixels,
                               float rawOffset,
                               float rawOffsetPixels) {

            }

            @Override
            public void onDragDismissed() {
                finish();
            }
        });

        colorAnimation.setDuration(10000); // milliseconds
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                colorView.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimation.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        colorAnimation.cancel();
        colorAnimation.end();
    }
}
