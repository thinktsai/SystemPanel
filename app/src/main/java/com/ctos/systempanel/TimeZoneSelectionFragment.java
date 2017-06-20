package com.ctos.systempanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.text.TimeZoneNames;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

//import com.benny.openlauncher.model.AppInfo;
//import com.benny.openlauncher.util.AppManager;
//import com.benny.openlauncher.util.AppSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class TimeZoneSelectionFragment extends Fragment {

    @SuppressWarnings("unchecked")
    private ArrayList<String> list_activities = new ArrayList();
    @SuppressWarnings("unchecked")
    private ArrayList<ZoneInfo> list_activities_final = new ArrayList();
    @SuppressLint("StaticFieldLeak")
    private ViewSwitcher switcherLoad;
    private AsyncWorkerList taskList = new AsyncWorkerList();
    private Typeface tf;

    private static final String TAG = "RequestActivity";
    private static final boolean DEBUG = true; //TODO Set to false for PlayStore Release

    @SuppressWarnings("unused")
    private ViewSwitcher viewSwitcher;
    private ListView grid;
    private ZoneAdapter zoneInfoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.request, container, false);
        switcherLoad = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcherLoadingMain);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_rq);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelection();
            }
        });

        if (taskList.getStatus() == AsyncTask.Status.PENDING) {
            // My AsyncTask has not started yet
            taskList.execute();
        }

        if (taskList.getStatus() == AsyncTask.Status.FINISHED) {
            // My AsyncTask is done and onPostExecute was called
            new AsyncWorkerList().execute();
        }

        return rootView;
    }

    public class AsyncWorkerList extends AsyncTask<String, Integer, String> {

        private AsyncWorkerList() {
        }

        @Override
        protected void onPreExecute() {
//            List<String> hiddenList = AppSettings.get().getHiddenAppsList();
//            list_activities.addAll(hiddenList);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // Compare them to installed apps
                prepareData();
                return null;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            populateView();
            //Switch from loading screen to the main view
            switcherLoad.showNext();

            super.onPostExecute(result);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (DEBUG) Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    private void confirmSelection() {
        Thread actionSend_Thread = new Thread() {

            @Override
            public void run() {
                int selected = 0;
                ArrayList<String> hiddenList = new ArrayList<>();

                // Get all selected apps
                for (int i = 0; i < list_activities_final.size(); i++) {
                    if ((list_activities_final.get(i)).isSelected()) {
                        hiddenList.add((list_activities_final.get(i)).getCode());
                        selected++;
                    }
                }
                if (selected == 0) {//When there's no app selected show a toast and return.
                    Snackbar snackbar = Snackbar
                            .make(grid, R.string.request_toast, Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getActivity().finish();
                                }
                            });
                    snackbar.show();

                } else {
//                    AppSettings.get().setHiddenAppsList(hiddenList);
                    getActivity().finish();
                }
            }
        };
        if (!actionSend_Thread.isAlive()) {
            //Prevents the thread to be executed twice (or more) times.
            actionSend_Thread.start();
        }
    }


    private void prepareData() {


        for (String tz : TimeZone.getAvailableIDs()) {
            ZoneInfo tempZoneInfo = new ZoneInfo(
                    "GMT" + TimeZone.getTimeZone(tz).getRawOffset() / 3600000 + "  " + tz,
                    TimeZone.getTimeZone(tz).getDisplayName(),
                    getResources().getDrawable(R.drawable.ic_arrow_back_white_24px),
                    false
            );
            list_activities_final.add(tempZoneInfo);
        }
    }

    @SuppressWarnings("unchecked")
    private void populateView() {
        grid = (ListView) getActivity().findViewById(R.id.appgrid);

        assert grid != null;
        grid.setFastScrollEnabled(true);
        grid.setFastScrollAlwaysVisible(false);

        zoneInfoAdapter = new ZoneAdapter(getActivity(), list_activities_final);

        grid.setAdapter(zoneInfoAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> AdapterView, View view, int position, long row) {
                ZoneInfo zoneInfo = (ZoneInfo) AdapterView.getItemAtPosition(position);
                CheckBox checker = (CheckBox) view.findViewById(R.id.CBappSelect);
                ViewSwitcher icon = (ViewSwitcher) view.findViewById(R.id.viewSwitcherChecked);

                checker.toggle();
                zoneInfo.setSelected(checker.isChecked());

                if (zoneInfo.isSelected()) {
                    if (DEBUG) Log.v(TAG, "Selected App: " + zoneInfo.getName());
                    if (icon.getDisplayedChild() == 0) {
                        icon.showNext();
                    }
                } else {
                    if (DEBUG) Log.v(TAG, "Deselected App: " + zoneInfo.getName());
                    if (icon.getDisplayedChild() == 1) {
                        icon.showPrevious();
                    }
                }
                getActivity().getIntent().putExtra("APP", zoneInfo.getName());
                confirmSelection();
            }
        });
    }

    private class ZoneAdapter extends ArrayAdapter<ZoneInfo> {
        @SuppressWarnings("unchecked")

        private ZoneAdapter(Context context, ArrayList<ZoneInfo> adapterArrayList) {
            super(context, R.layout.zone_request_item_list, adapterArrayList);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zone_request_item_list, parent, false);
                holder = new ViewHolder();
                holder.apkIcon = (ImageView) convertView.findViewById(R.id.IVappIcon);
                holder.apkName = (TextView) convertView.findViewById(R.id.TVappName);
                holder.apkPackage = (TextView) convertView.findViewById(R.id.TVappPackage);
                holder.checker = (CheckBox) convertView.findViewById(R.id.CBappSelect);
                holder.switcherChecked = (ViewSwitcher) convertView.findViewById(R.id.viewSwitcherChecked);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ZoneInfo zoneInfo = getItem(position);

            holder.apkPackage.setText(zoneInfo.getCode());
            holder.apkPackage.setTypeface(tf);

            holder.apkName.setText(zoneInfo.getName());

            holder.apkIcon.setImageDrawable(zoneInfo.getImage());

            holder.switcherChecked.setInAnimation(null);
            holder.switcherChecked.setOutAnimation(null);

            holder.checker.setChecked(zoneInfo.isSelected());
            if (zoneInfo.isSelected()) {
                if (holder.switcherChecked.getDisplayedChild() == 0) {
                    holder.switcherChecked.showNext();
                }
            } else {
                if (holder.switcherChecked.getDisplayedChild() == 1) {
                    holder.switcherChecked.showPrevious();
                }
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView apkName;
        TextView apkPackage;
        ImageView apkIcon;
        CheckBox checker;
        ViewSwitcher switcherChecked;
    }

}
