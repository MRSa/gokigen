package jp.sourceforge.gokigen.mr999ctl;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;
import android.os.Environment;

/**
 *   スクリプトをファイルから読み込んで記憶する人
 * @author MRSa
 *
 */
public class ScriptHolder
{
    private int              currentLine = 0;
    private PreferenceHolder  preference = null;
    private Vector<String>    scriptData = null;
    
    /**
     *  コンストラクタ
     */
    public ScriptHolder(PreferenceHolder arg)
    {
        super();

        preference = arg;
    }

    /**
     *  クラスの準備
     */
    public void prepare()
    {
        //
    }

    /**
     *  スクリプトファイルを読み込む。
     * @param fileName
     * @return 読み込んだ行数 (-1の場合には、読み込みエラー発生)
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
                // データを読み出せるところまで読み出す
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
            // 例外が発生したので、エラー応答する
            return (-1);
        }
        currentLine = 0;
        return (readLine);
    }    

    /**
     *  コメント行(または空行)かどうかを判定する
     * @param scriptLine
     * @return  true:コメント行だった、false: コメント行ではない
     */
    private boolean checkCommentLine(String scriptLine)
    {
        return (false);
    }
    
    /**
     *  命令を解析する
     * @param scriptLine
     * @return
     */
    private int parseCommandOperation(String scriptLine)
    {
        // ラインがゼロならば、終了する
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
     *  値を解析 (数値データ)
     * @param scriptLine
     * @return
     */
    private int parseValue(String scriptLine)
    {
        String data = scriptLine.trim();
        int value = 0;
        try
        {
            // パラメータ名が指定されていたときは、それを読み出してくる
            if (data.startsWith("PRM") == true)
            {
                data = preference.getParameterString(data);
            }

            if (data.toUpperCase().startsWith("0X") == true)
            {
                // 文字列を16進数として処理する
                String hexString = data.substring(2);
                value = Integer.parseInt(hexString, 16);
            }
            else
            {
                // 文字列を10進数として処理する
                value = Integer.parseInt(data);
            }
        }
        catch (Exception ex)
        {
            // 何もしない
            value = 0;
        }        
        return (value);
    }

    /**
     *  次行を読み出す
     * @return 次行のコマンドデータ
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
            // エラー発生
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
            // 不足分は、一つ前のデータで補う 
            value = target;
        }            
        return (new commandInfo(command, target, value));
    }
    
    /**
     *  次に読み出す行を特定する
     * 
     * @param lineNumber 次データを読み出すライン番号
     */
    public void setNextScriptLine(int lineNumber)
    {
        currentLine = (lineNumber < 0) ? 0 : lineNumber - 1;
    }

    /**
     *   応答する命令情報
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
         *  コンストラクタ...
         * 
         */
        private commandInfo(int arg0, int arg1, int arg2)
        {
            operation = arg0;
            target    = arg1;
            value     = arg2;
        }
        
        /**
         *  操作を取得する
         * @return  操作の種別
         */
        public int getOperation()
        {
            return (operation);
        }
        
        /**
         *  操作の対象を取得する
         * @return  操作の対象
         */
        public int getTarget()
        {
            return (target);
        }

        /**
         *  操作の値を取得する
         * @return  操作の値
         */
        public int getValue()
        {
            return (value);
        }
    }
}
