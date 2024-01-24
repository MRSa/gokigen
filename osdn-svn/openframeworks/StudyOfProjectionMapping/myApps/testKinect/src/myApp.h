#pragma once

#include "ofxKinectNui.h"
//#include "ofxKinectNuiPlayer.h"
//#include "ofxKinectNuiRecorder.h"
#include "ofMain.h"
#include "skeletonHolder.h"

class ofxKinectNuiDrawTexture;
class ofxKinectNuiDrawSkeleton;

// uncomment this to read from two kinects simultaneously
//#define USE_TWO_KINECTS


//--------------------------------------------------------------
class myApp : public ofBaseApp
{

	public:
		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);
		void exit();

    public:
		void kinectPlugged();
		void kinectUnplugged();

		void kinectPlugged2();
		void kinectUnplugged2();

    public:
	    void drawSkeleton(int x, int y, int width, int height);

    private:
	    bool initializeKinectSensor();
	    bool initializeKinectSensor2();
		void initializeSelf();
        void drawSkeleton(ofPoint* src, float scaleX, float scaleY);

    private:
		ofxKinectNui *kinect;
		bool bPlugged;
		//ofxBase3DVideo* kinectSource;
		ofxKinectNuiDrawSkeleton*	skeletonDraw_;
		ofxKinectNuiDrawTexture*	videoDraw_;

		ofxKinectNui *kinect2;
		bool bPlugged2;
		//ofxBase3DVideo* kinectSource2;
		ofxKinectNuiDrawSkeleton*	skeletonDraw2_;
		ofxKinectNuiDrawTexture*	videoDraw2_;

		skeletonHolder *skeleton;
};
