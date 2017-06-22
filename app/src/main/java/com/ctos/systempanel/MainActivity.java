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
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
	protected Toolbar toolbar;

    public static CTOSInfo ctosInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ctosInfo = new CTOSInfo();

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
                String key = preference.getKey();
                if (key.contentEquals("battery_preference")) {
                    preference.setSummary(stringValue + "%");
                    if (!stringValue.isEmpty())
                        ctosInfo.setBatteryThreshold(Integer.parseInt(stringValue));
                } else if (key.contentEquals("reboot_time_preference")) {
                    preference.setSummary(stringValue + " hours");
                    if (!stringValue.isEmpty())
                        ctosInfo.setRebootInterval(Integer.parseInt(stringValue));
                } else if (key.contentEquals("ntp_server")) {
                    preference.setSummary(stringValue);
                    if (!stringValue.isEmpty())
                        ctosInfo.setNTPServer(stringValue);
                } else
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
        private int SECOND_ACTIVITY_RESULT_CODE = 100;

        @Override
		@SuppressLint("DefaultLocale")
		public void onResume() {
			super.onResume();
			((MainActivity) getActivity()).toolbar.setTitle("SystemPanel");
		}

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
                if (resultCode == RESULT_OK) {
                    String returnString = data.getStringExtra("APP");
                    findPreference("default_app").setSummary(returnString);
                    ctosInfo.setDefaultApplication(returnString);
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
                if (key.contentEquals("date_time")) {
                    ((MainActivity) getActivity()).showFragment(DatetimeFragment.TAG, true);
                    return true;
                } else if (key.contentEquals("about")) {
                    ((MainActivity) getActivity()).showFragment(AboutFragment.TAG, true);
                    return true;
                } else if (key.contentEquals("default_app")) {
                    Intent intent = new Intent(getActivity(), AppsSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, SECOND_ACTIVITY_RESULT_CODE);
                    return true;
                } else if (key.contentEquals("change_password")) {
                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    return true;
                }

            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        private void resetToDefault() {
        }

        private void resetToDefaultDialog(Context context) {
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

            NumberPickerPreference reboot_time = (NumberPickerPreference) findPreference("reboot_time_preference");
            NumberPickerPreference battery_threshold = (NumberPickerPreference) findPreference("battery_preference");
            bindPreferenceSummaryToValue(reboot_time);
            bindPreferenceSummaryToValue(battery_threshold);
            findPreference("default_app").setSummary(ctosInfo.getDefaultApplication());

            reboot_time.setValue(ctosInfo.getRebootInterval());
            reboot_time.setSummary(ctosInfo.getRebootInterval() + " hours");
            battery_threshold.setValue(ctosInfo.getBatteryThreshold());
            battery_threshold.setSummary(ctosInfo.getBatteryThreshold() + "%");


            SwitchPreference keySound = (SwitchPreference) findPreference("key_sound_switch");
            SwitchPreference enablePassword = (SwitchPreference) findPreference("enable_password_switch");
            SwitchPreference autoReboot = (SwitchPreference) findPreference("key_autoreboot_switch");
            SwitchPreference debugMode = (SwitchPreference) findPreference("key_debug_switch");

            keySound.setChecked(ctosInfo.getKeySoundState());
            enablePassword.setChecked(ctosInfo.getPasswordState());
            autoReboot.setChecked(ctosInfo.getAutoRebootState());
            debugMode.setChecked(ctosInfo.getDebugModeState());

            keySound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    ctosInfo.setKeySoundState(switched);
                    return true;
                }
            });

            enablePassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    ctosInfo.setPasswordState(switched);
                    return true;
                }
            });

            autoReboot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    ctosInfo.setAutoRebootState(switched);
                    return true;
                }
            });

            debugMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    ctosInfo.setDebugModeState(switched);
                    return true;
                }
            });

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

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
			if (isAdded() && preference.hasKey()) {
				String key = preference.getKey();
				if (key.contentEquals("timezone")) {
					Intent intent = new Intent(getActivity(), TimeZoneSelectionActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					return true;
				}
			}
			return super.onPreferenceTreeClick(screen, preference);
		}

        private void setAutoTimeState(boolean enable) {

            Preference ntpServer = findPreference("ntp_server");
            Preference date = findPreference("date");
            Preference time = findPreference("time");

            ntpServer.setSummary(ctosInfo.getNTPServer());

            if (!enable) {
                ntpServer.setEnabled(false);
                date.setEnabled(true);
                time.setEnabled(true);
            } else {
                ntpServer.setEnabled(true);
                date.setEnabled(false);
                time.setEnabled(false);
            }

        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("app");
            // Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preference_datetime);

            bindPreferenceSummaryToValue(findPreference("ntp_server"));

            SwitchPreference autoTime = (SwitchPreference) findPreference("auto_time");

            autoTime.setChecked(ctosInfo.getAutoTimeState());
            setAutoTimeState(ctosInfo.getAutoTimeState());

            autoTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    setAutoTimeState(!switched);
                    return true;
                }
            });

            SwitchPreference hour_24 = (SwitchPreference) findPreference("24_hour");
            if (android.text.format.DateFormat.is24HourFormat(getActivity()))
                hour_24.setChecked(true);
            else
                hour_24.setChecked(false);
            hour_24.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();

                  /* WRITE_SETTING  permission problem
                    if (switched)
                        Settings.System.putString(getActivity().getContentResolver(), Settings.System.TIME_12_24, "24");
                    else
                        Settings.System.putString(getActivity().getContentResolver(), Settings.System.TIME_12_24, "12");
*/
                    return true;
                }
            });


        }

        @Override
        @SuppressLint("DefaultLocale")
        public void onResume() {
            if (findPreference("timezone") != null)
                findPreference("timezone").setSummary(TimeZone.getDefault().getID());
            super.onResume();
        }
	}
}