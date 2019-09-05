package com.example.taskmodel.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.R;
import com.example.taskmodel.element.ElementModel;
import com.example.taskmodel.view.LineTextView;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ElementModelViewAdapter extends RecyclerView.Adapter<ElementModelViewAdapter.ElementHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private List<ElementModel> elementModels;
    private ItemTouchHelper touchHelper;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private int limit;

    public ElementModelViewAdapter(List<ElementModel> elementModels, Context context, Set set) {

        this.elementModels = elementModels;
        this.mContext = context;
        this.limit = set.size();
    }

    @NonNull
    @Override
    public ElementModelViewAdapter.ElementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item, parent, false);
        ElementHolder holder = new ElementHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ElementHolder holder, final int position) {
        holder.tvNaziv.setText(elementModels.get(position).getNaziv());
        holder.tvPocetak.setText(String.valueOf(elementModels.get(position).getPocetak()));
        holder.tvKraj.setText(String.valueOf(elementModels.get(position).getId()));
    }

    class ElementHolder extends RecyclerView.ViewHolder {

        TextView tvNaziv;
        TextView tvPocetak;
        TextView tvKraj;
        LineTextView lineTextViewTopHalf;
        LineTextView lineTextViewBottomHalf;

        public ElementHolder(@NonNull View itemView) {

            super(itemView);
            tvNaziv = itemView.findViewById(R.id.tvNaziv);
            tvPocetak = itemView.findViewById(R.id.tvPocetak);
            tvKraj = itemView.findViewById(R.id.tvKraj);
            lineTextViewTopHalf = itemView.findViewById(R.id.lineTextViewTopHalf);
            lineTextViewBottomHalf = itemView.findViewById(R.id.lineTextViewBottomHalf);
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
        notifyItemMoved(fromPosition, toPosition);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.putBoolean("listMovedAround", true);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemDismiss(int position) {

        elementModels.remove(position);
        notifyItemRemoved(position);
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MyObjectsList", json);
        editor.apply();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }
}