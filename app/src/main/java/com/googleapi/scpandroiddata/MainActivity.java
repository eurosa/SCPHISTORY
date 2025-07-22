package com.googleapi.scpandroiddata;



import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.Manifest.permission;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchHistoryAdapter.OnHistoryItemClickListener {
    private static final int PAGE_SIZE = 50;
    private static final String SEARCH_HISTORY_KEY = "search_history";

    private DataService dataService;
    private Gson gson;
    private static final int REQUEST_MANAGE_STORAGE = 1000;
    private AutoCompleteTextView searchInput;
    private Button searchButton;
    private Button clearButton;
    private Button exportButton;
    private Button manageHistoryButton;
    private ProgressBar progressBar;
    private RecyclerView dataRecyclerView;
    private RecyclerView historyRecyclerView;
    private View historyContainer;
    private static final String PREF_NAME = "DeviceIdHistoryPrefs";
    private static final String HISTORY_KEY = "device_id_history";
    private static final int MAX_HISTORY_ITEMS = 999;
    private DataItemAdapter dataAdapter;
    private SearchHistoryAdapter historyAdapter;

    private List<DataItem> currentData = new ArrayList<>();
    private List<SearchHistoryItem> searchHistory = new ArrayList<>();

    private String currentSearch = "";
    private int currentPage = 1;
    private boolean hasMoreData = true;
    private boolean isLoading = false;
    private boolean showHistory = false;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataService = new DataService();
        gson = new Gson();

        initializeViews();
        setupAdapters();
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        setupViewPager();
        loadSearchHistory();
        updateHistorySuggestions();
    }
    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this, currentData);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Temperature"); break;
                case 1: tab.setText("Humidity"); break;
                case 2: tab.setText("Pressure"); break;
                case 3: tab.setText("Table"); break;
            }
        }).attach();
    }
    private void initializeViews() {
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        clearButton = findViewById(R.id.clear_button);
        exportButton = findViewById(R.id.export_button);
        manageHistoryButton = findViewById(R.id.manage_history_button);
        progressBar = findViewById(R.id.progress_bar);
       // dataRecyclerView = findViewById(R.id.data_recycler_view);
        historyRecyclerView = findViewById(R.id.history_recycler_view);
        historyContainer = findViewById(R.id.history_container);

        searchButton.setOnClickListener(v -> searchData());
        clearButton.setOnClickListener(v -> clearSearch());
        exportButton.setOnClickListener(v -> exportToExcel());
        manageHistoryButton.setOnClickListener(v -> toggleHistoryVisibility());
    }

    private void setupAdapters() {
        dataAdapter = new DataItemAdapter(currentData);
        //dataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       // dataRecyclerView.setAdapter(dataAdapter);

        historyAdapter = new SearchHistoryAdapter(searchHistory, this);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void loadSearchHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet(HISTORY_KEY, new LinkedHashSet<>());
        searchHistory.clear();

        // Convert Set to List of SearchHistoryItem (if you still want to use the class)
        for (String deviceId : historySet) {
            SearchHistoryItem item = new SearchHistoryItem();
            item.setValue(deviceId);
            searchHistory.add(item);
        }
    }

    private void saveSearchHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Set<String> historySet = new LinkedHashSet<>();

        // Convert List to Set of strings
        for (SearchHistoryItem item : searchHistory) {
            historySet.add(item.getValue());
        }

        prefs.edit()
                .putStringSet(HISTORY_KEY, historySet)
                .apply();
    }

    private void updateHistorySuggestions() {
        List<String> suggestions = new ArrayList<>();
        for (SearchHistoryItem item : searchHistory) {
            suggestions.add(item.getValue());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );

        searchInput.setAdapter(adapter);
    }

    // Replace your current searchData() method with:
    private void searchData() {
        String searchValue = searchInput.getText().toString().trim();
        if (searchValue.isEmpty()) {
            Toast.makeText(this, "Please enter a device ID", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSearch = searchValue;
        currentPage = 1;
        hasMoreData = true;
        isLoading = true;

        updateUiState();
        new FetchDataTask().execute(searchValue, String.valueOf(currentPage));

        // Add to history (simplified version)
        addToDeviceIdHistory(searchValue);
    }

    private void addToDeviceIdHistory(String deviceId) {
        // Remove if already exists to avoid duplicates
        searchHistory.removeIf(item -> item.getValue().equals(deviceId));

        // Create new item and add to beginning
        SearchHistoryItem newItem = new SearchHistoryItem();
        newItem.setValue(deviceId);
        searchHistory.add(0, newItem);

        // Keep only last 10 items
        if (searchHistory.size() > MAX_HISTORY_ITEMS) {
            searchHistory = searchHistory.subList(0, MAX_HISTORY_ITEMS);
        }

        saveSearchHistory();
        updateHistorySuggestions();
        historyAdapter.updateData(searchHistory);
    }

    private void clearSearch() {
        searchInput.setText("");
        currentSearch = "";
        currentData.clear();
       // dataAdapter.updateData(currentData);
        updateUiState();
    }

    private void exportToExcel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - No permission needed for Downloads folder
            new ExportDataTask().execute(currentSearch);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11-12 - Check if we have MANAGE_EXTERNAL_STORAGE
            if (Environment.isExternalStorageManager()) {
                new ExportDataTask().execute(currentSearch);
            } else {
                requestManageStoragePermission();
            }
        } else {
            // Android 10 and below - Use old WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                new ExportDataTask().execute(currentSearch);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            }
        }

    }
    @TargetApi(Build.VERSION_CODES.R)
    private void requestManageStoragePermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
        } catch (Exception e) {
            // Fallback if the intent fails
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    new ExportDataTask().execute(currentSearch);
                } else {
                    Toast.makeText(this, "Permission denied. Cannot export without storage access.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void toggleHistoryVisibility() {
        showHistory = !showHistory;
        historyContainer.setVisibility(showHistory ? View.VISIBLE : View.GONE);
        manageHistoryButton.setText(showHistory ? "Hide History" : "Manage History");

        // Refresh history when showing
        if (showHistory) {
            loadSearchHistory();
            historyAdapter.updateData(searchHistory);
        }
    }

    private void updateUiState() {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        exportButton.setEnabled(!currentSearch.isEmpty());
    }

    @Override
    public void onEditClick(SearchHistoryItem item) {
        searchInput.setText(item.getValue());
        searchInput.setSelection(searchInput.getText().length());
        toggleHistoryVisibility();
    }

    @Override
    public void onDeleteClick(SearchHistoryItem item) {
        searchHistory.remove(item);
        saveSearchHistory();
        updateHistorySuggestions();
        historyAdapter.updateData(searchHistory);
    }

    private class FetchDataTask extends AsyncTask<String, Void, ApiResponse> {
        @Override
        protected ApiResponse doInBackground(String... params) {
            String searchValue = params[0];
            int page = Integer.parseInt(params[1]);

            try {
                return dataService.getData(searchValue, page, PAGE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ApiResponse response) {
            isLoading = false;
            updateUiState();

            if (response == null || response.getData() == null) {
                Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentPage == 1) {
                currentData.clear();
            }

            currentData.addAll(response.getData());
            currentData.addAll(response.getData());
           // dataAdapter.updateData(currentData);
            dataAdapter.updateData(currentData);
            viewPagerAdapter.notifyDataSetChanged(); // Refresh all fragments


            hasMoreData = response.getData().size() >= PAGE_SIZE;
        }
    }

    private class ExportDataTask extends AsyncTask<String, Void, Uri> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Exporting data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Uri doInBackground(String... params) {
            String deviceId = params[0];

            try {
                InputStream inputStream = dataService.exportToExcel(deviceId);
                if (inputStream != null) {
                    // Create a file in the Downloads directory using MediaStore
                    String fileName = "SCP_Export_" + System.currentTimeMillis() + ".csv";
                    String mimeType = "text/csv";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                    values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    ContentResolver resolver = getContentResolver();
                    Uri uri = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    }

                    if (uri != null) {
                        OutputStream outputStream = resolver.openOutputStream(uri);
                        if (outputStream != null) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            outputStream.close();
                        }
                        inputStream.close();
                        return uri;
                    }
                }
            } catch (IOException e) {
                Log.e("ExportDataTask", "Export failed", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri fileUri) {
            progressDialog.dismiss();

            if (fileUri != null) {
                // Show success and offer to open/share the file
                showExportSuccessDialog(fileUri);
            } else {
                Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showExportSuccessDialog(Uri fileUri) {
        new AlertDialog.Builder(this)
                .setTitle("Export Successful")
                .setMessage("The file has been saved to your Downloads folder.")
                .setPositiveButton("Open", (dialog, which) -> openFile(fileUri))
                .setNegativeButton("Share", (dialog, which) -> shareFile(fileUri))
                .setNeutralButton("OK", null)
                .show();
    }

    private void openFile(Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app available to open CSV files", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile(Uri fileUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share exported data"));
    }

    private void shareExportedFile(File file) {
        Uri fileUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share exported data"));
    }
}