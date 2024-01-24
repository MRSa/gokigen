package jp.sfjp.gokigen.okaken;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class SymbolListArrayAdapter extends ArrayAdapter<SymbolListArrayItem>
{
    private LayoutInflater inflater = null;
    private int textViewResourceId = 0;
    private List<SymbolListArrayItem> listItems = null;
    
    /**
     * コンストラクタ
     */
    public SymbolListArrayAdapter(Context context, int textId, List<SymbolListArrayItem> items)
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

        SymbolListArrayItem item = listItems.get(position);
        Bitmap icon = item.getIconResource();
        if (icon != null)
        {
            ImageView imageView = (ImageView) view.findViewWithTag("icon");
            imageView.setImageBitmap(icon);
        }

        // title : 選択した回答を表示
        TextView titleView = (TextView)view.findViewWithTag("title");
        titleView.setText(item.getAnsweredString());

        // detail : 誤答の場合、正しい回答を表示
        TextView detailView = (TextView)view.findViewWithTag("detail");
        if (item.getIsCorrect() == false)
        {
            detailView.setText("  (" + item.getCorrectAnswerString() + ") ");
        }
        else
        {
            detailView.setText(" ");        	
        }

        //  option :  option情報を表示
        TextView optionView = (TextView)view.findViewWithTag("option");
        //optionView.setText(item.getOptionString()); // URLを表示すると、意図したとおり動かん...
        optionView.setText(" ");

        return (view);
    }
}
