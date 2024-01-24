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
 *  �f�X�g���N�^...�ێ��f�[�^�����`��ԃN���A����
 *
 */
positionHolder::~positionHolder(void)
{
	axisX.clear();
	axisY.clear();
}

/**
 *   �ێ�����ʒu�f�[�^�̏���������...
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
