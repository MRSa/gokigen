package jp.sourceforge.gokigen.diary;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SymbolListArrayAdapter extends ArrayAdapter<SymbolListArrayItem>
{
    private LayoutInflater inflater = null;
    private int textViewResourceId = 0;
    private List<SymbolListArrayItem> listItems = null;
    
    /**
     * �R���X�g���N�^
     */
    public SymbolListArrayAdapter(Context context, int textId, List<SymbolListArrayItem> items)
    {
        super(context, textId, items);

        // ���\�[�XID�ƕ\���A�C�e��
        textViewResourceId = textId;
        listItems = items;

        // Context����LayoutInflater���擾
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
        
        ImageView imageView = (ImageView) view.findViewWithTag("icon");
        imageView.setImageResource(item.getIconResource());

        int subIcon = item.getSubIconResource();
        //if (subIcon != 0)
        {
            ImageView subImage = (ImageView) view.findViewWithTag("subIcon");
            subImage.setImageResource(subIcon);
        }            

        TextView textView = (TextView)view.findViewWithTag("text");
        textView.setText(item.getTextResource1st());

        TextView contentView = (TextView)view.findViewWithTag("content");
        contentView.setText(item.getTextResource3rd());

        return (view);
    }
}
