#pragma once

#include <vector>

class positionHolder
{
public:
	positionHolder(int positionSize = 7);
	~positionHolder(void);

public:
    void set(int id, double x, double y);
	double getX(int id);
	double getY(int id);
	int getSize() { return (size); };

private:
    void initialize(int max);

private:
	int  size;
	std::vector<double> axisX;
	std::vector<double> axisY;

};
