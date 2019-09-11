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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.element.ElementModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UiHandlerThread extends HandlerThread {

    private Handler handler;
    private SharedPreferences sharedPreferences;
    private List<ElementModel> elementModels;
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
    private float stopY;

    public UiHandlerThread(String name, int priority, Context context, MainActivity mainActivity,
                           RecyclerView recyclerView, List<ElementModel> elementModels, Set set,
                           Canvas canvas, Path path, Float startX,
                           Float startY, Float stopX, Float stopY, SharedPreferences sharedPreferences) {
        super("handleUIUpdateOnTagValue", Process.THREAD_PRIORITY_DEFAULT);
        this.canvas = canvas;
        this.mContext = context;
        this.mainActivity = mainActivity;
        this.recyclerViewMain = recyclerView;
        this.sharedPreferences = sharedPreferences;
        this.elementModels = elementModels;
        this.limit = set.size();
        this.allTags = new ArrayList<String>(set);
        this.path = path;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.differentTagsLimit = set;
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

                        tagPosition = 0;
                        firstPosition = false;
                        secondPosition = false;
                        coordinates.clear();

                        Gson gson = new Gson();
                        String json = sharedPreferences.getString("MyObjectsList", "");
                        String json1 = sharedPreferences.getString("DifferentTagList", "");
                        Type type1 = new TypeToken<Set<String>>() {
                        }.getType();
                        Type type = new TypeToken<List<ElementModel>>() {
                        }.getType();
                        elementModels = gson.fromJson(json, type);
                        differentTagsLimit = gson.fromJson(json1, type1);
                        allTags = new ArrayList<>(differentTagsLimit);
                        limit = differentTagsLimit.size();

                        for (tagPosition = 0; tagPosition < limit; tagPosition++) {
                            coordinates.add(new ArrayList<>());
                            findFirst(tagPosition);
                            if (firstPosition) {
                                findLast(tagPosition);
                                if (secondPosition) {
                                    secondPosition = false;
                                }
                            }
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json2 = gson.toJson(coordinates);
                        editor.putString("CoordinatesList", json2);
                        editor.apply();
                    }
                });
            }
        };
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
