package malathkar.akshay.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    Animation topAnim, bottomAnim, horizontalAnim;
    ImageView ivContacts,ivImage;
    View progressBar;
    TextView tvText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.down_animation);
        horizontalAnim = AnimationUtils.loadAnimation(this, R.anim.horizontal_anim);

        progressBar = findViewById(R.id.progressBar);
        ivContacts = findViewById(R.id.ivContact);
        ivImage = findViewById(R.id.ivImage);
        tvText = findViewById(R.id.tvText);

        progressBar.setAnimation(horizontalAnim);
        ivContacts.setAnimation(topAnim);
        ivImage.setAnimation(bottomAnim);
        tvText.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,malathkar.akshay.contacts.Login.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}