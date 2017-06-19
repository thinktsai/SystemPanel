package com.ctos.systempanel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
	protected Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle("SystemPanel");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.onBackPressed();
            }
        });

        showFragment(PrefsFragment.TAG, false);
	}

	protected void showFragment(String tag, boolean addToBackStack) {
		PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentByTag(tag);
		if (fragment == null) {
			switch (tag) {
				case DatetimeFragment.TAG:
					fragment = new DatetimeFragment();
					toolbar.setTitle("Date & Time");
					break;
                case AboutFragment.TAG:
                    fragment = new AboutFragment();
					toolbar.setTitle("About");
                    break;
				case PrefsFragment.TAG:
				default:
					fragment = new PrefsFragment();
					toolbar.setTitle("SystemPanel");
					break;
			}
		}
		FragmentTransaction t = getFragmentManager().beginTransaction();
		if (addToBackStack) {
			t.addToBackStack(tag);
		}
		t.replace(R.id.settings_fragment_container, fragment, tag).commit();
	}


	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(
						index >= 0
								? listPreference.getEntries()[index]
								: null);

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));
	}

	public static class PrefsFragment extends PreferenceFragment {
        public static final String TAG = "PrefsFragment";

		@Override
		@SuppressLint("DefaultLocale")
		public void onResume() {
			super.onResume();
			((MainActivity) getActivity()).toolbar.setTitle("SystemPanel");
		}

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
                if (key.contentEquals("date_time")) {
                    ((MainActivity) getActivity()).showFragment(DatetimeFragment.TAG, true);
                    return true;
                }
                else if (key.contentEquals("about")) {
                    ((MainActivity) getActivity()).showFragment(AboutFragment.TAG, true);
                    return true;
                }
                else if (key.contentEquals("default_app")) {
                    Intent intent = new Intent(getActivity(), AppsSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    return true;
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        private void resetToDefault() {
		}
		private void resetToDefaultDialog(Context context){
			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
			} else {
				builder = new AlertDialog.Builder(context);
			}
			builder.setTitle("Reset to default")
					.setMessage("Are you sure you want to reset to factory settings?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							resetToDefault();
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("app");

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			bindPreferenceSummaryToValue(findPreference("reboot_time_preference"));
//			bindPreferenceSummaryToValue(findPreference("list_preference"));
//			bindPreferenceSummaryToValue(findPreference("key_sound_switch"));

			Preference button = findPreference("factory_reset_button");
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					resetToDefaultDialog(getActivity());
					return true;
				}
			});

			setHasOptionsMenu(true);
		}
	}

	public static class DatetimeFragment extends PreferenceFragment {
        public static final String TAG = "DatetimeFragment";

        public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preference_datetime);
		}
	}


}
