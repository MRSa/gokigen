package jp.sourceforge.gokigen.diary;

import android.graphics.Canvas;

/**
 *  �O���t�����ۂɕ`�悷��N���X(�̃C���^�t�F�[�X)
 * @author MRSa
 *
 */
public interface IGokigenGraphDrawer
{
	/** �\���̏���(�g�嗦�Ȃǂ̏����� **/
	public abstract void prepare();
	
	/** (�W�v��)�`������{ **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType, GokigenGraphDataHolder dataHolder);

    /** �g�� **/
    public abstract void actionZoomIn();
    
    /** �k�� **/
    public abstract void actionZoomOut();
    
    /** �O�f�[�^ **/
    public abstract boolean actionShowPreviousData();

    /** ��f�[�^ **/
    public abstract boolean actionShowNextData();
}
