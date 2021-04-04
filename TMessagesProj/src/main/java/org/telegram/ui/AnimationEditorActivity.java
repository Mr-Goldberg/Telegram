package org.telegram.ui;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

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

    private static final int TYPE_TEXT = 0;

    ViewPagerFixed viewPager;

    AnimationSettingsStorage settingsStorage = new AnimationSettingsStorage();
    AnimationSettings animationSettings;

    @Override
    public View createView(Context context) {
        settingsStorage.setContext(context);
        animationSettings = settingsStorage.load();

        actionBar.setTitle("Animation Settings");
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {
                switch (id) {
                    case -1:
                        finishFragment();
                        break;
                    case 3:
                        animationSettings = new AnimationSettings();
                        settingsStorage.save(animationSettings);
                        viewPager.notifyDataSetChanged();
                        break;
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem headerItem = menu.addItem(0, R.drawable.ic_ab_other);
        headerItem.addSubItem(1, "Share Parameters");
        headerItem.addSubItem(2, "Import Parameters");
        headerItem.addSubItem(3, "Restore to Default");

        viewPager = new ViewPagerFixed(context);
        viewPager.setAdapter(new ViewPagerFixed.Adapter() {

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public String getItemTitle(int position) {
                return "Text";
            }

            @Override
            public View createView(int viewType) {
                View view;
                switch (viewType) {
                    case TYPE_TEXT:
                        view = LayoutInflater.from(context).inflate(R.layout.animation_settings_text, null);
                        break;
                    default:
                        return null;
                }

                // TODO setup layout style

                return view;
            }

            @Override
            public int getItemCount() {
                return 1;
            }

            @Override
            public void bindView(View view, int position, int viewType) {
                Log.d(TAG, "bindView() " + position);
                // Text
                if (viewType == TYPE_TEXT) {
                    Button durationButton = view.findViewById(R.id.durationButton);
                    durationButton.setText(formatMs(getDuration(viewType)));
                    durationButton.setOnClickListener(button -> showDurationPopup(context, durationButton, viewType));
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

    private void showDurationPopup(Context context, View anchor, int settingsType) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        Menu menu = popupMenu.getMenu();
        for (int duration : DURATION_ITEMS) {
            menu.add(Menu.NONE, duration, Menu.NONE, formatMs(duration));
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id >= DURATION_ITEMS[0] && id <= DURATION_ITEMS[DURATION_ITEMS.length - 1]) {
                Log.d(TAG, "onItemClick() type " + settingsType + " id " + id);
                switch (settingsType) {
                    case TYPE_TEXT:
                        animationSettings.textDuration = id;
                        break;
                    default:
                        Log.e(TAG, "showDurationPopup() Bad type: " + settingsType);
                        return true;
                }
                viewPager.notifyDataSetChanged();
                settingsStorage.save(animationSettings);
            }

            return true;
        });

        popupMenu.show();
    }

    private int getDuration(int type) {
        switch (type) {
            case TYPE_TEXT:
                return animationSettings.textDuration;
            default:
                Log.e(TAG, "getDuration() Bad type: " + type);
                return -1;
        }
    }

    private static String formatMs(int ms) {
        return String.format(Locale.US, "%dms", ms);
    }
}
