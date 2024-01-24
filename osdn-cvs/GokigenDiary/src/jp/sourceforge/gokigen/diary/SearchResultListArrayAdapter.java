package jp.sourceforge.gokigen.diary;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultListArrayAdapter extends ArrayAdapter<SearchResultListArrayItem>
{
    private LayoutInflater inflater = null;
    private int textViewResourceId = 0;
    private List<SearchResultListArrayItem> listItems = null;
    
    /**
     * コンストラクタ
     */
    public SearchResultListArrayAdapter(Context context, int textId, List<SearchResultListArrayItem> items)
    {
        super(context, textId, items);

        // リソースIDと表示アイテム
        textViewResourceId = textId;
        listItems = items;

        // ContextからLayoutInflaterを取得
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /**
     * 
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if(convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = inflater.inflate(textViewResourceId, null);
        }

        SearchResultListArrayItem item = listItems.get(position);

        TextView dateView = (TextView)view.findViewWithTag("date");
        dateView.setText(item.getDate());
        
        ImageView imageView = (ImageView) view.findViewWithTag("icon");
        imageView.setImageResource(item.getIconId());

        TextView contentView = (TextView)view.findViewWithTag("content");
        contentView.setText(item.getContent());
        
        return (view);
    }
}
