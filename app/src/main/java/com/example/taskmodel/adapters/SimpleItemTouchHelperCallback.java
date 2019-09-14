package com.example.taskmodel.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.backgroundTasks.UiHandlerThread;
import com.example.taskmodel.element.ElementModel;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    Context mContext;
    private boolean mOrderChanged;
    private UiHandlerThread uiHandlerThread;
    private List<ElementModel> elementModels;
    ElementModelViewAdapter mmAdapter;
    RecyclerView recyclerViewMain;
    SharedPreferences sharedPreferences;


    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Context context,
                                         UiHandlerThread uiHandlerThread,
                                         ElementModelViewAdapter mmAdapter,
                                         RecyclerView recyclerViewMain, List elementModels,
                                         SharedPreferences sharedPreferences) {

        this.mAdapter = adapter;
        this.mContext = context;
        this.uiHandlerThread = uiHandlerThread;
        this.mmAdapter = mmAdapter;
        this.recyclerViewMain = recyclerViewMain;
        this.elementModels = elementModels;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {

        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        mOrderChanged = true;
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(elementModels);
            editor.putString("MyObjectsList", json);
            editor.apply();

            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putSerializable("valuesList", (Serializable) elementModels);
            message.setData(bundle);
            uiHandlerThread.getHandler().sendMessage(message);
            mOrderChanged = false;
        }
    }

    public interface ItemTouchHelperAdapter {

        void onItemMove(int oldPosition, int newPosition);
        void onItemDismiss(int position);
    }
}