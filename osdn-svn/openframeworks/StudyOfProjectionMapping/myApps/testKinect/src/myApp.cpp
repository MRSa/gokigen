/******************************************************************************/
/**
 *
 *
 *
 *
 */
/******************************************************************************/
#include "myApp.h"
#include "ofxKinectNuiDraw.h"

//--------------------------------------------------------------
void myApp::setup()
{
	ofSetLogLevel(OF_LOG_VERBOSE);

	skeleton = new skeletonHolder();
	kinect = new ofxKinectNui();
	kinect2 = new ofxKinectNui();

	initializeSelf();


	ofSetVerticalSync(true);

	ofSetFrameRate(60);

}

//--------------------------------------------------------------
void myApp::update()
{
	kinect->update();

#ifdef USE_TWO_KINECTS
    kinect2->update();
#endif // #ifdef USE_TWO_KINECTS

}

//--------------------------------------------------------------
void myApp::draw()
{
	ofBackground(100, 100, 100);

	kinect->drawVideo(0,0,320,240);
	drawSkeleton(0, 0, 320, 240);

#ifdef USE_TWO_KINECTS
	kinect2.drawVideo(0,245,320, 240);
	kinect2.drawSkeleton(0, 245, 320, 240);	// draw skeleton images on video images
#endif // #ifdef USE_TWO_KINECTS
}


//--------------------------------------------------------------
void myApp::drawSkeleton(int x, int y, int width, int height)
{
	//kinect->drawSkeleton(x, y, width, height);	// draw skeleton images on video images

	// source size is VGA (640 x 480)
	float scaleX = width / 640.0f;
	float scaleY = height / 480.0f;

	int nofSkeletons = 0;
	nofSkeletons = skeleton->update(kinect);
	if ((bPlugged == true)&&(nofSkeletons != 0))
	{
        drawSkeleton(skeleton->getCurrentSkeltonPoints(), scaleX, scaleY);
	}
	else
	{
		nofSkeletons = 0;
	}

	stringstream reportStream = stringstream();
	reportStream << "fps: " << ofGetFrameRate() << endl
				 << "skeletons : " << nofSkeletons << endl
				 << endl;
/****
	for (int index = 0; index < nofSkeletons; index++)
	{
		ofPoint *src = skeleton&skeletonPoints[index * NUI_SKELETON_POSITION_COUNT];
		reportStream << " Head : " << "(" << src[NUI_SKELETON_POSITION_HEAD].x << "," << src[NUI_SKELETON_POSITION_HEAD].y << ")" << endl;
 	    reportStream << " Shoulder: "<< "(" << src[NUI_SKELETON_POSITION_SHOULDER_CENTER].x << "," << src[NUI_SKELETON_POSITION_SHOULDER_CENTER].y << ")" << endl;
 	    reportStream << " Spine: "<< "(" << src[NUI_SKELETON_POSITION_SPINE].x << "," << src[NUI_SKELETON_POSITION_SPINE].y << ")" << endl;
 	    reportStream << " Hip: "<< "(" << src[NUI_SKELETON_POSITION_HIP_CENTER].x << "," << src[NUI_SKELETON_POSITION_HIP_CENTER].y << ")" << endl;
	}
****/
	ofSetColor(255, 255, 255);
	ofDrawBitmapString(reportStream.str(), 5, 260);
}




void  myApp::drawSkeleton(ofPoint* src, float scaleX, float scaleY)
{
	ofColor lineColor;
	ofPolyline pLine;
	ofPushStyle();

	lineColor.set(255, 255, 0);
	ofSetColor(lineColor);
	ofNoFill();
	ofSetLineWidth(4);
	// HEAD
	pLine.clear();
	pLine.addVertex(src[NUI_SKELETON_POSITION_HIP_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_HIP_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_SPINE].x * scaleX, src[NUI_SKELETON_POSITION_SPINE].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_SHOULDER_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_SHOULDER_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_HEAD].x * scaleX, src[NUI_SKELETON_POSITION_HEAD].y * scaleY);
	pLine.draw();
	
	// BODY_LEFT
	pLine.clear();
	pLine.addVertex(src[NUI_SKELETON_POSITION_SHOULDER_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_SHOULDER_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_SHOULDER_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_SHOULDER_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_ELBOW_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_ELBOW_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_WRIST_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_WRIST_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_HAND_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_HAND_LEFT].y * scaleY);
	pLine.draw();

	// BODY_RIGHT
	pLine.clear();
	pLine.addVertex(src[NUI_SKELETON_POSITION_SHOULDER_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_SHOULDER_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_SHOULDER_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_SHOULDER_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_ELBOW_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_ELBOW_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_WRIST_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_WRIST_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_HAND_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_HAND_RIGHT].y * scaleY);
	pLine.draw();

	// LEG_LEFT
	pLine.clear();
	pLine.addVertex(src[NUI_SKELETON_POSITION_HIP_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_HIP_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_HIP_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_HIP_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_KNEE_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_KNEE_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_ANKLE_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_ANKLE_LEFT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_FOOT_LEFT].x * scaleX, src[NUI_SKELETON_POSITION_FOOT_LEFT].y * scaleY);
	pLine.draw();

	// LEG_RIGHT
	pLine.clear();
	pLine.addVertex(src[NUI_SKELETON_POSITION_HIP_CENTER].x * scaleX, src[NUI_SKELETON_POSITION_HIP_CENTER].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_HIP_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_HIP_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_KNEE_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_KNEE_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_ANKLE_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_ANKLE_RIGHT].y * scaleY);
	pLine.addVertex(src[NUI_SKELETON_POSITION_FOOT_RIGHT].x * scaleX, src[NUI_SKELETON_POSITION_FOOT_RIGHT].y * scaleY);
	pLine.draw();

	//ofSetColor(pointColor_);
	ofSetLineWidth(0);
	ofFill();
	for(int i = 0; i < NUI_SKELETON_POSITION_COUNT; ++i)
	{
		ofCircle(src[i].x * scaleX, src[i].y * scaleY, 5);
	}
	ofPopStyle();
}


//--------------------------------------------------------------
void myApp::keyPressed(int key)
{

}

//--------------------------------------------------------------
void myApp::keyReleased(int key)
{

}

//--------------------------------------------------------------
void myApp::mouseMoved(int x, int y )
{

}

//--------------------------------------------------------------
void myApp::mouseDragged(int x, int y, int button)
{

}

//--------------------------------------------------------------
void myApp::mousePressed(int x, int y, int button)
{

}

//--------------------------------------------------------------
void myApp::mouseReleased(int x, int y, int button)
{

}

//--------------------------------------------------------------
void myApp::windowResized(int w, int h)
{

}

//--------------------------------------------------------------
void myApp::gotMessage(ofMessage msg)
{

}

//--------------------------------------------------------------
void myApp::dragEvent(ofDragInfo dragInfo)
{ 

}

//--------------------------------------------------------------
void myApp::exit() 
{
	videoDraw_->destroy();
	videoDraw_ = NULL;
	delete skeletonDraw_;
	skeletonDraw_ = NULL;

	kinect->setAngle(0);
	kinect->close();
	kinect->removeKinectListener(this);

#ifdef USE_TWO_KINECTS
	videoDraw2_->destroy();
	videoDraw2_ = NULL;
	delete skeletonDraw2_;
	skeletonDraw2_ = NULL;

	kinect2->setAngle(0);
	kinect2->close();
	kinect2->removeKinectListener(this);
#endif // #ifdef USE_TWO_KINECTS

    delete skeleton;
	delete kinect;
	delete kinect2;
}

//--------------------------------------------------------------
void myApp::initializeSelf()
{

	initializeKinectSensor();
#ifdef USE_TWO_KINECTS
	initializeKinectSensor2();
#endif // #ifdef USE_TWO_KINECTS

}


//--------------------------------------------------------------
bool myApp::initializeKinectSensor()
{
	bool ret = false;

	ofxKinectNui::InitSetting initSetting;
	initSetting.grabVideo = true;
	initSetting.grabDepth = false;
	initSetting.grabAudio = false;
	initSetting.grabLabel = false;
	initSetting.grabSkeleton = true;
	initSetting.grabCalibratedVideo = false;
	initSetting.grabLabelCv = false;
	initSetting.videoResolution = NUI_IMAGE_RESOLUTION_640x480;
	initSetting.depthResolution = NUI_IMAGE_RESOLUTION_640x480;
	kinect->init(initSetting);
//	kinect->setMirror(false); // if you want to get NOT mirror mode, uncomment here
//	kinect->setNearmode(true); // if you want to set nearmode, uncomment here
	kinect->open();

	kinect->addKinectListener(this, &myApp::kinectPlugged, &myApp::kinectUnplugged);

	bPlugged = kinect->isConnected();
	//kinectSource = &kinect;

	videoDraw_ = ofxKinectNuiDrawTexture::createTextureForVideo(kinect->getVideoResolution());

	skeletonDraw_ = new ofxKinectNuiDrawSkeleton();
	kinect->setSkeletonDrawer(skeletonDraw_);
	kinect->setVideoDrawer(videoDraw_);

	return (ret);
}

//--------------------------------------------------------------
bool myApp::initializeKinectSensor2()
{
	bool ret = false;

	ofxKinectNui::InitSetting initSetting;
	initSetting.grabVideo = true;
	initSetting.grabDepth = true;
	initSetting.grabAudio = true;
	initSetting.grabLabel = true;
	initSetting.grabSkeleton = true;
	initSetting.grabCalibratedVideo = true;
	initSetting.grabLabelCv = true;
	//initSetting.videoResolution = NUI_IMAGE_RESOLUTION_640x480;
	//initSetting.depthResolution = NUI_IMAGE_RESOLUTION_640x480;
	initSetting.videoResolution = NUI_IMAGE_RESOLUTION_320x240;
	initSetting.depthResolution = NUI_IMAGE_RESOLUTION_320x240;
	kinect2->init(initSetting);
//	kinect2->setMirror(false); // if you want to get NOT mirror mode, uncomment here
//	kinect2->setNearmode(true); // if you want to set nearmode, uncomment here
	kinect2->open();

	kinect2->addKinectListener(this, &myApp::kinectPlugged2, &myApp::kinectUnplugged2);
	//kinectSource2 = &kinect2;

	bPlugged2 = kinect2->isConnected();

	videoDraw2_ = ofxKinectNuiDrawTexture::createTextureForVideo(kinect2->getVideoResolution());

	skeletonDraw2_ = new ofxKinectNuiDrawSkeleton();
	kinect2->setVideoDrawer(videoDraw2_);
	kinect2->setSkeletonDrawer(skeletonDraw2_);

	return (ret);
}


//--------------------------------------------------------------
void myApp::kinectPlugged()
{
	bPlugged = true;
}

//--------------------------------------------------------------
void myApp::kinectUnplugged()
{
	bPlugged = false;
}

//--------------------------------------------------------------
void myApp::kinectPlugged2()
{
	bPlugged2 = true;
}

//--------------------------------------------------------------
void myApp::kinectUnplugged2()
{
	bPlugged2 = false;
}
