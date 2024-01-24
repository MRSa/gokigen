#include "parameterData.h"

/**
**
**
*
*/
parameterData::parameterData(int dataId, std::string &name, int defValue): id(dataId), name(name), defaultValue(defValue) 
{
	reset();
}

/**
**
**
*
*/
parameterData::~parameterData(void)
{
}

/**
**
**
*
*/
void parameterData::reset()
{
	value = defaultValue;
}

void parameterData::updateValue(std::string &dataName, int defValue, int curValue)
{
	name = dataName;
	defaultValue = defValue;
	value = curValue;
}
