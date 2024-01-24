package jp.sourceforge.gokigen.graphic.test;

import javax.microedition.khronos.opengles.GL10;

/**
 *  �O���t�B�b�N�`��p�̃C���^�t�F�[�X
 * @author MRSa
 *
 */
public interface IGraphicsDrawer
{
	/** �����N���X **/
	public abstract void prepareObject();

	/** �`��O�̏��������s���� **/
	public abstract void preprocessDraw(GL10 gl);

    /** �`������s���� **/
	public abstract void drawObject(GL10 gl);
}
