#pragma once

#include <vector>
#include <parameterData.h>

namespace gokigen {
  static const char INITIALIZE_PARAMETER_FILE[] = "data/initData.txt";
}


class parameterHolder
{
public:
	parameterHolder(void);
	~parameterHolder(void);

public:
	void reset();
	parameterData &get(int id);
	int  size() { return parameters.size(); };

	void backup();

private:
    void initialize();
	void restore();

private:
	std::vector<parameterData *> parameters;

   
};
