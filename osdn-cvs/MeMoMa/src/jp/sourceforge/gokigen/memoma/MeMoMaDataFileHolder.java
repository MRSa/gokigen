package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 *    �߂��܂̃f�[�^�t�@�C������ێ�����N���X�@�iArrayAdapter���g���j
 * 
 * @author MRSa
 *
 */
public class MeMoMaDataFileHolder extends ArrayAdapter<String> implements FilenameFilter
{
	private ExternalStorageFileUtility fileUtility = null;
	private String fileExtension = "";

	/**
	 *    �R���X�g���N�^
	 * 
	 */
    public MeMoMaDataFileHolder(Context context, int textViewRscId, ExternalStorageFileUtility utility, String extension)
    {
    	super(context, textViewRscId);
    	fileUtility = utility;
    	fileExtension = extension;
    }
    
    /**
     *    �t�@�C���ꗗ�𐶐�����B
     * 
     */
    public int updateFileList(String currentFileName, String extendDirectory)
    {
    	int outputIndex = -1;
    	
    	clear();
        String directory = fileUtility.getGokigenDirectory();
        if (extendDirectory != null)
        {
        	// �f�B���N�g�����w�肳��Ă������ɂ́A���̃f�B���N�g����ǉ�����
        	directory = directory + extendDirectory;
        }
    	String[] dirFileList = (new File(directory)).list(this);
    	for (int index = 0; index < dirFileList.length; index++)
    	{
    		String fileName = dirFileList[index].substring(0, dirFileList[index].indexOf(fileExtension));
    		if (fileName.contentEquals(currentFileName) == true)  // �t�@�C���擪�ɂȂ��ꍇ�͒ǉ�����B
    		{
    		    // �I�������C���f�b�N�X��ݒ肷��B
    			outputIndex = index;
    		}
            add(fileName);
    		//Log.v(Main.APP_IDENTIFIER, fileName + ", File : " + dirFileList[index]);
    	}
    	System.gc();
    	
    	//Log.v(Main.APP_IDENTIFIER, "::::::: "  + " (" + currentFileName + ") : " + outputIndex);
    	return (outputIndex);
    }

    /**
     *    �󂯕t����t�@�C�����̃t�B���^����������B
     *    (�w�肳�ꂽ�g���q�����ȃt�@�C���������o����B)
     * 
     */
    public boolean accept(File dir, String filename)
    {
    	return (filename.endsWith(fileExtension) ? true : false);
    }
}
