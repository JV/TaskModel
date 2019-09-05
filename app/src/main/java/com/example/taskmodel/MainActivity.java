package com.example.taskmodel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.adapters.ElementModelViewAdapter;
import com.example.taskmodel.adapters.SimpleItemTouchHelperCallback;
import com.example.taskmodel.backgroundTasks.UiHandlerThread;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewMain;
    ElementModelViewAdapter mmAdapter;
    FloatingActionButton floatingActionButton;
    List<ElementModel> elementModels = new ArrayList<>();
    Set<String> numberOfDifferentTags = new HashSet<>();
    int[][] positions = new int[numberOfDifferentTags.size()][];
    SharedPreferences sharedPreferences;
    UiHandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getBoolean("firstTime", false)) {
            populateLists();
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("firstTime", true);
            editor1.apply();
            saveMockData();
        }

        handlerThread = new UiHandlerThread("handleUIUpdateOnTagValue", Process.THREAD_PRIORITY_DEFAULT, elementModels, this, recyclerViewMain);
        handlerThread.start();

        initViews();
        setupListeners();
        loadPrefs();

        setUpScreen();

    }

    public void doWork(View view) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) elementModels);
        message.setData(bundle);
        handlerThread.getHandler().sendMessage(message);
    }

    private void saveMockData() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.apply();
    }

    private void loadPrefs() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("MyObjectsList", "");
        Type type = new TypeToken<List<ElementModel>>() {
        }.getType();
        elementModels = gson.fromJson(json, type);
    }

    private void setUpScreen() {

        prepareElementData();

        mmAdapter = new ElementModelViewAdapter(elementModels, getApplicationContext(), numberOfDifferentTags);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(mmAdapter, getApplicationContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        mmAdapter.setTouchHelper(itemTouchHelper);
        recyclerViewMain.setAdapter(mmAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerViewMain);
        recyclerViewMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        doWork(recyclerViewMain);
    }

    public void prepareElementData() {

        if (!sharedPreferences.getBoolean("listMovedAround", false)) {
            Collections.sort(elementModels, new Comparator<ElementModel>() {

                @Override
                public int compare(ElementModel elementModel, ElementModel t1) {
                    return t1.getPocetak() < elementModel.getPocetak() ? -1 : (t1.getPocetak() > elementModel.getPocetak()) ? 1 : 0;
                }
            });
        }
        saveDifferentTags();
    }

    private void getPositionOfItemsWithSameTag() {
        List<String> listOfTags = new ArrayList<>(numberOfDifferentTags);
        long itemPosition = 0;
        String tag = listOfTags.get(0);
        String tag2 = listOfTags.get(1);
        while (itemPosition != elementModels.size()) {
            if ((elementModels.get((int) itemPosition).getTag()).equals(tag)) {
                positions = new int[][]{{Integer.parseInt(tag), Integer.parseInt(elementModels.get((int) itemPosition).getTag())}};
            } else {
                positions = new int[][]{{Integer.parseInt(tag2), Integer.parseInt(elementModels.get((int) itemPosition).getTag())}};

            }
            itemPosition++;
        }
    }

    private boolean checkTag(ElementModel elementModel) {
        return true;
    }

    private void saveDifferentTags() {
        for (ElementModel elementModel : elementModels) {
            numberOfDifferentTags.add(elementModel.getTag());
        }
    }

    private void initViews() {
        recyclerViewMain = findViewById(R.id.recyclerviewMain);
        floatingActionButton = findViewById(R.id.floating_action_button);
    }

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

    private void populateLists() {

        long i = 0;
        while (i < 24) {

            ElementModel elementModel = new ElementModel();
            elementModel.setId(i + 1);
            elementModel.setNaziv("Ele" + elementModel.getId());
            elementModel.setPocetak(i);
            elementModel.setKraj(i + i);
            if (i % 2 != 0) {
                elementModel.setTag("1");
            } else {
                elementModel.setTag("" + i % 2);
            }
            elementModels.add(elementModel);
            i++;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
}