// Della Matera Lorenzo 5E

package com.example.ledlamps.main;

import android.content.Intent;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.ledlamps.R;
import com.example.ledlamps.settings.SettingsActivity;
import com.example.ledlamps.user.UserActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MeowBottomNavigation bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_person_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewpager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        SynchronizableFragment synchronizableFragment = (SynchronizableFragment) adapter.getItem(0);
        synchronizableFragment.setShowToast(false);
        synchronizableFragment.sync();

        bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.show(0, true);
        bottom_nav.add(new MeowBottomNavigation.Model(0,R.drawable.ic_baseline_home_24));
        bottom_nav.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_color_lens_24));
        bottom_nav.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_space_dashboard_24));
        bottom_nav.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_format_list_bulleted_24));

        bottom_nav.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                viewPager.setCurrentItem(model.getId());
                return null;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SynchronizableFragment synchronizableFragment = (SynchronizableFragment) adapter.getItem(position);
                synchronizableFragment.setShowToast(false);
                synchronizableFragment.sync();
                bottom_nav.show(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == android.R.id.home){
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}