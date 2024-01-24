package jp.sourceforge.gokigen.memoma;

import java.util.Enumeration;
import java.util.Hashtable;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

/**
 *   �\���I�u�W�F�N�g�̏���ێ�����N���X
 * 
 * @author MRSa
 *
 */
public class MeMoMaObjectHolder
{
	public static final int ID_NOTSPECIFY = -1;
	
    public static final int DRAWSTYLE_RECTANGLE = 0;
    public static final int DRAWSTYLE_ROUNDRECT = 1;
    public static final int DRAWSTYLE_OVAL = 2;
    public static final int DRAWSTYLE_DIAMOND = 3;
    public static final int DRAWSTYLE_HEXAGONAL = 4;
    public static final int DRAWSTYLE_PARALLELOGRAM = 5;
    public static final int DRAWSTYLE_KEYBOARD = 6;
    public static final int DRAWSTYLE_PAPER = 7;
    public static final int DRAWSTYLE_DRUM = 8;
    public static final int DRAWSTYLE_CIRCLE = 9;
    public static final int DRAWSTYLE_NO_REGION = 10;
    
    public static final int DRAWSTYLE_LOOP_START = 11;
    public static final int DRAWSTYLE_LOOP_END = 12;
    public static final int DRAWSTYLE_LEFT_ARROW = 13;
    public static final int DRAWSTYLE_DOWN_ARROW = 14;
    public static final int DRAWSTYLE_UP_ARROW = 15;
    public static final int DRAWSTYLE_RIGHT_ARROW = 16;

    public static final float ROUNDRECT_CORNER_RX = 8;
    public static final float ROUNDRECT_CORNER_RY = 8;
    
    public static final float STOROKE_BOLD_WIDTH = 3.5f;
    public static final float STOROKE_NORMAL_WIDTH = 0.0f;
    
    public static final float DUPLICATEPOSITION_MARGIN = 15.0f;

    public static final float OBJECTSIZE_DEFAULT_X = 144.0f;
	public static final float OBJECTSIZE_DEFAULT_Y = (OBJECTSIZE_DEFAULT_X / 16.0f * 9.0f);

	public static final float OBJECTSIZE_MINIMUM_X = 48.0f;
	public static final float OBJECTSIZE_MINIMUM_Y = (OBJECTSIZE_MINIMUM_X / 16.0f * 9.0f);
	
	public static final float OBJECTSIZE_MAXIMUM_X = 14400.0f;
	public static final float OBJECTSIZE_MAXIMUM_Y =  (OBJECTSIZE_MAXIMUM_X / 16.0f * 9.0f);

	public static final float OBJECTSIZE_STEP_X = OBJECTSIZE_MINIMUM_X * 1.0f;
	public static final float OBJECTSIZE_STEP_Y = OBJECTSIZE_MINIMUM_Y * 1.0f;
	
	public static final float FONTSIZE_DEFAULT = 12.0f;
	
    private MeMoMaConnectLineHolder connectLineHolder = null;
    
    /**
     *    �I�u�W�F�N�g�̏�� 
     * 
     * @author MRSa
     *
     */
	public class PositionObject
	{
		private Integer key;                 // �I�u�W�F�N�g���ʎq (�ύX�s�j
		public RectF rect;                    // �I�u�W�F�N�g�̑傫��
	    public int drawStyle;               // �I�u�W�F�N�g�̌`��

	    public int icon;                        // �I�u�W�F�N�g�̃A�C�R��
	    public String label;                   // �I�u�W�F�N�g�̕\�����x��
	    public String detail;                  // �I�u�W�F�N�g�̐���
	    //public String backgroundUri;     // �I�u�W�F�N�g�̔w�i�摜
	    //public String otherInfoUri;        // �⑫�i�ʐ^�Ƃ��ւ�URI�j
	    //public String objectStatus;        // �I�u�W�F�N�g�̏��
	    public boolean userChecked;    // ���[�U�`�F�b�N�{�b�N�X

	    public int labelColor;               // �I�u�W�F�N�g���ɕ\������F
	    public int objectColor;             // �I�u�W�F�N�g�̐F
	    public String paintStyle;           // �I�u�W�F�N�g�̕\�����@ �i�g���̂݁A�h��Ԃ��A�h��Ԃ��Ƙg���j
	    public float strokeWidth;         // �g���̑���
	    public float fontSize;               // �t�H���g�T�C�Y

	    /**
	     *    �R���X�g���N�^ (�L�[��ݒ肷��)
	     * @param id
	     */
	    public PositionObject(int id)
	    {
	    	key = id;
	    }

	    /**
	     *    �I�u�W�F�N�g�̃L�[���擾����
	     * @return
	     */
	    public Integer getKey()
	    {
	    	return (key);
	    }
	};

	private Hashtable<Integer, PositionObject> objectPoints = null;
	private Integer serialNumber = 1;
	private String  dataTitle = "";
	private String  background = "";
	private Context parent = null;

    public MeMoMaObjectHolder(Context context, MeMoMaConnectLineHolder lineHolder)
    {
		  objectPoints = new Hashtable<Integer, PositionObject>();
		  connectLineHolder = lineHolder;
		  parent = context;
    }

    /**
     *    �f�[�^�̗L�������� (true �̏ꍇ�A�f�[�^�͂Ȃ��B)
     * 
     * @return
     */
    public boolean isEmpty()
    {	
    	if ((connectLineHolder == null)||(objectPoints == null))
    	{
    		return (true);
    	}
    	return (objectPoints.isEmpty());
    }
    
    public MeMoMaConnectLineHolder getConnectLineHolder()
    {
    	return (connectLineHolder);
    }
    
    public void setDataTitle(String title)
    {
    	dataTitle = title;
    }
    
    public String getDataTitle()
    {
    	return (dataTitle);
    }

    public void setBackground(String data)
    {
    	background = data;
    }
    
    public String getBackground()
    {
    	return (background);
    }

    public int getCount()
    {
    	return (objectPoints.size());
    }

    public Enumeration<Integer> getObjectKeys()
    {
    	return (objectPoints.keys());
    }

    public PositionObject getPosition(Integer key)
    {
        return  (objectPoints.get(key));
    }

    public boolean removePosition(Integer key)
    {
    	objectPoints.remove(key);
    	Log.v(Main.APP_IDENTIFIER, "REMOVE : " + key);
    	return (true);
    }
    
    public void removeAllPositions()
    {
        objectPoints.clear();
        serialNumber = 1;
    }

    public void setSerialNumber(int id)
    {
    	serialNumber = (id == ID_NOTSPECIFY) ? ++serialNumber : id;
    }
    
    public int getSerialNumber()
    {
    	return (serialNumber);
    }

    public void dumpPositionObject(PositionObject position)
    {
    	if (position == null)
    	{
    		return;
    	}
        Log.v(Main.APP_IDENTIFIER, "[" + position.rect.left + "," + position.rect.top + "][" + position.rect.right + "," + position.rect.bottom + "] " + "label : " + position.label + " detail : " + position.detail);
    }
    
    
    /**
     *   �I�u�W�F�N�g�𕡐�����B
     * 
     * @param key
     * @return
     */
    public PositionObject duplicatePosition(int key)
    {
    	PositionObject orgPosition = objectPoints.get(key);
    	if (orgPosition == null)
    	{
    		// ���̃I�u�W�F�N�g��������Ȃ������̂ŁA���������ɖ߂�
    		return (null);
    	}
    	PositionObject position = new PositionObject(serialNumber);
    	position.rect = new RectF();
    	position.rect.left = orgPosition.rect.left + DUPLICATEPOSITION_MARGIN;
    	position.rect.right = orgPosition.rect.right + DUPLICATEPOSITION_MARGIN;
    	position.rect.top = orgPosition.rect.top + DUPLICATEPOSITION_MARGIN;
    	position.rect.bottom = orgPosition.rect.bottom + DUPLICATEPOSITION_MARGIN;
    	position.drawStyle = orgPosition.drawStyle;
    	position.icon = orgPosition.icon;
    	position.label = orgPosition.label;
    	position.detail = orgPosition.detail;
    	//position.otherInfoUri = orgPosition.otherInfoUri;
    	//position.backgroundUri = orgPosition.backgroundUri;
    	//position.objectStatus = orgPosition.objectStatus;
    	position.userChecked = orgPosition.userChecked;
    	position.objectColor = orgPosition.objectColor;
    	position.labelColor = orgPosition.labelColor;
    	position.paintStyle = orgPosition.paintStyle;
    	position.strokeWidth = orgPosition.strokeWidth;
    	position.fontSize = orgPosition.fontSize;
		objectPoints.put(serialNumber, position);
		serialNumber++;
		return (position);
    }

    public PositionObject createPosition(int id)
    {
    	PositionObject position = new PositionObject(id);
    	position.rect = new RectF();
    	position.rect.top = 0;
    	position.rect.bottom = OBJECTSIZE_DEFAULT_Y;
    	position.rect.left = 0;
    	position.rect.right = OBJECTSIZE_DEFAULT_X;
    	position.drawStyle = MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE;
    	position.icon = 0;
    	position.label = "";
    	position.detail = "";
    	//position.otherInfoUri = "";
    	//position.backgroundUri = "";
    	//position.objectStatus = "";
    	position.userChecked = false;
    	position.objectColor = Color.WHITE;
    	position.labelColor = Color.WHITE;
    	position.paintStyle = Paint.Style.STROKE.toString();
    	position.strokeWidth = STOROKE_NORMAL_WIDTH;
    	position.fontSize = FONTSIZE_DEFAULT;
		objectPoints.put(id, position);
		return (position);    	
    }
    
    public PositionObject createPosition(float x, float y, int drawStyle)
    {
    	PositionObject position = createPosition(serialNumber);
    	position.rect.left = position.rect.left + x;
    	position.rect.right = position.rect.right + x;
    	position.rect.top = position.rect.top + y;
    	position.rect.bottom = position.rect.bottom + y;
    	position.drawStyle = drawStyle;
		serialNumber++;
		return (position);
    }

    /**
     *   �I�u�W�F�N�g�̃T�C�Y���g�傷��
     * 
     * @param key
     */
    public void expandObjectSize(Integer key)
    {
    	PositionObject position = objectPoints.get(key);
    	if (position == null)
    	{
    		// ���̃I�u�W�F�N�g��������Ȃ������̂ŁA���������ɖ߂�
    		return;
    	}
        float width = position.rect.right - position.rect.left;
        float height = position.rect.bottom - position.rect.top;
        if (((width + (OBJECTSIZE_STEP_X * 2.0f)) > OBJECTSIZE_MAXIMUM_X)||((height + (OBJECTSIZE_STEP_Y * 2.0f)) > OBJECTSIZE_MAXIMUM_Y))
        {
            // �g�僊�~�b�g�������B�B�B�g�債�Ȃ�
    		String outputMessage = parent.getString(R.string.object_bigger_limit) + " ";
            Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        position.rect.left = position.rect.left - OBJECTSIZE_STEP_X;
        position.rect.right = position.rect.right + OBJECTSIZE_STEP_X;
        position.rect.top = position.rect.top - OBJECTSIZE_STEP_Y;
        position.rect.bottom = position.rect.bottom + OBJECTSIZE_STEP_Y;	
    }

    /**
     *   �I�u�W�F�N�g�̃T�C�Y���k������
     * 
     * @param key
     */
    public void shrinkObjectSize(Integer key)
    {
    	PositionObject position = objectPoints.get(key);
    	if (position == null)
    	{
    		// ���̃I�u�W�F�N�g��������Ȃ������̂ŁA���������ɖ߂�
    		return;
    	}
        float width = position.rect.right - position.rect.left;
        float height = position.rect.bottom - position.rect.top;
        if (((width - (OBJECTSIZE_STEP_X * 2.0f)) < OBJECTSIZE_MINIMUM_X)||((height - (OBJECTSIZE_STEP_Y * 2.0f)) < OBJECTSIZE_MINIMUM_Y))
        {
            // �k�����~�b�g�������B�B�B�k�����Ȃ�
    		String outputMessage = parent.getString(R.string.object_small_limit) + " ";
            Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        position.rect.left = position.rect.left + OBJECTSIZE_STEP_X;
        position.rect.right = position.rect.right - OBJECTSIZE_STEP_X;
        position.rect.top = position.rect.top + OBJECTSIZE_STEP_Y;
        position.rect.bottom = position.rect.bottom - OBJECTSIZE_STEP_Y;	    	
    }

    public MeMoMaConnectLineHolder getLineHolder()
	{
		return (connectLineHolder);
	}
	
	static public int getObjectDrawStyleIcon(int drawStyle)
	{
		int icon = 0;
    	if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE)
    	{
    		icon  = R.drawable.btn_rectangle;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_ROUNDRECT)
    	{
    		icon = R.drawable.btn_roundrect;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_OVAL)
    	{
    		icon = R.drawable.btn_oval;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DIAMOND)
    	{
    		icon = R.drawable.btn_diamond;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_HEXAGONAL)
    	{
    		icon = R.drawable.btn_hexagonal;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_PARALLELOGRAM)
    	{
    		icon = R.drawable.btn_parallelogram;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_KEYBOARD)
    	{
    		icon = R.drawable.btn_keyboard;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_PAPER)
    	{
    		icon = R.drawable.btn_paper;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DRUM)
    	{
    		icon = R.drawable.btn_drum;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_CIRCLE)
    	{
    		icon = R.drawable.btn_circle;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_NO_REGION)
    	{
    		icon = R.drawable.btn_noregion;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LOOP_START)
    	{
    		icon = R.drawable.btn_trapezoidy_up;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LOOP_END)
    	{
    		icon = R.drawable.btn_trapezoidy_down;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LEFT_ARROW)
    	{
    		icon = R.drawable.btn_arrow_left;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DOWN_ARROW)
    	{
    		icon = R.drawable.btn_arrow_down;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_UP_ARROW)
    	{
    		icon = R.drawable.btn_arrow_up;
    	}
    	else if (drawStyle == MeMoMaObjectHolder.DRAWSTYLE_RIGHT_ARROW)
    	{
    		icon = R.drawable.btn_arrow_right;
    	}
    	return (icon);
	}
	
}
