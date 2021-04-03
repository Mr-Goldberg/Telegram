package org.telegram.ui;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ViewPagerFixed;

import java.util.Locale;

class AnimationEditorActivity extends BaseFragment {

    private static final String TAG = "AnimationEditorActivity";
    private static final int[] DURATION_ITEMS = new int[]{200, 300, 400, 500, 600, 700, 800, 900, 1000, 1500, 2000, 3000};

    @Override
    public View createView(Context context) {
        actionBar.setTitle("Animation Settings");
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {

                if (id == -1) {
                    finishFragment();
                    return;
                }

                if (id >= DURATION_ITEMS[0] && id <= DURATION_ITEMS[DURATION_ITEMS.length - 1]) {
                    Log.d(TAG, "onItemClick() " + id);
                    // TODO Record duration
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem headerItem = menu.addItem(0, R.drawable.ic_ab_other);
        for (int duration : DURATION_ITEMS) {
            headerItem.addSubItem(duration, String.format(Locale.US, "%d ms", duration));
        }

        ViewPagerFixed viewPager = new ViewPagerFixed(context);
        viewPager.setAdapter(new ViewPagerFixed.Adapter() {

            @Override
            public String getItemTitle(int position) {
                return "Title 1";
            }

            @Override
            public View createView(int viewType) {
                View view = new View(context);
                return view;
            }

            @Override
            public int getItemCount() {
                return 3;
            }

            @Override
            public void bindView(View view, int position, int viewType) {
                if (position % 2 == 0) {
                    view.setBackgroundColor(Color.GRAY);
                } else {
                    view.setBackgroundColor(Color.GREEN);
                }
            }
        });

        FrameLayout contentView = new FrameLayout(context);
        contentView.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.BOTTOM, 0, 44, 0, 0));
        ViewPagerFixed.TabsView tabsView = viewPager.createTabsView();
        tabsView.updateColors();
        contentView.addView(tabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44, Gravity.TOP));

        return contentView;
    }
}
