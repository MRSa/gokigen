#include "positionHolder.h"

/**
 *
 *
 */
positionHolder::positionHolder(int positionHolder)
{
	initialize(positionHolder);
}

/**
 *  デストラクタ...保持データをぜ～んぶクリアする
 *
 */
positionHolder::~positionHolder(void)
{
	axisX.clear();
	axisY.clear();
}

/**
 *   保持する位置データの初期化処理...
**/
void positionHolder::initialize(int max)
{
	size = max;
	axisX.clear();
	axisY.clear();
	for (int index = 0; index < max; index++)
	{
        axisX.push_back((double) 0.0);
        axisY.push_back((double) 0.0);
	}
}

/**
 *
 */
void positionHolder::set(int id, double x, double y)
{
	if ((id >=0)&&(id < size))
	{
		axisX.at(id) = x;
		axisY.at(id) = y;
	}
}


/**
 *
 */
double positionHolder::getX(int id)
{
	if ((id >=0)&&(id < size))
	{
		return (axisX.at(id));
	}
	return (0.0);
}

/**
 *
 */
double positionHolder::getY(int id)
{
	if ((id >=0)&&(id < size))
	{
		return (axisY.at(id));
	}
	return (0.0);
}
