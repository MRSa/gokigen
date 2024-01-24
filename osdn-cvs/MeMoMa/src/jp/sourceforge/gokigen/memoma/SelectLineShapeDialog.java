package jp.sourceforge.gokigen.memoma;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

/**
 *   �ڑ����̌`���I������_�C�A���O��\������
 * 
 * @author MRSa
 *
 */
public class SelectLineShapeDialog implements ImageButton.OnClickListener
{
	private int lineThickness = LineStyleHolder.LINETHICKNESS_THIN;
	private int lineStyle = LineStyleHolder.LINESTYLE_STRAIGHT_NO_ARROW;
	private int lineShape = LineStyleHolder.LINESHAPE_NORMAL;
	
	private Context context = null;	
	private IResultReceiver resultReceiver = null;
	private LineStyleHolder lineStyleHolder = null;
	
	private View dialogLayout = null;

	/**
	 *    �R���X�g���N�^
	 * 
	 * @param arg
	 * @param holder
	 */
	public SelectLineShapeDialog(Context arg, LineStyleHolder holder)
	{
		context = arg;
		lineStyleHolder = holder;
	}

	/**
	 *    �_�C�A���O�őI���������ʂ���M���邽�߂̃��V�[�o��ݒ肷��
	 * 
	 * @param receiver
	 */
	public void setResultReceiver(IResultReceiver receiver)
	{
		resultReceiver = receiver;
	}
	
    /**
     *   �m�F�_�C�A���O����������
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.lineselection, null);
        dialogLayout = layout;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //  �_�C�A���O�ŕ\������f�[�^��ݒ肷��ꏊ

        // ���݂̐��̌`��Ǝ�ނ��擾����
        lineShape = lineStyleHolder.getLineShape();
        lineStyle = lineStyleHolder.getLineStyle();
        lineThickness = lineStyleHolder.getLineThickness();

        // ���̑���
        final ImageButton thin = (ImageButton) layout.findViewById(R.id.btnLineThicknessThin);
        thin.setOnClickListener(this);
        final ImageButton middle = (ImageButton) layout.findViewById(R.id.btnLineThicknessMiddle);
        middle.setOnClickListener(this);
        final ImageButton heavy = (ImageButton) layout.findViewById(R.id.btnLineThicknessHeavy);
        heavy.setOnClickListener(this);

        // ���̌`��
        final ImageButton straight = (ImageButton) layout.findViewById(R.id.btnLineShapeStraight);
        straight.setOnClickListener(this);
        final ImageButton tree = (ImageButton) layout.findViewById(R.id.btnLineShapeTree);
        tree.setOnClickListener(this);
        final ImageButton curve = (ImageButton) layout.findViewById(R.id.btnLineShapeCurve);
        curve.setOnClickListener(this);
        final ImageButton straightDash = (ImageButton) layout.findViewById(R.id.btnLineShapeStraightDash);
        straightDash.setOnClickListener(this);
        final ImageButton treeDash = (ImageButton) layout.findViewById(R.id.btnLineShapeTreeDash);
        treeDash.setOnClickListener(this);
        final ImageButton curveDash = (ImageButton) layout.findViewById(R.id.btnLineShapeCurveDash);
        curveDash.setOnClickListener(this);
        final ImageButton straightRarrow = (ImageButton) layout.findViewById(R.id.btnLineShapeStraightRarrow);
        straightRarrow.setOnClickListener(this);
        final ImageButton treeRarrow = (ImageButton) layout.findViewById(R.id.btnLineShapeTreeRarrow);
        treeRarrow.setOnClickListener(this);
        final ImageButton curveRarrow = (ImageButton) layout.findViewById(R.id.btnLineShapeCurveRarrow);
        curveRarrow.setOnClickListener(this);
        final ImageButton straightRarrowDash = (ImageButton) layout.findViewById(R.id.btnLineShapeStraightRarrowDash);
        straightRarrowDash.setOnClickListener(this);
        final ImageButton treeRarrowDash = (ImageButton) layout.findViewById(R.id.btnLineShapeTreeRarrowDash);
        treeRarrowDash.setOnClickListener(this);
        final ImageButton curveRarrowDash = (ImageButton) layout.findViewById(R.id.btnLineShapeCurveRarrowDash);
        curveRarrowDash.setOnClickListener(this);

        builder.setView(layout);
        builder.setTitle(context.getString(R.string.Title_SelectLineShape));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   boolean ret = false;
                	   setLineShape(lineStyle, lineShape, lineThickness);
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.finishSelectLineShape(lineStyle, lineShape, lineThickness);
                	   }
                	   updateButtonHighlightLineThickness(0);
                	   updateButtonHighlightLineShape(0);
                       if (ret == true)
                       {
                    	   dialog.dismiss();
                       }
                       else
                       {
                           dialog.cancel();
                       }
                       System.gc();
                   }
               });
        builder.setNegativeButton(context.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   boolean ret = false;
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.cancelSelectLineShape();
                	   }
                	   updateButtonHighlightLineThickness(0);
                	   updateButtonHighlightLineShape(0);
                       if (ret == true)
                       {
                    	   dialog.dismiss();
                       }
                       else
                       {
                           dialog.cancel();
                       }
                       System.gc();
                   }
               });
        return (builder.create());    	
    }

    /**
     *    �I�u�W�F�N�g���͗p�_�C�A���O�̕\������������
     *    �i�_�C�A���O�̕\�����������ɌĂ΂��j
     */
    public void prepareSelectLineShapeDialog(Dialog dialog, Integer objectKey)
    {
        // ���݂̐��̌`��Ǝ�ނ��擾����
        lineShape = lineStyleHolder.getLineShape();
        lineStyle = lineStyleHolder.getLineStyle();
        lineThickness = lineStyleHolder.getLineThickness();

        // ��ʁi�_�C�A���O�j�ŁA���ݑI�𒆂̂��̂��n�C���C�g�ɂ���B
        updateButtonHighlightLineThickness(getLineThicknessButtonId(lineThickness));
        updateButtonHighlightLineShape(getLineShapeButtonId(lineStyle, lineShape));

    }

    /**
     *    ���̌`���ݒ肷��
     * 
     * @param toSetLineStyle
     * @param toSetLineShape
     */
    public void setLineShape(int toSetLineStyle, int toSetLineShape, int toSetLineThickness)
    {
        lineStyleHolder.setLineShape(toSetLineShape);
        lineStyleHolder.setLineStyle(toSetLineStyle);
        lineStyleHolder.setLineThickness(toSetLineThickness);
        
        Log.v(Main.APP_IDENTIFIER, ":::CHANGE LINE :::  shape:" + toSetLineShape + " style:" + toSetLineStyle + " thickness:" + toSetLineThickness);
    }

    /**
     * 
     * 
     * @param id       �X�V����{�^����ID
     * @param judge ���f����{�^����ID
     */
    private void setButtonBorder(int id, int judge)
    {
    	try
    	{
            ImageButton button = (ImageButton) dialogLayout.findViewById(id);
            //GradientDrawable btnBackgroundShape = (GradientDrawable)button.getBackground();
            BitmapDrawable btnBackgroundShape = (BitmapDrawable)button.getBackground();
            if (id == judge)
            {
                //btnBackgroundShape.setColorFilter(Color.rgb(51, 181, 229), Mode.LIGHTEN);
            	btnBackgroundShape.setColorFilter(Color.BLUE, Mode.LIGHTEN);
            }
            else
            {
            	btnBackgroundShape.setColorFilter(Color.BLACK, Mode.LIGHTEN);
            } 
    	}
    	catch (Exception ex)
    	{
    		// 
    		Log.v(Main.APP_IDENTIFIER, "setButtonBorder(): " + ex.toString());
    	}
    	
    }
    
    /**
     *    �C���[�W�{�^���̑I����Ԃ��X�V���� (�ڑ����̑���)
     * 
     * @param buttonId
     */
    private void updateLineThickness(int buttonId)
    {
    	switch (buttonId)
    	{
          case R.id.btnLineThicknessMiddle:
        	lineThickness = LineStyleHolder.LINETHICKNESS_MIDDLE;
  	        break;
    	  case R.id.btnLineThicknessHeavy:
    		lineThickness = LineStyleHolder.LINETHICKNESS_HEAVY;
  	        break;
          case R.id.btnLineThicknessThin:
    	  default:
    		lineThickness = LineStyleHolder.LINETHICKNESS_THIN;
    	    break;
    	}
    }

    /**
     *    ���̌`��̑I����Ԃ��L���i�X�V�j����
     * 
     * @param buttonId
     */
    private void updateLineStyle(int buttonId)
    {
    	switch (buttonId)
    	{
          case R.id.btnLineShapeTree:
        	lineStyle = LineStyleHolder.LINESTYLE_TREESTYLE_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_NORMAL;
  	        break;

          case R.id.btnLineShapeCurve:
          	lineStyle = LineStyleHolder.LINESTYLE_CURVESTYLE_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_NORMAL;
  	        break;

          case R.id.btnLineShapeStraightDash:
          	lineStyle = LineStyleHolder.LINESTYLE_STRAIGHT_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
    	    break;

          case R.id.btnLineShapeTreeDash:
          	lineStyle = LineStyleHolder.LINESTYLE_TREESTYLE_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
    	    break;

          case R.id.btnLineShapeCurveDash:
          	lineStyle = LineStyleHolder.LINESTYLE_CURVESTYLE_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
  	        break;

    	  case R.id.btnLineShapeStraightRarrow:
          	lineStyle = LineStyleHolder.LINESTYLE_STRAIGHT_R_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_NORMAL;
      	    break;

    	  case R.id.btnLineShapeTreeRarrow:
            lineStyle = LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW;
            lineShape = LineStyleHolder.LINESHAPE_NORMAL;
      	    break;

          case R.id.btnLineShapeCurveRarrow:
          	lineStyle = LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_NORMAL;
    	    break;
  	        
      	  case R.id.btnLineShapeStraightRarrowDash:
          	lineStyle = LineStyleHolder.LINESTYLE_STRAIGHT_R_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
      	    break;

      	  case R.id.btnLineShapeTreeRarrowDash:
          	lineStyle = LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
      	    break;

          case R.id.btnLineShapeCurveRarrowDash:
          	lineStyle = LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_DASH;
    	    break;

          case R.id.btnLineShapeStraight:
    	  default:
          	lineStyle = LineStyleHolder.LINESTYLE_STRAIGHT_NO_ARROW;
        	lineShape = LineStyleHolder.LINESHAPE_NORMAL;
    	    break;
    	}
    	
    }    
    /**
     *    ���݂̑�����ݒ肷��
     * 
     * @param thickness
     * @return
     */
    private int getLineThicknessButtonId(int thickness)
    {
        int buttonId = R.id.btnLineThicknessThin;
    	switch (thickness)
        {
  	      case LineStyleHolder.LINETHICKNESS_HEAVY:
            buttonId = R.id.btnLineThicknessHeavy;
            break;

    	  case LineStyleHolder.LINETHICKNESS_MIDDLE:
              buttonId = R.id.btnLineThicknessMiddle;
              break;

    	  case LineStyleHolder.LINETHICKNESS_THIN:
          default:
        	  buttonId = R.id.btnLineThicknessThin;
        	  break;
        }
        return (buttonId);
    }    
    
    /**
     * 
     * 
     * @param currentLineStyle
     * @param currentLineShape
     * @return
     */
    private int getLineShapeButtonId(int currentLineStyle, int currentLineShape)
    {
        int buttonId = R.id.btnLineShapeStraight;
    	
        if ((currentLineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeTree;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeCurve;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeStraightDash;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeTreeDash;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeCurveDash;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeStraightRarrow;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeTreeRarrow;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeCurveRarrow;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeStraightRarrowDash;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeTreeRarrowDash;
        }
        else if ((currentLineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_DASH))
        {
        	buttonId = R.id.btnLineShapeCurveRarrowDash;
        }
        /**
        else  if ((currentLineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_NO_ARROW)&&
        		(currentLineShape == LineStyleHolder.LINESHAPE_NORMAL))
        {
        	buttonId = R.id.btnLineShapeStraight;
        }
        **/
        return (buttonId);
    }

    /**
     * 
     *     
     * @param id
     */
    private void updateButtonHighlightLineThickness(int id)
    {
    	setButtonBorder(R.id.btnLineThicknessThin, id);	
    	setButtonBorder(R.id.btnLineThicknessMiddle, id);	
    	setButtonBorder(R.id.btnLineThicknessHeavy, id);    	
    }
    
    /**
     * 
     * 
     * @param id
     */
    private void updateButtonHighlightLineShape(int id)
    {
        setButtonBorder(R.id.btnLineShapeStraight, id);	
    	setButtonBorder(R.id.btnLineShapeTree, id);	
    	setButtonBorder(R.id.btnLineShapeCurve, id);	

    	setButtonBorder(R.id.btnLineShapeStraightDash, id);	
    	setButtonBorder(R.id.btnLineShapeTreeDash, id);	
    	setButtonBorder(R.id.btnLineShapeCurveDash, id);	

    	setButtonBorder(R.id.btnLineShapeStraightRarrow, id);	
    	setButtonBorder(R.id.btnLineShapeTreeRarrow, id);	
    	setButtonBorder(R.id.btnLineShapeCurveRarrow, id);	

    	setButtonBorder(R.id.btnLineShapeStraightRarrowDash, id);	
    	setButtonBorder(R.id.btnLineShapeTreeRarrowDash, id);	
    	setButtonBorder(R.id.btnLineShapeCurveRarrowDash, id);    	
    }
    
    /**
     *    �{�^���������ꂽ���̏���...
     * 
     */
    public void onClick(View v)
    {
    	int id = v.getId();
    	
    	// �����ꂽ�{�^�����ڑ����̑����������ꍇ...
        if ((id == R.id.btnLineThicknessThin)||(id == R.id.btnLineThicknessMiddle)||(id == R.id.btnLineThicknessHeavy))
        {
        	updateButtonHighlightLineThickness(id);
        	updateLineThickness(id);
        	return;
        }

        // ���̌`����X�V�����ꍇ...
        updateButtonHighlightLineShape(id);
    	updateLineStyle(id);
    }

    public interface IResultReceiver
    {
        public abstract void finishSelectLineShape(int style, int shape, int thickness);
        public abstract void cancelSelectLineShape();
    }
}
