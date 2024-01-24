package jp.sourceforge.gokigen.viewsensor;

import java.util.List;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 *
 */
public class SymbolListArrayAdapter extends WearableListView.Adapter
{
    private static final String TAG = "SymbolListArrayAdapter";

    private List<SymbolListArrayItem> mListItems = null;

    /**
     *    SymbolListArrayAdapter
     *
     */
    public SymbolListArrayAdapter(List<SymbolListArrayItem> items)
    {
        mListItems = items;
    }

    /**
     *
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_array, parent, false);

        WearableListView.ViewHolder vh = new MyViewHolder(v);

        return (vh);
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try
        {
            SymbolListArrayItem item = mListItems.get(position);
            MyViewHolder myHolder = (MyViewHolder) holder;
            myHolder.setDrawObjects(item);
        }
        catch (Exception e)
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "onBindViewHolder ex.:" + e.getMessage());
            }
        }
        return;
    }

    @Override
    public int getItemCount()
    {
        if (mListItems == null)
        {
            return (0);
        }
        return (mListItems.size());
    }


    /**
     *
     *
     */
    static class MyViewHolder extends WearableListView.ViewHolder
    {
        public View mView = null;

        /**
         *
         * @return
         */
        public MyViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        /**
         *
         * @param data
         */
        public void setDrawObjects(SymbolListArrayItem data)
        {
            ImageView iconView = (ImageView) mView.findViewWithTag("icon");
            if (iconView != null)
            {
                int resId = data.getIconId();
                if (resId != 0)
                {
                    iconView.setImageResource(resId);
                }
            }

            TextView titleView  = (TextView) mView.findViewWithTag("title");
            if (titleView != null)
            {
                titleView.setText(data.getLabel());
            }

            TextView dataView  = (TextView) mView.findViewWithTag("detail");
            if (dataView != null)
            {
                dataView.setText(data.getValue());
            }
        }
    }
}
