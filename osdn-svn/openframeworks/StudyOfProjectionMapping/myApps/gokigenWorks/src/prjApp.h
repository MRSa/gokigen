#pragma once

#include "ofMain.h"
#include <parameterHolder.h>
#include <positionHolder.h>

class prjApp : public ofBaseApp
{
	public:
		void setup();
		void update();
		void draw();
		
		void keyPressed(int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y);
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);

    private:
	    void resetPosition();  // position reset

		void drawCalibrationMode();  // calibration mode
		void drawShape1();
		void drawShape2();
		void drawShape3();
		void drawShape4();
		void drawShape5();
		void drawShape6();
		void drawShape7();
		void drawShape8();
		void drawBlank();


        void drawShape2_1();
        void drawShape2_2();
        void drawShape2_3();

		void mapImage(ofColor color, int pos1, int pos2, int pos3, int pos4);
		void mapVideo(ofColor color, int pos1, int pos2, int pos3, int pos4);
		void drawShape(ofColor color, int pos1, int pos2, int pos3, int pos4);
        void drawLine(ofColor color, float x1, float y1, float x2, float y2, float startPercentage, float endParcentage);

        void drawImageMapping(unsigned char *image, int imageWidth, int imageHeight, int pos1, int pos2, int pos3, int pos4);

		void margeImageHorizontal(unsigned char *marged, unsigned char *left, unsigned char *right, int width, int height, float position);
        void margeImageVertical(unsigned char *marged, unsigned char *upper, unsigned char *lower, int width, int height, float position);
		void keyPressedCalibrationMode(int key);

    private:
		int displayMode;
		unsigned long counter;
		unsigned long slowCounter;

		std::string messageToShow;
		parameterHolder parameteHolder_;
		positionHolder  positionHolder_;

		ofImage  picture;
		ofVideoPlayer video;
        ofTexture texture;
};
