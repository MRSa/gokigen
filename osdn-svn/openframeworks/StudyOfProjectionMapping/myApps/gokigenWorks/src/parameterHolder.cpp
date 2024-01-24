#include "parameterHolder.h"
#include <fstream>

#include <ofUtils.h>

/**
 *
 *
 */
parameterHolder::parameterHolder(void)
{
	initialize();

	restore();
}

/**
 *  デストラクタ...保持データをぜ～んぶクリアする
 *
 */
parameterHolder::~parameterHolder(void)
{
    // parameterDataの開放処理 (データ中身の全消し)
	int max = parameters.size();
	for (int index = 0; index < max; index++)
	{
		delete (parameters.at(index));
	}
/*
    std::vector<parameterData *>::iterator iter = parameters.begin();
	while (iter != parameters.end());
	{
		delete (*iter);
		++iter;
	}
*/
	parameters.clear();
}

/**
 *   保持するパラメータ値の初期化処理...
**/
void parameterHolder::initialize()
{
	parameters.clear();
	parameters.push_back(new parameterData(0, std::string("parameter2Y"),130));
	parameters.push_back(new parameterData(1, std::string("marginCenterY"),180));
	parameters.push_back(new parameterData(2, std::string("patameter5"),25));
	parameters.push_back(new parameterData(3, std::string("parameter3"),85));
	parameters.push_back(new parameterData(4, std::string("parameter6"),-95));
	parameters.push_back(new parameterData(5, std::string("parameter33"),145));
	parameters.push_back(new parameterData(6, std::string("parameter66"),-135));
	parameters.push_back(new parameterData(7, std::string("parameter4"),-118));
	parameters.push_back(new parameterData(8, std::string("line7Y"),12));
	parameters.push_back(new parameterData(9, std::string("line4Y"),-21));

}

void parameterHolder::reset()
{
    // 初期値にデータを戻す
    std::vector<parameterData *>::iterator iter = parameters.begin();
	while (iter != parameters.end())
	{
		(*iter)->reset();
		++iter;
	}
}

parameterData &parameterHolder::get(int id)
{
	if ((id >= static_cast<int>(parameters.size()))||(id < 0))
	{
		// データが異常の場合は、先頭データを返す
		return (*parameters.at(0));
	}
	return (*parameters.at(id));
}


/**
*   パラメータデータのバックアップ
*
*/
void parameterHolder::backup()
{
    std::ofstream ofs(gokigen::INITIALIZE_PARAMETER_FILE);
    std::vector<parameterData *>::iterator iter = parameters.begin();
	while (iter != parameters.end())
	{
		ofs << (*iter)->getId() << "," << (*iter)->getValue() << "," << (*iter)->getDefault()<< "," << (*iter)->getName() << std::endl;
        iter++;
	}
}

void parameterHolder::restore()
{
	std::ifstream ifs(gokigen::INITIALIZE_PARAMETER_FILE);
	std::string buffer;

	if (!ifs)
	{
		return;
	}
    while (getline(ifs, buffer))
	{
        std::vector<std::string> splitItems = ofSplitString(buffer, ",");

		int id = ofToInt(splitItems[0]);
		int value = ofToInt(splitItems[1]);
		int defValue = ofToInt(splitItems[2]);
		string name = splitItems[3];
		
		if ((id < parameters.size())&&(id >= 0))
		{
			parameters.at(id)->updateValue(name, defValue, value);
		}
    }
}
