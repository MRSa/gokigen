package jp.sourceforge.gokigen.diary;

import java.io.File;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

public class DiaryDateLineDrawer implements ICanvasDrawer
{
	int    totalDatas = 0;
    int    currentPosition = 0;
    int    dayItems[] = null;
    String dataFiles[] = null;
    String dataDirectory = null;

    /**
     *  �R���X�g���N�^
     * 
     */
	public DiaryDateLineDrawer()
    {
        // Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::DiaryDateLineDrawer() ");
    }
	
	/**
	 *  �t�@�C������������āA�\������f�[�^�����߂�
	 * 
	 * @param targetFileName
	 */
	public void prepare(String fileName)
	{
        // Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::prepare() " + fileName);

    	// �f�[�^�̏��Ԃ��m�F����
		checkDataOrder(fileName);
	}

	/**
	 *  �\���f�[�^���ЂƂO�Ɉړ�������
	 * 
	 * @return �O�ɂ���t�@�C����
	 */
	public String moveToPreviousData()
	{
		if (currentPosition > 0)
		{
			currentPosition--;
		}
		String fileName = dataFiles[currentPosition];
		return (dataDirectory + "/" + fileName);		
	}

	/**
	 *  �\���f�[�^���ЂƂ��Ɉړ�������
	 * 
	 * @return ���ɂ���t�@�C����
	 */
	public String moveToNextData()
	{
		if (currentPosition < (totalDatas - 1))
		{
            currentPosition++;
		}
		String fileName = dataFiles[currentPosition];
		return (dataDirectory + "/" + fileName);		
    }

	/**
	 *  �`�悷��
	 * 
	 */
    public void drawOnCanvas(Canvas canvas)
    {
    	//Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::drawOnCanvas() " + currentPosition + "/" + totalDatas);
        Paint paint = new Paint();
        paint.setColor(0x00004000);

        int widthMargin = 8;
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float bandWidth = width - widthMargin * 2;

        // �`��̈��h��Ԃ�
        //canvas.drawColor(0x00004000);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect((float) 0, (float) 0, (float) width, (float) height, paint);			

        // �X�P�[���̕\��
        paint.setColor(Color.LTGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawLine(widthMargin, (height / 2), (width - widthMargin), (height / 2), paint);

        // 2���Ԃ����ɋ�؂��������
        for (int index = 0; index <= 12; index++)
        {
        	float drawX = widthMargin + ((bandWidth * index) / 12);
        	float drawY = (height / 2);
        	if ((index % 3) == 0)
            {
        	    // 6���ԒP�ʂł́A������ɐL�΂�
        	    canvas.drawLine(drawX, (drawY - 3), drawX, (drawY + 3), paint);
            }
        	else
            {
        	    canvas.drawLine(drawX, (drawY), drawX, (drawY + 3), paint);
            }
        }

        float markerHeight = height / 2;

        // �����ɂ��킹���}�[�J�[��`��
        paint.setColor(Color.GREEN);
		for (int index = 0; index < totalDatas; index++)
		{
		    
			// dayItems�͕b�P�ʂȂ̂�...
			float dataPosition = ((float) dayItems[index]) * ((float) bandWidth) / (60 * 60 * 24) + widthMargin;
            canvas.drawRect((dataPosition - 1), (markerHeight - 3), (dataPosition + 1),(markerHeight + 1), paint);			
        	//canvas.drawRect((dataPosition - 1), (0), (dataPosition + 1),(height), paint);			
		}

        // ���݃f�[�^�̈ʒu�������}�[�J�[��`��
		if (totalDatas > currentPosition)
		{
			float dataPosition = ((float) dayItems[currentPosition]) * ((float) bandWidth) / (60 * 60 * 24) + widthMargin;
	        paint.setColor(Color.YELLOW);
		    canvas.drawRect((dataPosition - 2), (markerHeight - 4) , (dataPosition + 2), (markerHeight + 4), paint);
		}

        // ���x����\��
        paint.setColor(Color.LTGRAY);
        canvas.drawText("0", (widthMargin), (height - 2), paint);
        canvas.drawText("12", (width / 2), (height - 2), paint);
        canvas.drawText("24", (widthMargin + bandWidth), (height - 2), paint);
        
        //Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::drawOnCanvas() finished.");
    }

    /**
     *  �L�^�f�[�^�̓���(������)����������
     * 
     * @return �����̕�����
     */
    public String getDateTimeString()
    {
    	String outputData = "";
    	
    	outputData = " " + (currentPosition + 1) + "/" + totalDatas + " ";
    	return (outputData);
    }
    
    /**
     *  �^�b�`���ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	// �Ƃ肠�����������Ȃ�
        return (false);
    }
    
	/**
     *  ����Ɋ܂܂��f�[�^�̏��Ԃ���������
     * 
     * @param directory
     */
	private void checkDataOrder(String targetFileName)
    {
        try
        {
    		int slashIndex = targetFileName.lastIndexOf("/");
    		dataDirectory = targetFileName.substring(0, slashIndex);
            String fileString = targetFileName.substring(slashIndex + 1);
    		totalDatas = 0;
            dayItems = null;
            File checkDirectory = new File(dataDirectory);
            if (checkDirectory.exists() == false)
            {
                // �f�[�^���Ȃ�...�I������
                Log.v(Main.APP_IDENTIFIER, "checkDataOrder() abort... " + dataDirectory);
                return;
            }

            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List �� items ���\�[�g����I 
                java.util.Arrays.sort(dirList);
                
                dayItems = new int[dirList.length];
                dataFiles = new String[dirList.length];
                
                // �t�@�C���ꗗ�����グ��
                for (String fileName : dirList)
                {
                    // �f�[�^�t�@�C�����ǂ����`�F�b�N
                    if (fileName.endsWith(".txt") == false)
                    {
                        continue;
                    }

                    // �t�@�C�����̎��������𒊏o����
            		int hourIndex = fileName.indexOf("-diary") + 6;
            		if (hourIndex > 0)
            		{
                        try
                        {
                   		    String hourString = fileName.substring(hourIndex, hourIndex + 2);
                    		String minString = fileName.substring(hourIndex + 2, hourIndex + 4);
                    		String secString = fileName.substring(hourIndex + 4, hourIndex + 6);                    		
                            int seconds = (Integer.parseInt(hourString, 10) * 60 * 60) + (Integer.parseInt(minString, 10) * 60) + (Integer.parseInt(secString, 10));

                            // ���݂̈ʒu���������� 
                            if (fileName.matches(fileString) == true)
                            {
                            	// �f�[�^�̏��Ԃƌo�ߕb�����L������
                            	currentPosition = totalDatas;
                            }

                            // �f�[�^�̕b���ƃt�@�C�������L������
                            dayItems[totalDatas] = seconds;
                            dataFiles[totalDatas] = fileName;
                            
                            // �f�[�^�����J�E���g�A�b�v
                            totalDatas++;
                        }
                        catch (Exception e)
                        {
                        	// �������Ȃ�
                        }
             		}   
                }
            }    
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", fileName : " + targetFileName);
        }
    }
}
