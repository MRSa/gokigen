package jp.sourceforge.gokigen.aligner;

import javax.microedition.khronos.opengles.GL10;

/**
 *  グラフィック描画用のインタフェース
 * @author MRSa
 *
 */
public interface IGraphicsDrawer
{
    /** 準備クラス **/
    public abstract void prepareObject();

    /** 準備クラス(その２) **/
    public abstract void prepareDrawer(GL10 gl);
    
    /** 描画前の処理を実行する **/
    public abstract void preprocessDraw(GL10 gl);

    /** 描画を実行する **/
    public abstract void drawObject(GL10 gl);
}
