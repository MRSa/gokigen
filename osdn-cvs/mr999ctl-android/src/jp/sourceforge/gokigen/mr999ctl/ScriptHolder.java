package jp.sourceforge.gokigen.mr999ctl;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;
import android.os.Environment;

/**
 *   �X�N���v�g���t�@�C������ǂݍ���ŋL������l
 * @author MRSa
 *
 */
public class ScriptHolder
{
    private int              currentLine = 0;
    private PreferenceHolder  preference = null;
    private Vector<String>    scriptData = null;
    
    /**
     *  �R���X�g���N�^
     */
    public ScriptHolder(PreferenceHolder arg)
    {
        super();

        preference = arg;
    }

    /**
     *  �N���X�̏���
     */
    public void prepare()
    {
        //
    }

    /**
     *  �X�N���v�g�t�@�C����ǂݍ��ށB
     * @param fileName
     * @return �ǂݍ��񂾍s�� (-1�̏ꍇ�ɂ́A�ǂݍ��݃G���[����)
     */
    public int readScript(String fileName)
    {
        int readLine = 0;
        try
        {
            scriptData = null;
            System.gc();

            scriptData = new Vector<String>();
            
            String scriptFileName = Environment.getExternalStorageDirectory() + "/" + fileName;

            File scriptFile = new File(scriptFileName);
            FileReader scriptReader = new FileReader(scriptFile);
            BufferedReader script = new BufferedReader(scriptReader);
            try
            {
                // �f�[�^��ǂݏo����Ƃ���܂œǂݏo��
                while (true)
                {
                    String data = script.readLine();
                    if (data == null)
                    {
                        break;
                    }
                    scriptData.addElement(data);
                    readLine++;
                }
                script.close();
            }
            catch (Exception ex)
            {
                // end of script
            }            
        }
        catch (Exception ex)
        {
            // ��O�����������̂ŁA�G���[��������
            return (-1);
        }
        currentLine = 0;
        return (readLine);
    }    

    /**
     *  �R�����g�s(�܂��͋�s)���ǂ����𔻒肷��
     * @param scriptLine
     * @return  true:�R�����g�s�������Afalse: �R�����g�s�ł͂Ȃ�
     */
    private boolean checkCommentLine(String scriptLine)
    {
        return (false);
    }
    
    /**
     *  ���߂���͂���
     * @param scriptLine
     * @return
     */
    private int parseCommandOperation(String scriptLine)
    {
        // ���C�����[���Ȃ�΁A�I������
        if (scriptLine.length() <= 0)
        {
            return (commandInfo.OPERATION_END);
        }

        int value = commandInfo.OPERATION_NOP;        
        String data = scriptLine.toUpperCase();
        if (data.startsWith("END") == true)
        {
            value = commandInfo.OPERATION_END;
        }
        else if (data.startsWith("JMP") == true)
        {
            value = commandInfo.OPERATION_JUMP;
        }
        else if (data.startsWith("MOVE") == true)
        {
            value = commandInfo.OPERATION_MOVE;
        }
        else if (data.startsWith("SLEEP") == true)
        {
            value = commandInfo.OPERATION_SLEEP;
        }
        else if (data.startsWith("STOP") == true)
        {
            value = commandInfo.OPERATION_STOP;
        }
        return (value);
    }
    
    /**
     *  �l����� (���l�f�[�^)
     * @param scriptLine
     * @return
     */
    private int parseValue(String scriptLine)
    {
        String data = scriptLine.trim();
        int value = 0;
        try
        {
            // �p�����[�^�����w�肳��Ă����Ƃ��́A�����ǂݏo���Ă���
            if (data.startsWith("PRM") == true)
            {
                data = preference.getParameterString(data);
            }

            if (data.toUpperCase().startsWith("0X") == true)
            {
                // �������16�i���Ƃ��ď�������
                String hexString = data.substring(2);
                value = Integer.parseInt(hexString, 16);
            }
            else
            {
                // �������10�i���Ƃ��ď�������
                value = Integer.parseInt(data);
            }
        }
        catch (Exception ex)
        {
            // �������Ȃ�
            value = 0;
        }        
        return (value);
    }

    /**
     *  ���s��ǂݏo��
     * @return ���s�̃R�}���h�f�[�^
     */
    public commandInfo readNext()
    {
        
        String scriptLine = null;
        try
        {
            boolean isLoop = true;
            while (isLoop == true)
            {
                scriptLine = scriptData.elementAt(currentLine);
                currentLine++;
                isLoop = checkCommentLine(scriptLine);
            }
        }
        catch (Exception ex)
        {
            // �G���[����
            scriptLine = "";
        }


        String  trimmedLine = scriptLine.trim();        
        String[] dataList = trimmedLine.split("[, ]");
        
        int command = commandInfo.OPERATION_NOP;
        int target  = 0;
        int value   = 0;
        
        try
        {
            command = parseCommandOperation(dataList[0]);
            target  = parseValue(dataList[1]);
            value   = parseValue(dataList[2]);
        }
        catch (Exception ex)
        {
            // �s�����́A��O�̃f�[�^�ŕ₤ 
            value = target;
        }            
        return (new commandInfo(command, target, value));
    }
    
    /**
     *  ���ɓǂݏo���s����肷��
     * 
     * @param lineNumber ���f�[�^��ǂݏo�����C���ԍ�
     */
    public void setNextScriptLine(int lineNumber)
    {
        currentLine = (lineNumber < 0) ? 0 : lineNumber - 1;
    }

    /**
     *   �������閽�ߏ��
     *     
     * @author MRSa
     *
     */
    public class commandInfo
    {
        public static final int OPERATION_END        = -1;
        public static final int OPERATION_NOP        = 0;
        public static final int OPERATION_SLEEP      = 1;
//        public static final int OPERATION_CHANGEUNIT = 50;
        public static final int OPERATION_JUMP       = 100;
        public static final int OPERATION_STOP       = 200;
        public static final int OPERATION_MOVE       = 300;

        private int operation = 0;
        private int target    = 0;
        private int value     = 0;
        
        /**
         *  �R���X�g���N�^...
         * 
         */
        private commandInfo(int arg0, int arg1, int arg2)
        {
            operation = arg0;
            target    = arg1;
            value     = arg2;
        }
        
        /**
         *  ������擾����
         * @return  ����̎��
         */
        public int getOperation()
        {
            return (operation);
        }
        
        /**
         *  ����̑Ώۂ��擾����
         * @return  ����̑Ώ�
         */
        public int getTarget()
        {
            return (target);
        }

        /**
         *  ����̒l���擾����
         * @return  ����̒l
         */
        public int getValue()
        {
            return (value);
        }
    }
}
