/**
 * ------------------------------------------------------------------------------
 *   mydefine.h :  ローカルの定義群
 *
 * ------------------------------------------------------------------------------
 */
#define MY_SHOWMODE_NORMAL  0   // 通常画像
#define MY_SHOWMODE_GRAY    1   // グレースケール
#define MY_SHOWMODE_EDGE    2   // エッジ抽出
#define MY_SHOWMODE_BINARY  3   // 2値化
#define MY_SHOWMODE_VISION  4   // 加工表示

#define MY_CAMERASIZE_VGA    0  // カメラの画像サイズ (640x480)
#define MY_CAMERASIZE_QVGA   1  // カメラの画像サイズ (320x240)
#define MY_CAMERASIZE_HQVGA  2  // カメラの画像サイズ (160x120)

#define MY_FALSE             0
#define MY_TRUE              1

/*
 *    構造体： アプリケーション設定情報
 */
typedef struct {
  int   cameraSize;             // カメラキャプチャサイズ
  int   showMode;               // 画像表示モード
} MyConfiguration;

/*
 *    構造体： アプリケーション ワークエリア
 */
typedef struct {
  char      *mainWindow;        // メインウィンドウの名称
  char      *backWindow;        // 背景ウィンドウの名称

  int        binLevel;          // ２値レベル
  int        fCount;            // カウント数
  int        doCapture;         // キャプチャ実施する/しない
  CvCapture *capture;           // カメラキャプチャ用の構造体
  IplImage  *frameImage;        // キャプチャ画像用IplImage
  IplImage  *grayImage;         // グレースケールのイメージ
  IplImage  *outputImage;       // 出力画像ポインタ
  IplImage  *screenImage;       // 出力画像ポインタ
  IplImage  *backImage;         // 背景画像のイメージ
  IplImage  *backScreen;        // 背景スクリーン
} MyWorkArea;

