package jp.sourceforge.gokigen.diary;

import android.graphics.Canvas;

/**
 *  グラフを実際に描画するクラス(のインタフェース)
 * @author MRSa
 *
 */
public interface IGokigenGraphDrawer
{
	/** 表示の準備(拡大率などの初期化 **/
	public abstract void prepare();
	
	/** (集計と)描画を実施 **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType, GokigenGraphDataHolder dataHolder);

    /** 拡大 **/
    public abstract void actionZoomIn();
    
    /** 縮小 **/
    public abstract void actionZoomOut();
    
    /** 前データ **/
    public abstract boolean actionShowPreviousData();

    /** 後データ **/
    public abstract boolean actionShowNextData();
}
