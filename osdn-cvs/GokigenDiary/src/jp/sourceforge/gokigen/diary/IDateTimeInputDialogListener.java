package jp.sourceforge.gokigen.diary;

/**
 *  �������̓_�C�A���O�̃C���^�t�F�[�X
 * 
 * @author MRSa
 *
 */
public interface IDateTimeInputDialogListener
{
    /** ���������͂��ꂽ�I **/
	public abstract void inputDateTimeEntered(boolean useCurrent, int year, int month, int day, int hour, int minite);
	
	/** �������͂��L�����Z�������I **/
    public abstract void inputDateTimeCanceled();
}
