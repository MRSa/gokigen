package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

/**
 *  �f�[�^���t�@�C���ɕۑ�����G���W������
 *  
 * @author MRSa
 *
 */
public class MeMoMaFileSavingEngine
{	
	private ExternalStorageFileUtility fileUtility = null;
	private String backgroundUri = null;
	private String userCheckboxString = null;    
	
	/**
	 *   �R���X�g���N�^
	 */
    public MeMoMaFileSavingEngine(ExternalStorageFileUtility utility, String bgUri, String checkBoxLabel)
    {
    	/** �t�@�C�����[�e�B���e�B���L������ **/
    	fileUtility = utility;
    	
    	/** �t�@�C�����o�b�N�A�b�v����f�B���N�g�����쐬���� **/
    	File dir = new File(fileUtility.getGokigenDirectory() + "/backup");
    	dir.mkdir();

    	//  �ݒ�f�[�^�ǂݏo���p...�B
    	backgroundUri = bgUri;
    	userCheckboxString = checkBoxLabel;
    }

    /**
     *   �t�@�C�������݂����Ƃ��A���l�[������
     * 
     * @param targetFileName
     * @param newFileName
     */
    private boolean renameFile(String targetFileName, String newFileName)
    {
    	boolean ret = true;
		File targetFile = new File(targetFileName);
		if (targetFile.exists() == true)
		{
			// �t�@�C�������݂����A�A�A�t�@�C�������P����Â����̂ɕύX����
			ret = targetFile.renameTo(new File(newFileName));
		}
		return (ret);
    }
    
    /**
     *    �ۊǃf�[�^�𕡐�����ۊǂ���B
     * 
     * @param fileName
     */
    private void backupFiles(String dirName, String backupFileName)
    {
    	//  �f�[�^���o�b�N�A�b�v����B�i�㏑���\��̃t�@�C��������΁A������R�s�[����j
        boolean result = true;
        try
        {
        	String  fileName = dirName +  "backup/" + backupFileName;
    		File backFile = new File(fileName + ".xml.bak5");
    		if (backFile.exists() == true)
    		{
    			// �t�@�C�������݂����A�A�A�폜����
    			backFile.delete();
    		}
    		backFile = null;
    		renameFile((fileName + ".xml.bak4"), (fileName + ".xml.bak5"));
    		renameFile((fileName + ".xml.bak3"), (fileName + ".xml.bak4"));
    		renameFile((fileName + ".xml.bak2"), (fileName + ".xml.bak3"));
    		renameFile((fileName + ".xml.bak1"), (fileName + ".xml.bak2"));
    		renameFile((fileName + ".xml.bak"), (fileName + ".xml.bak1"));
    		renameFile((dirName + backupFileName + ".xml"), (fileName + ".xml.bak"));
        }
        catch (Exception ex)
        {
        	// ������O�����������ꍇ�ɂ̓G���[�ƔF������B
        	result = false;
        }
		if (result == false)
		{
            // �o�b�N�A�b�v�t�@�C���̃R�s�[���s�����O�ɋL�q����
            Log.v(Main.APP_IDENTIFIER, "rename failure : " + dirName +  backupFileName + ".xml");
		}
		return;
    }
    
    /**
     *    �f�[�^��(XML�`����)�ۊǂ���B
     * 
     * @param fileName
     * @param objectHolder
     * @return
     */
    private String storeToXmlFile(String fileName, MeMoMaObjectHolder objectHolder)
    {
    	String resultMessage = "";
        try
        {
            FileWriter writer = new FileWriter(new File(fileName + ".xml"));    	
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(Main.APP_NAMESPACE, "memoma");
            
            // �^�C�g���̏o��
            serializer.startTag(Main.APP_NAMESPACE, "title");
            serializer.text(objectHolder.getDataTitle());
            serializer. endTag(Main.APP_NAMESPACE, "title");

            // �w�i���̏o��
            serializer.startTag(Main.APP_NAMESPACE, "background");
            serializer.text(objectHolder.getBackground());
            serializer. endTag(Main.APP_NAMESPACE, "background");

            // �w�i�摜URI�̏o��
            serializer.startTag(Main.APP_NAMESPACE, "backgroundUri");
            serializer.text(backgroundUri);
            serializer. endTag(Main.APP_NAMESPACE, "backgroundUri");
            
            // ���[�U�`�F�b�N�{�b�N�X���̏o��
            serializer.startTag(Main.APP_NAMESPACE, "userCheckboxString");
            serializer.text(userCheckboxString);
            serializer. endTag(Main.APP_NAMESPACE, "userCheckboxString");
            
            serializer.startTag(Main.APP_NAMESPACE, "objserial");
            serializer.text(Integer.toString(objectHolder.getSerialNumber()));
            serializer.endTag(Main.APP_NAMESPACE, "objserial");

            serializer.startTag(Main.APP_NAMESPACE, "lineserial");
            serializer.text(Integer.toString(objectHolder.getConnectLineHolder().getSerialNumber()));
            serializer.endTag(Main.APP_NAMESPACE, "lineserial");
            
            
        	// �I�u�W�F�N�g�̏o�� �i�ێ����Ă�����̂͂��ׂĕ\������j
        	Enumeration<Integer> keys = objectHolder.getObjectKeys();
            while (keys.hasMoreElements())
            {
                Integer key = keys.nextElement();
                MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);
                serializer.startTag(Main.APP_NAMESPACE, "object");

                serializer.attribute(Main.APP_NAMESPACE, "key", Integer.toString(key));

                serializer.startTag(Main.APP_NAMESPACE, "rect");
                serializer.startTag(Main.APP_NAMESPACE, "top");
                serializer.text(Float.toString(pos.rect.top));
                serializer. endTag(Main.APP_NAMESPACE, "top");
                serializer.startTag(Main.APP_NAMESPACE, "left");
                serializer.text(Float.toString(pos.rect.left));
                serializer. endTag(Main.APP_NAMESPACE, "left");
                serializer.startTag(Main.APP_NAMESPACE, "right");
                serializer.text(Float.toString(pos.rect.right));
                serializer. endTag(Main.APP_NAMESPACE, "right");
                serializer.startTag(Main.APP_NAMESPACE, "bottom");
                serializer.text(Float.toString(pos.rect.bottom));
                serializer. endTag(Main.APP_NAMESPACE, "bottom");
                serializer. endTag(Main.APP_NAMESPACE, "rect");

                serializer.startTag(Main.APP_NAMESPACE, "drawStyle");
                serializer.text(Integer.toString(pos.drawStyle));
                serializer. endTag(Main.APP_NAMESPACE, "drawStyle");

                serializer.startTag(Main.APP_NAMESPACE, "icon");
                serializer.text(Integer.toString(pos.icon));
                serializer. endTag(Main.APP_NAMESPACE, "icon");

                serializer.startTag(Main.APP_NAMESPACE, "label");
                serializer.text(pos.label);
                serializer. endTag(Main.APP_NAMESPACE, "label");

                serializer.startTag(Main.APP_NAMESPACE, "detail");
                serializer.text(pos.detail);
                serializer. endTag(Main.APP_NAMESPACE, "detail");
/**
                serializer.startTag(Main.APP_NAMESPACE, "otherInfoUri");
                serializer.text(pos.otherInfoUri);
                serializer. endTag(Main.APP_NAMESPACE, "otherInfoUri");

                serializer.startTag(Main.APP_NAMESPACE, "backgroundUri");
                serializer.text(pos.backgroundUri);
                serializer. endTag(Main.APP_NAMESPACE, "backgroundUri");

                serializer.startTag(Main.APP_NAMESPACE, "objectStatus");
                serializer.text(pos.objectStatus);
                serializer. endTag(Main.APP_NAMESPACE, "objectStatus");
**/
                serializer.startTag(Main.APP_NAMESPACE, "userChecked");
                serializer.text(Boolean.toString(pos.userChecked));
                serializer. endTag(Main.APP_NAMESPACE, "userChecked");
                
                serializer.startTag(Main.APP_NAMESPACE, "labelColor");
                serializer.text(Integer.toString(pos.labelColor));
                serializer. endTag(Main.APP_NAMESPACE, "labelColor");

                serializer.startTag(Main.APP_NAMESPACE, "objectColor");
                serializer.text(Integer.toString(pos.objectColor));
                serializer. endTag(Main.APP_NAMESPACE, "objectColor");

                serializer.startTag(Main.APP_NAMESPACE, "paintStyle");
                serializer.text(pos.paintStyle);
                serializer. endTag(Main.APP_NAMESPACE, "paintStyle");
               
                serializer.startTag(Main.APP_NAMESPACE, "strokeWidth");
                serializer.text(Float.toString(pos.strokeWidth));
                serializer. endTag(Main.APP_NAMESPACE, "strokeWidth");

                serializer.startTag(Main.APP_NAMESPACE, "fontSize");
                serializer.text(Float.toString(pos.fontSize));
                serializer. endTag(Main.APP_NAMESPACE, "fontSize");

                serializer.endTag(Main.APP_NAMESPACE, "object");
            }

            // �ڑ����̏o�� �i�ێ����Ă�����̂͂��ׂĕ\������j
        	Enumeration<Integer> lineKeys = objectHolder.getConnectLineHolder().getLineKeys();
            while (lineKeys.hasMoreElements())
            {
                Integer key = lineKeys.nextElement();
                MeMoMaConnectLineHolder.ObjectConnector line = objectHolder.getConnectLineHolder().getLine(key);
                serializer.startTag(Main.APP_NAMESPACE, "line");
                serializer.attribute(Main.APP_NAMESPACE, "key", Integer.toString(key));

                serializer.startTag(Main.APP_NAMESPACE, "fromObjectKey");
                serializer.text(Integer.toString(line.fromObjectKey));
                serializer.endTag(Main.APP_NAMESPACE, "fromObjectKey");

                serializer.startTag(Main.APP_NAMESPACE, "toObjectKey");
                serializer.text(Integer.toString(line.toObjectKey));
                serializer.endTag(Main.APP_NAMESPACE, "toObjectKey");

                serializer.startTag(Main.APP_NAMESPACE, "lineStyle");
                serializer.text(Integer.toString(line.lineStyle));
                serializer.endTag(Main.APP_NAMESPACE, "lineStyle");

                serializer.startTag(Main.APP_NAMESPACE, "lineShape");
                serializer.text(Integer.toString(line.lineShape));
                serializer.endTag(Main.APP_NAMESPACE, "lineShape");

                serializer.startTag(Main.APP_NAMESPACE, "lineThickness");
                serializer.text(Integer.toString(line.lineThickness));
                serializer.endTag(Main.APP_NAMESPACE, "lineThickness");
/**
                serializer.startTag(Main.APP_NAMESPACE, "fromShape");
                serializer.text(Integer.toString(line.fromShape));
                serializer.endTag(Main.APP_NAMESPACE, "fromShape");

                serializer.startTag(Main.APP_NAMESPACE, "toShape");
                serializer.text(Integer.toString(line.toShape));
                serializer.endTag(Main.APP_NAMESPACE, "toShape");
                
                serializer.startTag(Main.APP_NAMESPACE, "fromString");
                serializer.text(line.fromString);
                serializer.endTag(Main.APP_NAMESPACE, "fromString");

                serializer.startTag(Main.APP_NAMESPACE, "toString");
                serializer.text(line.toString);
                serializer.endTag(Main.APP_NAMESPACE, "toString");
**/
                serializer.endTag(Main.APP_NAMESPACE, "line");
            }

            serializer.endTag(Main.APP_NAMESPACE, "memoma");
            serializer.endDocument();
            serializer.flush();
            writer.close();
        }
        catch (Exception e)
        {
        	resultMessage = " ERR>" + e.toString();
            Log.v(Main.APP_IDENTIFIER, resultMessage);
            e.printStackTrace();
        } 
        return (resultMessage);
    }

    /**
     *    �I�u�W�F�N�g��ۑ�����
     * 
     * @param objectHolder
     * @return
     */
    public String saveObjects(MeMoMaObjectHolder objectHolder)
    {
		// �f�[�^�^�C�g�����Ȃ��ꍇ...�ۑ������͍s��Ȃ��B
    	if (objectHolder.getDataTitle().length() <= 0)
        {
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaFileSavingEngine::saveObjects() : specified file name is illegal, save aborted. : " + objectHolder.getDataTitle() );

    		return ("");
        }

    	// �o�b�N�A�b�v��ۑ�����
    	backupFiles(fileUtility.getGokigenDirectory() + "/" , objectHolder.getDataTitle());
    	
        // �t�@�C�����̐ݒ� ... (�g���q�Ȃ�)
    	String fileName = fileUtility.getGokigenDirectory() + "/" + objectHolder.getDataTitle();

    	// �f�[�^��ۊǂ���
        String result = storeToXmlFile(fileName, objectHolder);

        return (result);
    }
}
