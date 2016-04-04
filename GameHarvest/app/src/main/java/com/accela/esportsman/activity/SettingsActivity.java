package com.accela.esportsman.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.accela.esportsman.R;
import com.accela.esportsman.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */

public class SettingsActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ImageView backView = (ImageView) findViewById(R.id.imageBackId);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_settings);
        }


        /**
         * Called when a shared preference is changed, added, or removed. This
         * may be called even if a preference is set to its existing value.
         * <p/>
         * <p>This callback will be run on your main thread.
         *
         * @param sharedPreferences The {@link SharedPreferences} that received
         *                          the change.
         * @param key               The key of the preference that was changed, added, or
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_environment")) {
                String environmentStr = sharedPreferences.getString(key, "");
                Utils.getEnvironment(environmentStr);
                Toast.makeText(getActivity(), getResources().getString(R.string.change_environment_warning), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}


