package org.foenix.shufflerecycler;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import org.foenix.shufflerecycler.model.Item;
import org.foenix.shufflerecycler.model.ItemCollection;
import org.foenix.shufflerecycler.utils.DrawableUtils;
import org.foenix.shufflerecycler.utils.ViewUtils;

/**
 * Created by Foenix on 16.12.2016.
 * a few rows onBindViewHolder, onCheckCanStartDrag taken here
 * https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_on_longpress
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>
        implements DraggableItemAdapter<RecyclerAdapter.RecyclerViewHolder> {

    public interface IDataChangeListener {
        void onMoveItem(Item item);
    }

    private ItemCollection mItems;
    private IDataChangeListener mListener;

    private interface Draggable extends DraggableItemConstants {
    }

    public RecyclerAdapter(ItemCollection items, IDataChangeListener listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return (mItems == null) ? 0 : mItems.getItemCount();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.bindTo(mItems.getData().get(position));
        final int dragState = holder.getDragStateFlags();
        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_list;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (mItems.getData() != null) {
            return mItems.getData().get(position).getId();
        }
        return 0;
    }

    public void swapData(ItemCollection items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void emptyData() {
        mItems.clearData();
        notifyDataSetChanged();
    }

    @Override
    public boolean onCheckCanStartDrag(RecyclerViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mContainer;
        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);
        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(RecyclerViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        //Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        if (fromPosition == toPosition) {
            return;
        }
        mItems.moveItem(fromPosition, toPosition);
        mListener.onMoveItem(mItems.getData().get(toPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }


    public static class RecyclerViewHolder extends AbstractDraggableItemViewHolder {
        private TextView mTextView;
        private FrameLayout mContainer;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            setupViews(itemView);
        }

        private void setupViews(View view) {
            mTextView = (TextView) view.findViewById(R.id.description);
            mContainer = (FrameLayout) view.findViewById(R.id.container);
        }

        void bindTo(Item item) {
            mTextView.setText(item.getDescription());
        }
    }
}
