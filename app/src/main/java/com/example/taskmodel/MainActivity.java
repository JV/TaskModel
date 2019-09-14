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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMain;
    private ElementModelViewAdapter mmAdapter;
    private FloatingActionButton floatingActionButton;
    private List<ElementModel> elementModels = new ArrayList<>();
    private Set<String> differentTagsLimit = new LinkedHashSet<>();

    private SharedPreferences sharedPreferences;
    private UiHandlerThread handlerThread;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private LinearLayout linearLayout;

    protected MainActivity mainActivity = this;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getDisplay();
        setContentView(R.layout.activity_main);
        setupGraphics();
        initViews();
        setupListeners();
        initThread();
        setUpScreen();
    }

    private void setUpScreen() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        coordinates = gson.fromJson(json, type);
        mmAdapter = new ElementModelViewAdapter(elementModels, getApplicationContext(),
                mainActivity, coordinates, sharedPreferences, screenHeight, handlerThread);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(
                mmAdapter, getApplicationContext(), handlerThread, mmAdapter, recyclerViewMain,
                elementModels, sharedPreferences);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        mmAdapter.setTouchHelper(itemTouchHelper);
        recyclerViewMain.setAdapter(mmAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerViewMain);
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
                Process.THREAD_PRIORITY_DEFAULT, this, recyclerViewMain, differentTagsLimit,
                canvas, path, (float) 0, (float) 0, screenWidth, screenHeight, sharedPreferences,
                mmAdapter, handlerThread);
        handlerThread.start();
        Gson gson1 = new Gson();
        String json1 = sharedPreferences.getString("MyObjectsList", "");
        Type type1 = new TypeToken<List<ElementModel>>() {
        }.getType();
        this.elementModels = gson1.fromJson(json1, type1);
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) this.elementModels);
        message.setData(bundle);
        handlerThread.getHandler().sendMessage(message);
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
                addElementFragment = new AddElementFragment(handlerThread, mmAdapter,
                        recyclerViewMain, screenHeight, mainActivity);
                addElementFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack("addElementFragment")
                        .replace(R.id.activityMain, addElementFragment).commit();
                floatingActionButton.hide();
            }
        });
    }

    public void showItem(View view) {

        long itemPosition = (long) recyclerViewMain.getChildLayoutPosition(view);
        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesList", (Serializable) elementModels);
        bundle.putLong("editItemPosition", itemPosition);
        editElementFragment = new EditElementFragment(handlerThread, mmAdapter, recyclerViewMain,
                screenHeight, mainActivity);
        editElementFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().addToBackStack("editElementFragment")
                .replace(R.id.activityMain, editElementFragment).commit();
        floatingActionButton.hide();
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
