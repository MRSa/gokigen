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
 * �r�b�g�}�b�v�̎O�p�`��`���B 
 * <br/>0 - 1
 * <br/>| /  
 * <br/>2 
 * @param g �O���t�B�b�N�X
 * @param bitmapData �r�b�g�}�b�v�f�[�^
 * @param a0 �]����(�r�b�g�}�b�v�f�[�^)�̍��W
 * @param a1 �]����(�r�b�g�}�b�v�f�[�^)�̍��W
         * @param a2 �]����(�r�b�g�}�b�v�f�[�^)�̍��W
         * @param b0 �]����(�O���t�B�b�N)�̍��W
         * @param b1 �]����(�O���t�B�b�N)�̍��W
         * @param b2 �]����(�O���t�B�b�N)�̍��W
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