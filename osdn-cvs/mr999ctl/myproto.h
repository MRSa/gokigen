/**
 * ------------------------------------------------------------------------------
 *   myproto.h : 関数のプロトタイプ宣言
 *
 * ------------------------------------------------------------------------------
 */
void detectLine(MyConfiguration *config, MyWorkArea *work);
void updateBackground(MyConfiguration *config, MyWorkArea *work);
void setCameraCaptureSize(CvCapture *capture, int cameraSize);
void mainLoop(MyConfiguration *config, MyWorkArea *work);
void parseOption(int argc, char **argv, MyConfiguration *config);
void setBackground(char *backImageFileName, MyWorkArea *work);
void prepare(char *mainWindow, char*backWindow, MyConfiguration *config, MyWorkArea *work);
void finish(MyConfiguration *config, MyWorkArea *work);
int doInitialize(MyConfiguration *config, MyWorkArea *work);
int handleKey(int key);
int handleLine(MyConfiguration *config, MyWorkArea *work);
