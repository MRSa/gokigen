#pragma once

#include <string>

class parameterData
{
public:
	parameterData(int dataId, std::string &name, int defValue = 0);
	~parameterData(void);

public:
	/*  �f�[�^�̃��Z�b�g */
	void reset();

    /*  �l�̍X�V  */
	void increment() { value++; };
	void decrement() { value--; };
	void setValue(int valueToSet) { value = valueToSet; }; 

	/*  �f�[�^�̃��t���b�V�� */
	void updateValue(std::string &dataName, int defValue, int curValue);

    /* �ێ��f�[�^�̉��� */
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
