/**
 *   
 *   
 *   
 */
#define MY_CAMERASIZE_VGA    0  // カメラの画像サイズ (640x480)
#define MY_CAMERASIZE_QVGA   1  // カメラの画像サイズ (320x240)
#define MY_CAMERASIZE_HQVGA  2  // カメラの画像サイズ (160x120)

#define MY_LINESTATUS_NONE              0  // 線なし
#define MY_LINESTATUS_HORIZONTAL_LOW    1  // 横線(画面下部)
#define MY_LINESTATUS_HORIZONTAL_HIGH   2  // 横線(画面上部)
#define MY_LINESTATUS_VERTICAL_LEFT     3  // 縦線(画面左部)
#define MY_LINESTATUS_VERTICAL_RIGHT    4  // 縦線(画面右部)
#define MY_LINESTATUS_LEFT_UP           5  // 斜線(左上→右下)
#define MY_LINESTATUS_RIGHT_UP          6  // 斜線(右上→左下)

#define MY_FALSE                        0  // false
#define MY_TRUE                         1  // true


/*
 *    構造体： アプリケーション設定情報
 */
typedef struct {
  int   cameraSize;             // カメラキャプチャサイズ
  int   showMode;               // 画像表示モード
  int   lineSize;               // 検出するラインのサイズ
  int   colorMode;              // 色成分の抽出
  char  dataFileName[512];      // Playするfile name
} MyConfiguration;



/*
 *    構造体： アプリケーション ワークエリア
 */
typedef struct {
  char      *mainWindow;        // メインウィンドウの名称
  char      *backWindow;        // 背景ウィンドウの名称
  char       message[20];       // 表示するメッセージ
  int        fCount;            // ループのカウント状態
  int        previousLineStatus;  // 線分の前回状態
  int        lineStatus;        // 線分の状態
  int        lineCount;         // 連続線分検出回数
  unsigned long previousFrameCount;  // 前回のフレームカウント数
  unsigned long lineFrameCount; // 状態が切り替わったタイミングのフレーム
  unsigned long frameCount;     // フレームカウントを取得する
  CvMemStorage *storage;        // ワーク領域
  CvCapture *capture;           // カメラキャプチャ用の構造体
  IplImage  *frameImage;        // キャプチャ画像用IplImage
  IplImage  *edgeImage;         // エッジのイメージ
  IplImage  *redImage;          // 赤色画像ポインタ
  IplImage  *backImage;         // 背景画像のイメージ
  IplImage  *backScreen;        // 背景スクリーン
} MyWorkArea;

