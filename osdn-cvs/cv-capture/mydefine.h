/**
 * ------------------------------------------------------------------------------
 *   mydefine.h :  ���[�J���̒�`�Q
 *
 * ------------------------------------------------------------------------------
 */
#define MY_SHOWMODE_NORMAL  0   // �ʏ�摜
#define MY_SHOWMODE_GRAY    1   // �O���[�X�P�[��
#define MY_SHOWMODE_EDGE    2   // �G�b�W���o
#define MY_SHOWMODE_BINARY  3   // 2�l��
#define MY_SHOWMODE_VISION  4   // ���H�\��

#define MY_CAMERASIZE_VGA    0  // �J�����̉摜�T�C�Y (640x480)
#define MY_CAMERASIZE_QVGA   1  // �J�����̉摜�T�C�Y (320x240)
#define MY_CAMERASIZE_HQVGA  2  // �J�����̉摜�T�C�Y (160x120)

#define MY_FALSE             0
#define MY_TRUE              1

/*
 *    �\���́F �A�v���P�[�V�����ݒ���
 */
typedef struct {
  int   cameraSize;             // �J�����L���v�`���T�C�Y
  int   showMode;               // �摜�\�����[�h
} MyConfiguration;

/*
 *    �\���́F �A�v���P�[�V���� ���[�N�G���A
 */
typedef struct {
  char      *mainWindow;        // ���C���E�B���h�E�̖���
  char      *backWindow;        // �w�i�E�B���h�E�̖���

  int        binLevel;          // �Q�l���x��
  int        fCount;            // �J�E���g��
  int        doCapture;         // �L���v�`�����{����/���Ȃ�
  CvCapture *capture;           // �J�����L���v�`���p�̍\����
  IplImage  *frameImage;        // �L���v�`���摜�pIplImage
  IplImage  *grayImage;         // �O���[�X�P�[���̃C���[�W
  IplImage  *outputImage;       // �o�͉摜�|�C���^
  IplImage  *screenImage;       // �o�͉摜�|�C���^
  IplImage  *backImage;         // �w�i�摜�̃C���[�W
  IplImage  *backScreen;        // �w�i�X�N���[��
} MyWorkArea;

