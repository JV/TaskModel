package com.example.taskmodel.backgroundTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.adapters.ElementModelViewAdapter;
import com.example.taskmodel.element.ElementModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UiHandlerThread extends HandlerThread {

    private Handler handler;
    private SharedPreferences sharedPreferences;
    private List<ElementModel> elementModels = new ArrayList<>();
    private Context mContext;
    private RecyclerView recyclerViewMain;
    private int limit;
    private List<String> allTags;
    boolean firstPosition;
    boolean secondPosition;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private int tagPosition = 0;
    private MainActivity mainActivity;
    private Set<String> differentTagsLimit = new LinkedHashSet<>();
    private Canvas canvas;
    private Path path;
    private float startX;
    private float startY;
    private float stopX;
    private float screenHeight;
    private ElementModelViewAdapter mmAdapter;

    private UiHandlerThread handlerThread;

    public UiHandlerThread(String name, int priority, Context context,
                           RecyclerView recyclerView, Set set,
                           Canvas canvas, Path path, Float startX,
                           Float startY, Float stopX, Float stopY,
                           SharedPreferences sharedPreferences, ElementModelViewAdapter mmAdapter,
                           UiHandlerThread handlerThread) {
        super("handleUIUpdateOnTagValue", Process.THREAD_PRIORITY_DEFAULT);
        this.canvas = canvas;
        this.mContext = context;

        this.recyclerViewMain = recyclerView;
        this.sharedPreferences = sharedPreferences;

        this.handlerThread = handlerThread;
        this.limit = set.size();
        this.allTags = new ArrayList<String>(set);
        this.path = path;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.mmAdapter = mmAdapter;

        this.screenHeight = stopY;
        this.differentTagsLimit = set;
        createMockData();
        loadPrefs();
        saveDifferentTags();
        prepareElementData();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.apply();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {

                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        loadPrefs();
                        saveDifferentTags();
                        prepareElementData();

                        allTags = new ArrayList<>(differentTagsLimit);
                        limit = differentTagsLimit.size();

                        tagPosition = 0;
                        firstPosition = false;
                        secondPosition = false;
                        coordinates.clear();

                        for (int i = 0; i < limit; i++) {

                            coordinates.add(new ArrayList<>());
                            findFirst(tagPosition);
                            if (firstPosition) {

                                findLast(tagPosition);
                                tagPosition++;
                                if (secondPosition) {
                                    secondPosition = false;
                                }
                            }
                        }
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json2 = gson.toJson(coordinates);
                        editor.putString("CoordinatesList", json2);
                        editor.apply();
                        doWork();
                    }
                });
            }
        };
    }

    private void doWork() {

        for (ElementModel elementModel : this.elementModels) {
            differentTagsLimit.add(elementModel.getTag());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(differentTagsLimit);
        editor.putString("DifferentTagList", json);
        editor.apply();

        String json1 = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        coordinates = gson.fromJson(json1, type);
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

    private void saveMockData() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.apply();
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
            this.elementModels.add(elementModel);
            i++;
        }
    }

    private void loadPrefs() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("MyObjectsList", "");
        Type type = new TypeToken<List<ElementModel>>() {
        }.getType();
        this.elementModels = gson.fromJson(json, type);

    }

    private void prepareElementData() {

        if (!sharedPreferences.getBoolean("listMovedAround", false)) {
            Collections.sort(elementModels, new Comparator<ElementModel>() {

                @Override
                public int compare(ElementModel elementModel, ElementModel t1) {
                    return t1.getPocetak() < elementModel.getPocetak() ? -1 : (t1.getPocetak() >
                            elementModel.getPocetak()) ? 1 : 0;
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

    private void findLast(int tagPosition) {
        reverseList(elementModels);

        for (int j = 0; j < elementModels.size(); j++) {

            int distanceFromEnd = elementModels.size() - 1 - coordinates.get(tagPosition).get(0);

            String searchTarget = allTags.get(tagPosition);
            secondPosition = elementModels.get(j).getTag().equals(searchTarget);

            if (secondPosition) {

                if (j == distanceFromEnd) {
                    coordinates.get(tagPosition).add(-1);
                    reverseList(elementModels);
                    return;
                } else {
                    int secondPositionC = elementModels.size() - j - 1;
                    coordinates.get(tagPosition).add(secondPositionC);
                    reverseList(elementModels);
                    return;
                }
            }
        }
        if (!secondPosition) {
            coordinates.get(tagPosition).add(-1);
        }
        reverseList(elementModels);
    }

    private void findFirst(int tagPosition) {

        for (int x = 0; x < elementModels.size(); x++) {

            String searchItem = allTags.get(tagPosition);
            firstPosition = elementModels.get(x).getTag().equals(searchItem);
            if (firstPosition) {
                coordinates.get(tagPosition).add(x);
            }
            if (firstPosition) {
                return;
            }
        }
        if (!firstPosition) {
            coordinates.remove(coordinates.get(tagPosition));
        }
    }

    private void reverseList(List<ElementModel> elementModels) {
        for (int i = 0; i < elementModels.size(); i++) {
            elementModels.add(i, elementModels.remove(elementModels.size() - 1));
        }
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void run() {
        super.run();
    }
}
