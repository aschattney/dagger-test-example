package dagger.extension.example.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Map;

import javax.inject.Inject;

import dagger.AllowStubGeneration;
import dagger.Replaceable;
import dagger.extension.example.di.scope.ActivityScope;
import dagger.extension.example.view.search.SearchFragment;
import dagger.extension.example.view.weather.TodayWeatherFragment;
import dagger.extension.example.view.weather.TomorrowWeatherFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public static final int POSITION_TODAY = 0;
    public static final int POSITION_TOMORROW = 1;
    public static final int POSITION_SEARCH = 2;

    private static final int PAGE_COUNT = 3;

    private final Map<Integer, String> pageTitles;

    @Inject @Replaceable
    public SectionsPagerAdapter(FragmentManager fm, Map<Integer, String> pageTitles) {
        super(fm);
        this.pageTitles = pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new TodayWeatherFragment();
            case 1:
                return new TomorrowWeatherFragment();
            case 2:
                return new SearchFragment();
            default:
                throw new IllegalArgumentException(String.format("invalid position : %d", position));
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = pageTitles.get(position);
        if (title == null)
        {
            throw new IllegalArgumentException(String.format("invalid position : %d", position));
        }
        return title;
    }
}
