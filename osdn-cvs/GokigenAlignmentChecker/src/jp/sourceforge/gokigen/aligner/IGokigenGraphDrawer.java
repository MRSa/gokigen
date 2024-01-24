package jp.sourceforge.gokigen.aligner;

import android.graphics.Canvas;

/**
 *  �O���t�����ۂɕ`�悷��N���X(�̃C���^�t�F�[�X)
 * @author MRSa
 *
 */
public interface IGokigenGraphDrawer
{
	/** �\���̏���(������) **/
	public abstract void prepare();
	
	/** �`����{ **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType);

    /** �g�� **/
    public abstract void actionZoomIn();
    
    /** �k�� **/
    public abstract void actionZoomOut();
    
    /** �O�f�[�^ **/
    public abstract boolean actionShowPreviousData();

    /** ��f�[�^ **/
    public abstract boolean actionShowNextData();

    /** �\�����郁�b�Z�[�W��ݒ肷�� **/
    public abstract void setMessage(String message);

    /** ��������Z�b�g���� **/
    public abstract void reset();
    
    /** �ЂƂ����߂� **/
    public abstract void undo();
    
    /** �`��^�C�v��ݒ肷�� **/
    public abstract void setDrawType(int type);
}
