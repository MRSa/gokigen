package jp.sourceforge.gokigen.usbiowrapper;

/**
 *  USB-IO�A�N�Z�X�p���b�p�[(libusbio-gokigen.so)��
 *  ���͂Ȃ�������N���X (JNI)
 *  
 * @author MRSa
 *
 */
public class LibUsbGokigenWrapper
{
    static
    {
        /** ���C�u���������[�h���� */ 
    	System.loadLibrary("usbio-gokigen");
    }

    /** �l�C�e�B�u���\�b�h�Q�i���ڌĂׂ邪�A�Ă΂Ȃ��j **/
    public native int  prepareMorphyUsbIo();
    public native void outputMorphyUsbIo(int port, int value); 
    public native int  inputMorphyUsbIo(int port);
    public native void shutdownMorphyUsbIo();

    /**
     *   USB-IO�𗘗p���邽�߂ɏ�������
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
    *   USB-IO�Ƀf�[�^�𑗐M����
    *   
    *   @param port  �|�[�g�ԍ� (0 �܂��� 1)
    *   @param value �o�̓f�[�^�l (0x00�`0xff, ��ʃr�b�g�͖�������)
    *
    */
    public void outputUsbIo(int port, int value)
    {
        outputMorphyUsbIo(port, value); 
    }

   /**
    *   USB-IO����f�[�^�����炤
    *   
    *   @param port �|�[�g�ԍ� (0 �܂��� 1)
    *   @return ���͒l �i0x00�`0xff�j
    *
    */
    public int inputUsbIo(int port)
    {
    	int data = inputMorphyUsbIo(port);
        return ((data & 0x000000ff));
    }

   /**
    *   USB-IO�Ƃ̐ڑ���؂�
    *
    */
    public void shutdownUsbIo()
    {
        shutdownMorphyUsbIo();
    }
}
