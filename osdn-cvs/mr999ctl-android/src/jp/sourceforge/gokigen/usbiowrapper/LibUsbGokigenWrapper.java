package jp.sourceforge.gokigen.usbiowrapper;

/**
 *  USB-IOアクセス用ラッパー(libusbio-gokigen.so)と
 *  おはなしをするクラス (JNI)
 *  
 * @author MRSa
 *
 */
public class LibUsbGokigenWrapper
{
    static
    {
        /** ライブラリをロードする */ 
    	System.loadLibrary("usbio-gokigen");
    }

    /** ネイティブメソッド群（直接呼べるが、呼ばない） **/
    public native int  prepareMorphyUsbIo();
    public native void outputMorphyUsbIo(int port, int value); 
    public native int  inputMorphyUsbIo(int port);
    public native void shutdownMorphyUsbIo();

    /**
     *   USB-IOを利用するために準備する
     *
     */
    public boolean prepareUsbIo()
    {
        int ret = prepareMorphyUsbIo();
        if (ret < 0)
        {        
            return (false);
        }
        return (true);
    } 

   /**
    *   USB-IOにデータを送信する
    *   
    *   @param port  ポート番号 (0 または 1)
    *   @param value 出力データ値 (0x00～0xff, 上位ビットは無視する)
    *
    */
    public void outputUsbIo(int port, int value)
    {
        outputMorphyUsbIo(port, value); 
    }

   /**
    *   USB-IOからデータをもらう
    *   
    *   @param port ポート番号 (0 または 1)
    *   @return 入力値 （0x00～0xff）
    *
    */
    public int inputUsbIo(int port)
    {
    	int data = inputMorphyUsbIo(port);
        return ((data & 0x000000ff));
    }

   /**
    *   USB-IOとの接続を切る
    *
    */
    public void shutdownUsbIo()
    {
        shutdownMorphyUsbIo();
    }
}
