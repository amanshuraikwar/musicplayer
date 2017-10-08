package app.sonu.com.musicplayer.ui.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 8/10/17.
 */

public class MainIntroActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    @BindView(R.id.givePermissionsTv)
    TextView givePermissionsTv;

    @BindView(R.id.givePermissionsBtn)
    Button givePermissionsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            givePermissionsBtn.setVisibility(View.GONE);
            givePermissionsTv.setText("Enjoy the material music!");

        }

        givePermissionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainIntroActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("disable_first_time_flag", true);
                    Intent intent = new Intent(MainIntroActivity.this, MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    finish();

                } else {
                    Toast.makeText(this, "Please give permissions!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
