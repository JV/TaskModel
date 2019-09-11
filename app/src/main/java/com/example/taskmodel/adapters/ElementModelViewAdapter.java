package com.example.taskmodel.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.R;
import com.example.taskmodel.element.ElementModel;
import com.example.taskmodel.interfaces.DoWork;
import com.example.taskmodel.view.LineView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementModelViewAdapter extends RecyclerView.Adapter<ElementModelViewAdapter.ElementHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private List<ElementModel> elementModels;
    private ItemTouchHelper touchHelper;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private DoWork doWork;
    protected MainActivity mainActivity;
    private float screenWidth;
    private float screenHeight;
    private long numberOfMaxShownRows = 5;

    public ElementModelViewAdapter(List<ElementModel> elementModels, Context context, DoWork doWork, MainActivity mainActivity, List<List<Integer>> coordinates, SharedPreferences sharedPreferences, Float screenHeight) {

        Gson gson = new Gson();
        this.sharedPreferences = sharedPreferences;
        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        this.coordinates = gson.fromJson(json, type);
        this.mainActivity = mainActivity;
        this.doWork = doWork;
        this.elementModels = elementModels;
        this.mContext = context;
        this.screenHeight = screenHeight;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public ElementModelViewAdapter.ElementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item, parent, false);
        int height = (int) (screenHeight / 5);
        view.setMinimumHeight(height);
        ElementHolder holder = new ElementHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ElementHolder holder, final int position) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        this.coordinates = gson.fromJson(json, type);

        holder.tvNaziv.setText(elementModels.get(position).getNaziv());
        holder.tvPocetak.setText(String.valueOf(elementModels.get(position).getPocetak()));
        holder.tvKraj.setText(String.valueOf(elementModels.get(position).getId()));
        holder.connectionHolders.removeAllViews();

        for (int i = 0; i < coordinates.size(); i++) {

            int startX;
            int startY;
            int stopX;
            int stopY;
            int height = (int) (screenHeight / numberOfMaxShownRows);
            startX = 0;
            startY = 0;
            stopX = 0;
            stopY = height;
            boolean matchFirst = false;
            boolean matchLast = false;
            boolean matchUnique = false;
            boolean belongs = false;
            boolean noMatch = false;

            LineView lineView = new LineView(holder.holderContex, holder.canvas, holder.bitmap, startX, startY, stopX, stopY, holder.path, holder.view, matchFirst, matchLast, matchUnique, belongs, noMatch);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, stopY);

            holder.connectionHolders.addView(lineView, layoutParams);
            if (position < coordinates.get(i).get(0) | position > coordinates.get(i).get(1) | coordinates.get(i).get(1) == -1) {

                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(20, stopY);
                    lineView.setLayoutParams(layoutParams2);
                    lineView.setVisibility(View.INVISIBLE);

            } else {

                if (position == coordinates.get(i).get(0) | position == coordinates.get(i).get(1)) {

                    if (position == coordinates.get(i).get(0)) {

                        matchFirst = true;

                        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(20, stopY / 2);
                        layoutParams3.gravity = Gravity.BOTTOM;
                        lineView.setLayoutParams(layoutParams3);
                        lineView.setVisibility(View.VISIBLE);

                    } else if (position == coordinates.get(i).get(1)) {

                        matchLast = true;

                        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(20, stopY / 2);
                        lineView.setLayoutParams(layoutParams4);
                        lineView.setVisibility(View.VISIBLE);

                    }
                } else {

                        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(20, stopY);
                        lineView.setLayoutParams(layoutParams5);
                        lineView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class ElementHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvNaziv;
        TextView tvPocetak;
        TextView tvKraj;
        LinearLayout connectionHolders;
        Context holderContex;
        Canvas canvas;
        Bitmap bitmap;
        Paint paint;
        LinearLayout connectionHolder;
        Path path;
        float startX;
        float startY;
        float stopX;
        float stopY;
        private LinearLayout linearLayoutElementItem;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public ElementHolder(@NonNull View itemView) {

            super(itemView);
            view = itemView;
            path = new Path();
            holderContex = itemView.getContext();
            paint = new Paint();
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            linearLayoutElementItem = itemView.findViewById(R.id.elementItem);
            canvas = new Canvas(bitmap);
            tvNaziv = itemView.findViewById(R.id.tvNaziv);
            tvPocetak = itemView.findViewById(R.id.tvPocetak);
            tvKraj = itemView.findViewById(R.id.tvKraj);
            connectionHolders = itemView.findViewById(R.id.conncectionHolders);
            connectionHolder = itemView.findViewById(R.id.connectionHolder);
            startX = 0;
            startY = 0;
            stopX = 0;
            stopY = 0;
        }

        public float getStartX() {
            return startX;
        }

        public void setStartX(float startX) {
            this.startX = startX;
        }

        public float getStartY() {
            return startY;
        }

        public void setStartY(float startY) {
            this.startY = startY;
        }

        public float getStopX() {
            return stopX;
        }

        public void setStopX(float stopX) {
            this.stopX = stopX;
        }

        public float getStopY() {
            return stopY;
        }

        public void setStopY(float stopY) {
            this.stopY = stopY;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }

    @Override
    public int getItemCount() {
        return elementModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(elementModels, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(elementModels, i, i - 1);
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.putBoolean("listMovedAround", true);
        editor.apply();
        notifyItemMoved(fromPosition, toPosition);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemDismiss(int position) {

        elementModels.remove(position);

        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MyObjectsList", json);
        editor.apply();
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

}