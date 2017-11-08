package app.sonu.com.musicplayer.ui.about;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    @BindView(R.id.projectGithubIv)
    ImageView projectGithubIv;

    @BindView(R.id.arLinkedinIv)
    ImageView arLinkedinIv;

    @BindView(R.id.arGithubIv)
    ImageView arGithubIv;

    @BindView(R.id.arInstagramIv)
    ImageView arInstagramIv;

    @BindView(R.id.doneBtn)
    Button doneBtn;

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

        projectGithubIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/amanshuraikwar/musicplayer"));
                startActivity(intent);
            }
        });

        arLinkedinIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.linkedin.com/in/amanshu-raikwar-36b534103/"));
                startActivity(intent);
            }
        });

        arGithubIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/amanshuraikwar"));
                startActivity(intent);
            }
        });

        arInstagramIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://instagram.com/amanshuraikwar"));
                startActivity(intent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
