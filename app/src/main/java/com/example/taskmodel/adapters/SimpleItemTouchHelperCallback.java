package com.example.taskmodel.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.interfaces.DoWork;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    Context mContext;
    private boolean mOrderChanged;
    private DoWork doWork;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Context context, DoWork doWork) {

        this.mAdapter = adapter;
        this.mContext = context;
        this.doWork = doWork;
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
        doWork.doWork();

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
            doWork.doWork();
            mOrderChanged = false;
        }
    }

    public interface ItemTouchHelperAdapter {

        void onItemMove(int oldPosition, int newPosition);

        void onItemDismiss(int position);
    }
}