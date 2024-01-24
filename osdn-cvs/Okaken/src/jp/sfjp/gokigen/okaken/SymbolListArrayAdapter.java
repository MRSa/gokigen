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
        Bitmap icon = item.getIconResource();
        if (icon != null)
        {
            ImageView imageView = (ImageView) view.findViewWithTag("icon");
            imageView.setImageBitmap(icon);
        }

        // title : �I�������񓚂�\��
        TextView titleView = (TextView)view.findViewWithTag("title");
        titleView.setText(item.getAnsweredString());

        // detail : �듚�̏ꍇ�A�������񓚂�\��
        TextView detailView = (TextView)view.findViewWithTag("detail");
        if (item.getIsCorrect() == false)
        {
            detailView.setText("  (" + item.getCorrectAnswerString() + ") ");
        }
        else
        {
            detailView.setText(" ");        	
        }

        //  option :  option����\��
        TextView optionView = (TextView)view.findViewWithTag("option");
        //optionView.setText(item.getOptionString()); // URL��\������ƁA�Ӑ}�����Ƃ��蓮����...
        optionView.setText(" ");

        return (view);
    }
}
