package com.example.taskmodel;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

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
import com.example.taskmodel.interfaces.DoWork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements DoWork {

    private RecyclerView recyclerViewMain;
    private ElementModelViewAdapter mmAdapter;
    private FloatingActionButton floatingActionButton;
    private List<ElementModel> elementModels = new ArrayList<>();
    private Set<String> differentTagsLimit = new LinkedHashSet<>();

    private SharedPreferences sharedPreferences;
    private UiHandlerThread handlerThread;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private LinearLayout linearLayout;

    protected MainActivity mainActivity;

    private long maxNumberOfTagsForDevice;

    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private Path path;
    private float screenHeight;
    private float screenWidth;
    private View screen;
    private EditElementFragment editElementFragment;
    private AddElementFragment addElementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDisplay();
        setContentView(R.layout.activity_main);
        setupGraphics();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json1 = gson.toJson(coordinates);
        editor.putString("CoordinatesList", json1);
        editor.apply();

        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        coordinates = gson.fromJson(json, type);

        createMockData();
        loadPrefs();

        saveDifferentTags();
        initViews();
        setupListeners();
        prepareElementData();
        initThread();
        setUpScreen();
        doWork();
    }

    private void setUpScreen() {
        mmAdapter = new ElementModelViewAdapter(elementModels, getApplicationContext(), this, mainActivity, coordinates, sharedPreferences, screenHeight);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(mmAdapter, getApplicationContext(), this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        mmAdapter.setTouchHelper(itemTouchHelper);
        recyclerViewMain.setAdapter(mmAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerViewMain);
        recyclerViewMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


    }

    private void setupGraphics() {
        bitmap = Bitmap.createBitmap((int) screenWidth, (int) screenHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        path = new Path();
    }

    private void getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initThread() {
        handlerThread = new UiHandlerThread("handleUIUpdateOnTagValue",
                Process.THREAD_PRIORITY_DEFAULT, this, mainActivity, recyclerViewMain,
                elementModels, differentTagsLimit, canvas, path, (float) 0, (float) 0, screenWidth, screenHeight, sharedPreferences);
        handlerThread.start();
    }

    @Override
    public void doWork() {

        for (ElementModel elementModel : elementModels) {
            differentTagsLimit.add(elementModel.getTag());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(differentTagsLimit);
        editor.putString("DifferentTagList", json);
        editor.apply();

        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) elementModels);

        message.setData(bundle);
        handlerThread.getHandler().sendMessage(message);

        String json1 = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        coordinates = gson.fromJson(json1, type);

        mmAdapter = new ElementModelViewAdapter(elementModels, getApplicationContext(), this, mainActivity, coordinates, sharedPreferences, screenHeight);

        recyclerViewMain.setAdapter(mmAdapter);
        mmAdapter.notifyDataSetChanged();

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

    private void saveDifferentTags() {
        for (ElementModel elementModel : elementModels) {
            differentTagsLimit.add(elementModel.getTag());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(differentTagsLimit);
        editor.putString("DifferentTagList", json);
        editor.apply();
    }

    private void initViews() {
        recyclerViewMain = findViewById(R.id.recyclerviewMain);
        floatingActionButton = findViewById(R.id.floating_action_button);
        linearLayout = findViewById(R.id.connectionHolder);

    }

    private void setupListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("valuesList", (Serializable) elementModels);

                addElementFragment = new AddElementFragment();
                addElementFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack("addElementFragment").replace(R.id.activityMain, addElementFragment).commit();
                floatingActionButton.hide();
            }
        });
    }

    public void showItem(View view) {

        long itemPosition = (long) recyclerViewMain.getChildLayoutPosition(view);
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) elementModels);
        bundle.putLong("editItemPosition", itemPosition);
        editElementFragment = new EditElementFragment();
        editElementFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().addToBackStack("editElementFragment").replace(R.id.activityMain, editElementFragment).commit();
        floatingActionButton.hide();
    }

    private void createMockData() {
        if (!sharedPreferences.getBoolean("firstTime", false)) {

            populateLists();
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("firstTime", true);
            editor1.apply();
            saveMockData();
        }

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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        else super.onBackPressed();
    }
}
