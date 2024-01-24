package jp.sourceforge.gokigen.aligner;

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

    /** �����N���X(���̂Q) **/
    public abstract void prepareDrawer(GL10 gl);
    
    /** �`��O�̏��������s���� **/
    public abstract void preprocessDraw(GL10 gl);

    /** �`������s���� **/
    public abstract void drawObject(GL10 gl);
}
