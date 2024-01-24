package jp.sourceforge.gokigen.memoma;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 *    �t�@�C���I���_�C�A���O
 * 
 * @author MRSa
 *
 */
public class FileSelectionDialog
{
	private Context context = null;
	private IResultReceiver resultReceiver = null;
	private ExternalStorageFileUtility fileUtility = null;
    private MeMoMaDataFileHolder dataFileHolder = null;
    private String title = null;
    private String fileExtension = null;
    private Dialog dialogRef = null;
    
	/**
	 *    �R���X�g���N�^
	 * 
	 * @param arg
	 */
	public FileSelectionDialog(Context arg, String titleMessage, ExternalStorageFileUtility utility, String extension, IResultReceiver receiver)
	{
	    context = arg;	
	    resultReceiver = receiver;
		title = titleMessage;
        fileUtility = utility;
        fileExtension = extension;
        dataFileHolder = new MeMoMaDataFileHolder(context, android.R.layout.simple_list_item_1, fileUtility, extension);
	}

	/**
	 *   �t�@�C���ꗗ�f�[�^������I
	 * 
	 * @param currentFileName
	 * @param extendDirectory
	 */
	public void prepare(String currentFileName, String extendDirectory)
	{
		dataFileHolder.updateFileList(currentFileName, extendDirectory);
	}

    /**
     *   �t�@�C���I���_�C�A���O����������
     *   
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.listdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        final ListView  listView = (ListView)  layout.findViewById(R.id.ListDataFileName);
        listView.setAdapter(dataFileHolder);

        // �\������f�[�^�i�_�C�A���O�^�C�g���j����������
        if (title != null)
        {
            builder.setTitle(title);
        }
        builder.setView(layout);

        // �A�C�e����I�������Ƃ��̏���
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
            {
                ListView listView = (ListView) parentView;
                String fileName = (String) listView.getItemAtPosition(position);

                /// ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��
         	   if (resultReceiver != null)
        	   {
        	       resultReceiver.selectedFileName(fileName + fileExtension);
        	   }
         	   if (dialogRef != null)
         	   {
         		   dialogRef.dismiss();
                   dialogRef = null;
         	   }
               System.gc();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(context.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                       dialog.cancel();
                       System.gc();
                   }
               });
        dialogRef = builder.create();
        return (dialogRef);    	
    }

    /**
     *   �t�@�C���_�C�A���O�̃C���^�t�F�[�X
     *   
     * @author MRSa
     *
     */
    public interface IResultReceiver
    {
    	/**
    	 *    �t�@�C�����I�����ꂽ�I
    	 *    
    	 */
        public abstract void selectedFileName(String fileName);
    }	
}
