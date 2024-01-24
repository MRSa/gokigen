#pragma once

#include <string>

class parameterData
{
public:
	parameterData(int dataId, std::string &name, int defValue = 0);
	~parameterData(void);

public:
	/*  データのリセット */
	void reset();

    /*  値の更新  */
	void increment() { value++; };
	void decrement() { value--; };
	void setValue(int valueToSet) { value = valueToSet; }; 

	/*  データのリフレッシュ */
	void updateValue(std::string &dataName, int defValue, int curValue);

    /* 保持データの応答 */
	const std::string &getName() const { return (name); };
	const int getValue() const { return (value); };
	const int getId() const { return (id); };
	const int getDefault() const { return (defaultValue); };

private:
	int           id;
	std::string   name;
	int           defaultValue;
	int           value;
};
