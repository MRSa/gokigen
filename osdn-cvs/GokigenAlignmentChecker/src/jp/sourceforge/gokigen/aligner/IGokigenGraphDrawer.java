package jp.sourceforge.gokigen.aligner;

import android.graphics.Canvas;

/**
 *  グラフを実際に描画するクラス(のインタフェース)
 * @author MRSa
 *
 */
public interface IGokigenGraphDrawer
{
	/** 表示の準備(初期化) **/
	public abstract void prepare();
	
	/** 描画実施 **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType);

    /** 拡大 **/
    public abstract void actionZoomIn();
    
    /** 縮小 **/
    public abstract void actionZoomOut();
    
    /** 前データ **/
    public abstract boolean actionShowPreviousData();

    /** 後データ **/
    public abstract boolean actionShowNextData();

    /** 表示するメッセージを設定する **/
    public abstract void setMessage(String message);

    /** 操作をリセットする **/
    public abstract void reset();
    
    /** ひとつ操作を戻す **/
    public abstract void undo();
    
    /** 描画タイプを設定する **/
    public abstract void setDrawType(int type);
}
