package il.ac.hit.gamersarc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private IntroViewPagerAdapter adapter;
    Button getStartedBtn;
    ArrayList<IntroItem> list;
    LinearLayout dots_layout;
    ImageView[] dots;
    Animation btnAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        if(getSp()){
            Intent intent = new Intent(com.example.runtime.IntroActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        list = new ArrayList<>();
        list.add(new IntroItem(R.string.welcome,R.drawable.page1new,R.string.find_someone,R.string.to_run_with,R.string.to_run_with));
        list.add(new IntroItem(R.string.create_profile,R.drawable.page2new,R.string.create1,R.string.create2,R.string.create3));
        list.add(new IntroItem(R.string.friends1,R.drawable.page3small,R.string.friend2,R.string.friend3,R.string.friend3));
        list.add(new IntroItem(R.string.event1,R.drawable.page4new,R.string.event2,R.string.event3,R.string.event3));

        viewPager = findViewById(R.id.viewPager);
        adapter = new IntroViewPagerAdapter(this,list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(changeListener);
        dots_layout=findViewById(R.id.dots_layout);
        getStartedBtn = findViewById(R.id.getStartedBtn);
        btnAnim =AnimationUtils.loadAnimation(this,R.anim.btn_anim);

        add_dots(0);
        setPosition(0);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.runtime.IntroActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                saveToSp();
            }
        });

    }

    public void add_dots(int position)
    {
        dots= new ImageView[4];
        dots_layout.removeAllViews();
        for(int i=0;i<dots.length;i++){
            dots[i]=new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(70,70);
            layoutParams.setMargins(2,2,2,2);
            dots[i].setLayoutParams(layoutParams);

            dots_layout.addView(dots[i]);

        }
        if(dots.length>0)
            dots[position].setImageDrawable(getResources().getDrawable(R.drawable.ic_brightness_1_black_24dp));
    }

    public void setPosition(int position){
       if(position == list.size()-1){
           getStartedBtn.setVisibility(View.VISIBLE);
           getStartedBtn.setAnimation(btnAnim);
       }
       else
           getStartedBtn.setVisibility(View.GONE);
    }


    ViewPager.OnPageChangeListener changeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setPosition(position);
            add_dots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void saveToSp(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.commit();
    }

    private boolean getSp(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        boolean isIntroActivityOpened = sp.getBoolean("isIntroOpened",false);
        return  isIntroActivityOpened;
    }
}
