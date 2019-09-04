package com.example.taskmodel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.adapters.ElementModelViewAdapter;
import com.example.taskmodel.adapters.SimpleItemTouchHelperCallback;
import com.example.taskmodel.element.ElementModel;
import com.example.taskmodel.fragments.AddElementFragment;
import com.example.taskmodel.fragments.EditElementFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewMain;
    ElementModelViewAdapter mmAdapter;
    FloatingActionButton floatingActionButton;
    List<ElementModel> elementModels = new ArrayList<>();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        // mock data creation and save on first installation

        if (!sharedPreferences.getBoolean("firstTime", false)) {

            populateLists();
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("firstTime", true);
            editor1.apply();
            saveMockData();
        }

        initViews();

        setupListeners();

        loadPrefs();

        setUpScreen();

    }

    // save mock data

    private void saveMockData() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);

        editor.putString("MyObjectsList", json);
        editor.apply();
    }

    // load last saved data

    private void loadPrefs() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("MyObjectsList", "");
        Type type = new TypeToken<List<ElementModel>>() {
        }.getType();
        elementModels = gson.fromJson(json, type);

    }

    private void setUpScreen() {

        prepareElementData();

        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        mmAdapter = new ElementModelViewAdapter(elementModels, getApplicationContext());
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(mmAdapter, getApplicationContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        mmAdapter.setTouchHelper(itemTouchHelper);
        recyclerViewMain.setAdapter(mmAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerViewMain);
        recyclerViewMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    //sort object list by @pocetak attribute

    public void prepareElementData() {

        if (!sharedPreferences.getBoolean("listMovedAround", false)) {

            Collections.sort(elementModels, new Comparator<ElementModel>() {

                @Override
                public int compare(ElementModel elementModel, ElementModel t1) {
                    return t1.getPocetak() < elementModel.getPocetak() ? -1 : (t1.getPocetak() > elementModel.getPocetak()) ? 1 : 0;
                }
            });
        }
    }

    private void initViews() {

        recyclerViewMain = findViewById(R.id.recyclerviewMain);
        floatingActionButton = findViewById(R.id.floating_action_button);

    }

    //add element to list via FloatingActionButton

    private void setupListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("valuesList", (Serializable) elementModels);

                AddElementFragment addElementFragment = new AddElementFragment();
                addElementFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activityMain, addElementFragment).commit();

                floatingActionButton.hide();
            }
        });
    }

    // edit element in list

    public void showItem(View view) {

        long itemPosition = (long) recyclerViewMain.getChildLayoutPosition(view);

        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) elementModels);
        bundle.putLong("editItemPosition", itemPosition);

        EditElementFragment editElementFragment = new EditElementFragment();
        editElementFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.activityMain, editElementFragment).commit();

        floatingActionButton.hide();

    }

    // populate mock data

    private void populateLists() {

        long i = 0;
        while (i < 24) {

            ElementModel elementModel = new ElementModel();
            elementModel.setId(i + 1);
            elementModel.setNaziv("Ele" + elementModel.getId());
            elementModel.setPocetak(i);
            elementModel.setKraj(i + i);
            if (i % 2 != 0) {
                elementModel.setTag("");
            } else {
                elementModel.setTag("" + i % 2);
            }
            elementModels.add(elementModel);
            i++;
        }
    }
}