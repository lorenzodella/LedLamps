// Della Matera Lorenzo 5E

package com.example.ledlamps.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ledlamps.main.automations.AutomationsFragment;
import com.example.ledlamps.main.colors.ColorsFragment;
import com.example.ledlamps.main.home.HomeFragment;
import com.example.ledlamps.main.modes.ModesFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private HomeFragment homeFragment;
    private ColorsFragment colorsFragment;
    private ModesFragment modesFragment;
    private AutomationsFragment automationsFragment;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        homeFragment = new HomeFragment();
        colorsFragment = new ColorsFragment();
        modesFragment = new ModesFragment();
        automationsFragment = new AutomationsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return homeFragment;
            case 1:
                return colorsFragment;
            case 2:
                return modesFragment;
            case 3:
                return automationsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public ColorsFragment getColorsFragment() {
        return colorsFragment;
    }

    public ModesFragment getModesFragment() {
        return modesFragment;
    }

    public AutomationsFragment getAutomationsFragment() {
        return automationsFragment;
    }
}