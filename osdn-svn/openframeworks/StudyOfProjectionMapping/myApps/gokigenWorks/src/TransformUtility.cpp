#include "TransformUtility.h"

//------------------------------------------
TransformUtility::TransformUtility(void)
{
}

//------------------------------------------
TransformUtility::~TransformUtility(void)
{
}

//------------------------------------------


/**
 * ビットマップの三角形を描く。 
 * <br/>0 - 1
 * <br/>| /  
 * <br/>2 
 * @param g グラフィックス
 * @param bitmapData ビットマップデータ
 * @param a0 転送元(ビットマップデータ)の座標
 * @param a1 転送元(ビットマップデータ)の座標
         * @param a2 転送元(ビットマップデータ)の座標
         * @param b0 転送先(グラフィック)の座標
         * @param b1 転送先(グラフィック)の座標
         * @param b2 転送先(グラフィック)の座標
         */
/*
public static function drawBitmapTriangle(
            g : Graphics, bitmapData : BitmapData,
            a0 : Point, a1 : Point, a2 : Point, 
            b0 : Point, b1 : Point, b2 : Point
        ) : void {
            var matrix : Matrix = createMatrix(a0, a1, a2, b0, b1, b2);
            g.beginBitmapFill(bitmapData, matrix);
            drawTriangle(g, a0, a1, a2, matrix);
            g.endFill();
        }
*/