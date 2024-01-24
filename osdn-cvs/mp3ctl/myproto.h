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

int parseFileList(char *targetFileName, char ***fileList);
void releaseFileList(int fileNameCount, char ***fileList);

extern int start_play_music(char *fileToPlay);
extern void stop_playing_music(void);

extern int  mpg123_debugMode;
extern int  numberOfPlayingFiles;
extern int  currentPlayingFile;
extern int  currentPlayingStatus;
extern char **playingFileList;
