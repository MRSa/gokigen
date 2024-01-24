package jp.sourceforge.gokigen.clock;

public interface INumberValueProvider 
{
    public final int BUSY_A = 10;  // �r�W�[A�L��
    public final int BUSY_B = 11;  // �r�W�[B�L��
    public final int MINUS  = 12;  // �}�C�i�X�L��
    public final int COLON  = 13;  // �R�����L��
    public final int DOT    = 14;  // �h�b�g�L��
    public final int SPACE  = 15;  // ��

    /**
     *  �f�[�^���X�V����
     */
    public abstract void update();
    
    /** �\�����錅�����擾����
     * 
     * @return  �\�����K�v�Ȍ���
     */
    public abstract int getNumberOfDigits();

    /** �\�����錅�̐������擾���� 
     * 
     * @param index
     * @return
     */
    public abstract int getNumber(int index);

}
