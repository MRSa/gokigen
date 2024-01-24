#include "prjApp.h"
#include "ofAppGlutWindow.h"

//--------------------------------------------------------------
int main()
{
    ofAppGlutWindow window; // create a window

	ofSetLogLevel(OF_LOG_VERBOSE);
	ofLogToFile("DEBUGLOG.TXT");

	// set width, height, mode (OF_WINDOW or OF_FULLSCREEN)
    //ofSetupOpenGL(&window, 1024, 768, OF_WINDOW);
	ofSetupOpenGL(&window, 800, 600, OF_FULLSCREEN);
	ofRunApp(new prjApp()); // start the app
}
