package com.photos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.photos.R;
import com.photos.adapter.SearchResultAdapter;
import com.photos.model.DataManager;
import com.photos.model.DataManager.PhotoResult;
import com.photos.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for searching photos by tags with auto-completion.
 * Supports single tag search, AND (conjunction), and OR (disjunction).
 */
public class SearchActivity extends AppCompatActivity implements SearchResultAdapter.SearchResultClickListener {

    private RadioGroup searchModeGroup;
    private RadioButton radioSingle;
    private RadioButton radioAnd;
    private RadioButton radioOr;

    private Spinner spinnerType1;
    private AutoCompleteTextView autoCompleteValue1;
    private LinearLayout secondTagLayout;
    private Spinner spinnerType2;
    private AutoCompleteTextView autoCompleteValue2;
    private Button btnSearch;

    private TextView resultsTitle;
    private RecyclerView resultsRecyclerView;
    private TextView noResultsText;

    private DataManager dataManager;
    private SearchResultAdapter adapter;
    private List<PhotoResult> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dataManager = DataManager.getInstance();
        searchResults = new ArrayList<>();

        initViews();
        setupListeners();
    }

    private void initViews() {
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Search mode
        searchModeGroup = findViewById(R.id.searchModeGroup);
        radioSingle = findViewById(R.id.radioSingle);
        radioAnd = findViewById(R.id.radioAnd);
        radioOr = findViewById(R.id.radioOr);

        // First tag
        spinnerType1 = findViewById(R.id.spinnerType1);
        autoCompleteValue1 = findViewById(R.id.autoCompleteValue1);

        // Second tag
        secondTagLayout = findViewById(R.id.secondTagLayout);
        spinnerType2 = findViewById(R.id.spinnerType2);
        autoCompleteValue2 = findViewById(R.id.autoCompleteValue2);

        // Search button
        btnSearch = findViewById(R.id.btnSearch);

        // Results
        resultsTitle = findViewById(R.id.resultsTitle);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        noResultsText = findViewById(R.id.noResultsText);

        // Setup spinners
        String[] tagTypes = {Tag.TYPE_PERSON, Tag.TYPE_LOCATION};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tagTypes);
        spinnerType1.setAdapter(spinnerAdapter);
        spinnerType2.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        resultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SearchResultAdapter(this, searchResults, this);
        resultsRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // Search mode changes
        searchModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean showSecondTag = (checkedId == R.id.radioAnd || checkedId == R.id.radioOr);
            secondTagLayout.setVisibility(showSecondTag ? View.VISIBLE : View.GONE);
        });

        // Spinner selection changes - update auto-complete
        spinnerType1.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateAutoComplete(autoCompleteValue1, (String) spinnerType1.getSelectedItem());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        spinnerType2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateAutoComplete(autoCompleteValue2, (String) spinnerType2.getSelectedItem());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Text watchers for auto-complete updates
        autoCompleteValue1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAutoCompleteSuggestions(autoCompleteValue1,
                        (String) spinnerType1.getSelectedItem(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteValue2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAutoCompleteSuggestions(autoCompleteValue2,
                        (String) spinnerType2.getSelectedItem(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Search button
        btnSearch.setOnClickListener(v -> performSearch());

        // Initialize auto-complete for first spinner
        updateAutoComplete(autoCompleteValue1, Tag.TYPE_PERSON);
        updateAutoComplete(autoCompleteValue2, Tag.TYPE_PERSON);
    }

    private void updateAutoComplete(AutoCompleteTextView autoComplete, String tagType) {
        List<String> suggestions = dataManager.getTagValuesWithPrefix(tagType, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1);
    }

    private void updateAutoCompleteSuggestions(AutoCompleteTextView autoComplete,
                                                String tagType, String prefix) {
        List<String> suggestions = dataManager.getTagValuesWithPrefix(tagType, prefix);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autoComplete.setAdapter(adapter);
    }

    private void performSearch() {
        String tagType1 = (String) spinnerType1.getSelectedItem();
        String value1 = autoCompleteValue1.getText().toString().trim();

        if (value1.isEmpty()) {
            showNoResults();
            return;
        }

        List<PhotoResult> results;

        if (radioSingle.isChecked()) {
            // Single tag search
            results = dataManager.searchByTag(tagType1, value1);
        } else {
            String tagType2 = (String) spinnerType2.getSelectedItem();
            String value2 = autoCompleteValue2.getText().toString().trim();

            if (value2.isEmpty()) {
                // Treat as single tag search if second value is empty
                results = dataManager.searchByTag(tagType1, value1);
            } else if (radioAnd.isChecked()) {
                // AND search
                results = dataManager.searchByTagsAnd(tagType1, value1, tagType2, value2);
            } else {
                // OR search
                results = dataManager.searchByTagsOr(tagType1, value1, tagType2, value2);
            }
        }

        displayResults(results);
    }

    private void displayResults(List<PhotoResult> results) {
        searchResults.clear();
        searchResults.addAll(results);
        adapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            showNoResults();
        } else {
            resultsTitle.setText(getString(R.string.search_results, results.size()));
            resultsTitle.setVisibility(View.VISIBLE);
            resultsRecyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        }
    }

    private void showNoResults() {
        resultsTitle.setVisibility(View.GONE);
        resultsRecyclerView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResultClick(PhotoResult result, int position) {
        // Open photo in its album context
        Intent intent = new Intent(this, PhotoDisplayActivity.class);
        intent.putExtra("album_name", result.album.getName());
        
        // Find the photo index in the album
        int photoIndex = result.album.getPhotos().indexOf(result.photo);
        intent.putExtra("photo_index", photoIndex);
        
        startActivity(intent);
    }
}

