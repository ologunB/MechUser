package com.mechanics.mechapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class LoopingPagerAdapter<T> extends PagerAdapter {

    protected Context context;
    private List<T> itemList;
    private SparseArray<View> viewCache = new SparseArray<>();

    private boolean isInfinite;
    private boolean canInfinite = true;

    private boolean dataSetChangeLock = false;

    public LoopingPagerAdapter(Context context, List<T> itemList, boolean isInfinite) {
        this.context = context;
        this.isInfinite = isInfinite;
        setItemList(itemList);
    }

     private void setItemList(List<T> itemList) {
        viewCache = new SparseArray<>();
        this.itemList = itemList;
        canInfinite = itemList.size() > 1;
        notifyDataSetChanged();
    }

     abstract View inflateView(int viewType, ViewGroup container, int listPosition);

     abstract void bindView(View convertView, int listPosition, int viewType);

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int listPosition = (isInfinite && canInfinite) ? getListPosition(position) : position;

        int viewType = getItemViewType(listPosition);

        View convertView;
        if (viewCache.get(viewType, null) == null) {
            convertView = inflateView(viewType, container, listPosition);
        } else {
            convertView = viewCache.get(viewType);
            viewCache.remove(viewType);
        }

        bindView(convertView, listPosition, viewType);

        container.addView(convertView);

        return convertView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        int listPosition = (isInfinite && canInfinite) ? getListPosition(position) : position;

        container.removeView((View) object);
        if (!dataSetChangeLock) viewCache.put(getItemViewType(listPosition), (View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        dataSetChangeLock = true;
        super.notifyDataSetChanged();
        dataSetChangeLock = false;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (itemList != null) {
            count = itemList.size();
        }
        if (isInfinite && canInfinite) {
            return count + 2;
        } else {
            return count;
        }
    }

     private int getItemViewType(int listPosition) {
        return 0;
    }

    int getListCount() {
        return itemList == null ? 0 : itemList.size();
    }

    private int getListPosition(int position) {
        if (!(isInfinite && canInfinite)) return position;
        int listPosition;
        if (position == 0) {
            listPosition = getCount() - 1 - 2; //First item is a dummy of last item
        } else if (position > getCount() - 2) {
            listPosition = 0; //Last item is a dummy of first item
        } else {
            listPosition = position - 1;
        }
        return listPosition;
    }

    public int getLastItemPosition() {
        if (isInfinite) {
            return itemList == null ? 0 : itemList.size();
        } else {
            return itemList == null ? 0 : itemList.size() - 1;
        }
    }
 }
