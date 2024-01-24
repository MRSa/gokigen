/**
 * ------------------------------------------------------------------------------
 *   myproto.h : �֐��̃v���g�^�C�v�錾
 *
 * ------------------------------------------------------------------------------
 */
void setCameraCaptureSize(CvCapture *capture, int cameraSize);
int saveImageToFile(IplImage *outputImage);
void parseOption(int argc, char **argv, MyConfiguration *config);
void setBackground(char *backImageFileName, MyWorkArea *work);
void updateBackground(MyConfiguration *config, MyWorkArea *work);
void prepare(char *mainWindow, char*backWindow, MyConfiguration *config, MyWorkArea *work);
void finish(MyConfiguration *config, MyWorkArea *work);
int doInitialize(MyConfiguration *config, MyWorkArea *work);
void mainLoop(MyConfiguration *config, MyWorkArea *work);
