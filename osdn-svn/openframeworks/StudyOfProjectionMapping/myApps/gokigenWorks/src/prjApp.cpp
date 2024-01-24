#include "prjApp.h"

//--------------------------------------------------------------
void prjApp::setup()
{
	displayMode = 0;   // 暫定仕様
	counter = 0;
	slowCounter = 0;

	// 背景色を設定する
	ofBackground(0,0,0);

    //画面の混色の設定を加算合成にする
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE);

	//resetPosition();

	picture.loadImage("sampleImage.jpg");

	//video.loadMovie("demoimage.wmv");
    video.loadMovie("M4V94782.MOV");
	video.play();
    texture.allocate(video.getWidth(), video.getHeight(),GL_RGB);
}

//--------------------------------------------------------------
void prjApp::resetPosition()
{
    // 初期値を設定する
	parameteHolder_.reset();
}

//--------------------------------------------------------------
void prjApp::update()
{
    //ムービー再生を待機状態に
    video.idleMovie();
}

//--------------------------------------------------------------
void prjApp::draw()
{
    // 線分用データを拾い出す
	if (parameteHolder_.size() < 9)
	{
		// まだ初期化済んでないから...何もせずに終了する
		return;
	}
    counter++;
	if ((counter % 16) == 0)
	{
		slowCounter++;
	}

	/**  画像の表示 **/

	if (displayMode == 0)
	{
		// ポジションキャリブレーションモード
		drawCalibrationMode();
		return;
	}
	if (displayMode == 1)
	{
        drawShape1();
		return;
    }
	if (displayMode == 2)
	{
        drawShape2();
		return;
    }
	if (displayMode == 3)
	{
        drawShape3();
		return;
    }
	if (displayMode == 4)
	{
        drawShape4();
		return;
    }

	if (displayMode == 5)
	{
        drawShape5();
		return;
	}

	if (displayMode == 6)
	{
        drawShape6();
		return;
	}

	if (displayMode == 7)
	{
        drawShape7();
		return;
	}

	if (displayMode == 8)
	{
        drawShape8();
		return;
	}

	if (displayMode == 9)
	{
        drawBlank();
		return;
	}
	return;
}

//--------------------------------------------------------------
void prjApp::drawBlank()
{

}

//--------------------------------------------------------------
void prjApp::drawCalibrationMode()
{

	int width = ofGetWidth(); 
	int height = ofGetHeight();
	int centerX = width / 2;
	int centerY = height / 4 * 3;

	int parameter2Y = parameteHolder_.get(0).getValue();
	int marginCenterY = parameteHolder_.get(1).getValue();
	int patameter5 = parameteHolder_.get(2).getValue();
	int parameter3 = parameteHolder_.get(3).getValue();
	int parameter6 = parameteHolder_.get(4).getValue();
	int parameter33 = parameteHolder_.get(5).getValue();
	int parameter66 = parameteHolder_.get(6).getValue();
	int parameter4 = parameteHolder_.get(7).getValue();
	int line7Y = parameteHolder_.get(8).getValue();
	int line4Y = parameteHolder_.get(9).getValue();

	// 線分の傾き(slope1 は0)
	double slope2 = ((double) parameter2Y - (double) centerY) / ((double) width - (double) centerX);
	double slope3 = ((double) height) / ((double) parameter3 - (double) parameter33);
	double slope4 = ((double) line4Y - ((double) centerY + (double) parameter4)) / ((double) width - (double) centerX);
	double slope5 = (((double) 130.0 + (double)patameter5) - (double)centerY) / ((double)- centerX);
	double slope6 = (((double) height) / ((double)parameter6 - (double) parameter66));
	double slope7 = ((double) line7Y - ((double) centerY + (double) parameter4)) / ((double)-centerX);

	// 線分の接点
    double intercept2 = (double) centerY - (double) slope2 * (double) centerX;
    double intercept3 = 0 - (double) slope3 * ((double) centerX + (double) parameter33);
    double intercept4 = (double) line4Y - (double) slope4 * (double) width;
	double intercept5 = (double) centerY - (double) slope5 * (double) centerX;
	double intercept6 = (double) 0 - (double) slope6 * (double) (centerX + parameter66);
	double intercept7 = (double) line7Y;

	/**/
	double x = (intercept2 - intercept3) / (slope3 - slope2);
	double y = (slope3 * intercept2 - slope2 * intercept3) / (slope3 - slope2);
	positionHolder_.set(0, x, y);

	x = (intercept4 - intercept3) / (slope3 - slope4);
    y = (slope3 * intercept4 - slope4 * intercept3) / (slope3 - slope4);
    positionHolder_.set(1, x, y);

	x = (double) centerX;
	y = (double) centerY + (double) parameter4;
    positionHolder_.set(2, x, y);

	x = (double) centerX;
	y = (double) centerY;
    positionHolder_.set(3, x, y);

	x = (intercept6 - intercept7) / (slope7 - slope6);
    y = (slope7 * intercept6 - slope6 * intercept7) / (slope7 - slope6);
    positionHolder_.set(4, x, y);

	x = (intercept5 - intercept6) / (slope6 - slope5);
    y = (slope6 * intercept5 - slope5 * intercept6) / (slope6 - slope5);
    positionHolder_.set(5, x, y);

	x = (double) centerX;
	y = (double) marginCenterY;
    positionHolder_.set(6, x, y);

	ofSetColor(255,255,255,255);

	// 中心線の表示
	ofSetColor(255,255,255,255);
	ofLine(0, centerY, width, centerY);    // 横

	// 中心線の表示(線分1)
	ofSetColor(255,255,255,255);
	ofLine(centerX, 0, centerX, height);   // 縦

	// 線分2
	ofSetColor(255,255,255,255);
	ofLine(centerX, centerY, width, parameter2Y);

	// 線分3
	ofSetColor(255,255,255,255);
	ofLine(centerX + parameter33, 0, centerX + parameter3, height);

	// 線分4
	ofSetColor(255,255,255,255);
	ofLine(centerX, centerY + parameter4, width, line4Y);

	// 線分5
	ofSetColor(255,255,255,255);
	ofLine(centerX, centerY, 0, 130.0 + patameter5);

	// 線分6
	ofSetColor(255,255,255,255);
    ofLine(centerX + parameter66, 0, centerX + parameter6, height);

	// 線分7
	ofSetColor(255,255,255,255);
	ofLine(centerX, centerY + parameter4, 0, line7Y);

	// テスト線分
	//ofSetColor(255,128,255,255);
	//ofLine(0, intercept4, width, width * slope4 + intercept4);

	// 線分の交点をマーキング
	ofSetColor(255, 0, 255);
	for (int index = 0; index < positionHolder_.getSize(); index++)
	{
	    ofCircle(positionHolder_.getX(index), positionHolder_.getY(index), 4);
	}

    // 現在の値を画面表示
	ofSetColor(255,255,255,255);
	ofTrueTypeFont myfont;
    myfont.loadFont("mplus-1c-light.ttf", 10);
	double hfSize = myfont.stringHeight("ZZ") + 3;
	for (int idx = 0; idx < parameteHolder_.size(); idx++)
	{
		stringstream ss;
		ss << parameteHolder_.get(idx).getName() << " : " << parameteHolder_.get(idx).getValue();
		myfont.drawString(ss.str(), 15, 25 + hfSize * idx);
	}

	// メッセージ表示...
	if (messageToShow.length() != 0)
	{
        ofSetColor(255,96,96,255);
		double hfHeight = height - hfSize;
		myfont.drawString(messageToShow, 15, hfHeight);
	}
	return;
}

//--------------------------------------------------------------
void prjApp::drawShape1()
{

    ofStyle style;
	ofSetStyle(style);

	ofSetColor(255,255,255,255);
	ofLine(positionHolder_.getX(0),positionHolder_.getY(0),positionHolder_.getX(1),positionHolder_.getY(1));
	ofLine(positionHolder_.getX(1),positionHolder_.getY(1),positionHolder_.getX(6),positionHolder_.getY(6));
	ofLine(positionHolder_.getX(6),positionHolder_.getY(6),positionHolder_.getX(4),positionHolder_.getY(4));
	ofLine(positionHolder_.getX(4),positionHolder_.getY(4),positionHolder_.getX(5),positionHolder_.getY(5));
	ofLine(positionHolder_.getX(5),positionHolder_.getY(5),positionHolder_.getX(3),positionHolder_.getY(3));
	ofLine(positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(2),positionHolder_.getY(2));
	ofLine(positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(4),positionHolder_.getY(4));
	ofLine(positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(0),positionHolder_.getY(0));
	ofLine(positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(1),positionHolder_.getY(1));

}

//--------------------------------------------------------------
void prjApp::drawShape2()
{
	ofColor color;
	int colorHigh = 255;
	int colorLow = 48;

	int count = slowCounter / 24;
	if (((count) % 8) == 0)
	{
        color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_2();
		drawShape2_3();
	}
	if (((count) % 8) == 1)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_2();
		drawShape2_3();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();

	}
	if (((count) % 8) == 2)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_3();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_2();
	}
	if (((count) % 8) == 3)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_2();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_3();
	}
	if (((count) % 8) == 4)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_2();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_3();
	}
	if (((count) % 8) == 5)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_2();
		drawShape2_3();
	}
	if (((count) % 8) == 6)
	{
		color.set(colorLow, colorLow, colorLow);
		ofSetColor(color);
        ofNoFill();
		drawShape2_3();

		color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_2();
	}
	if (((count) % 8) == 7)
	{
        color.set(colorHigh, colorHigh, colorHigh);
		ofSetColor(color);
        ofNoFill();
		drawShape2_1();
		drawShape2_2();
		drawShape2_3();
	}
}

//--------------------------------------------------------------
void prjApp::drawShape2_1()
{
	ofPolyline polyLine;
	polyLine.clear();
	polyLine.addVertex(positionHolder_.getX(2),positionHolder_.getY(2));
	polyLine.addVertex(positionHolder_.getX(3),positionHolder_.getY(3));
	polyLine.addVertex(positionHolder_.getX(5),positionHolder_.getY(5));
	polyLine.addVertex(positionHolder_.getX(4),positionHolder_.getY(4));
	polyLine.close();
	polyLine.draw();
    //ofLine(positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(3),positionHolder_.getY(3));
    //ofLine(positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(5),positionHolder_.getY(5));
    //ofLine(positionHolder_.getX(5),positionHolder_.getY(5),positionHolder_.getX(4),positionHolder_.getY(4));
    //ofLine(positionHolder_.getX(4),positionHolder_.getY(4),positionHolder_.getX(2),positionHolder_.getY(2));
}

//--------------------------------------------------------------
void prjApp::drawShape2_2()
{
    ofLine(positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(4),positionHolder_.getY(4));
    ofLine(positionHolder_.getX(4),positionHolder_.getY(4),positionHolder_.getX(6),positionHolder_.getY(6));
    ofLine(positionHolder_.getX(6),positionHolder_.getY(6),positionHolder_.getX(1),positionHolder_.getY(1));
    ofLine(positionHolder_.getX(1),positionHolder_.getY(1),positionHolder_.getX(2),positionHolder_.getY(2));
}

//--------------------------------------------------------------
void prjApp::drawShape2_3()
{
    ofLine(positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(1),positionHolder_.getY(1));
    ofLine(positionHolder_.getX(1),positionHolder_.getY(1),positionHolder_.getX(0),positionHolder_.getY(0));
    ofLine(positionHolder_.getX(0),positionHolder_.getY(0),positionHolder_.getX(3),positionHolder_.getY(3));
    ofLine(positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(2),positionHolder_.getY(2));
}


//--------------------------------------------------------------
void prjApp::drawShape4()
{
	ofColor color;
	int colorDepth = 255;
	color.set(colorDepth, colorDepth, colorDepth);
	drawShape(color, 0,1,2,3);
	drawShape(color, 3,2,4,5);
	drawShape(color, 1,2,4,6);
}

//--------------------------------------------------------------
void prjApp::drawShape5()
{
	ofColor color;
	int colorDepth = (((int) slowCounter) % 64) * 2 + 128;

	color.set(0, colorDepth, 0);
	drawShape(color, 0,1,2,3);

	color.set(colorDepth, colorDepth, 0);
	drawShape(color, 3,2,4,5);

	color.set(colorDepth, 0, 0);
	drawShape(color, 1,2,4,6);
}

//--------------------------------------------------------------
void prjApp::drawShape6()
{
	ofColor color;
	int colorDepth = ((int) counter) % 255;

	color.set(0, colorDepth, colorDepth);
	drawShape(color, 0,1,2,3);

	color.set(0, colorDepth, 0);
	drawShape(color, 3,2,4,5);

	color.set(colorDepth, 0, colorDepth);
	drawShape(color, 1,2,4,6);
}

//--------------------------------------------------------------
void prjApp::drawShape7()
{
	ofColor color;
	int colorDepth = 255;
	color.set(colorDepth, colorDepth, colorDepth);
	
	mapVideo(color, 3,2,4,5);
	mapImage(color, 0,1,2,3);
	mapVideo(color, 1,6,4,2);

	drawShape1();
}

//--------------------------------------------------------------
void prjApp::drawShape8()
{
	ofColor color;
	int colorDepth = 255;
	color.set(colorDepth, colorDepth, colorDepth);
	
	mapVideo(color, 1,6,4,2);


	// マッピングする画像のサイズと実データ（ビットマップ）を取得
	int iWidth = picture.getWidth();
	int iHeight = picture.getHeight();
	unsigned char *iPixels = picture.getPixels();

    // イメージを描画するためのエリアを確保する
	ofImage targetImage;
	targetImage.allocate(iWidth, iHeight, OF_IMAGE_COLOR);
    unsigned char *screen = targetImage.getPixels();
	memset(screen,0x00, (iWidth * iHeight * 3));


	float mod = ((float) (counter % 10)) / 10.0f;

	// イメージをマージする
    margeImageHorizontal(screen, iPixels, iPixels, iWidth, iHeight, mod);

	// 面にマッピングして表示する
	ofSetColor(color);
	drawImageMapping(screen, iWidth, iHeight, 0, 1, 2, 3);



    // イメージを描画するためのエリアを確保する
	ofImage targetImage2;
	targetImage2.allocate(iWidth, iHeight, OF_IMAGE_COLOR);
    unsigned char *screen2 = targetImage2.getPixels();
	memset(screen2, 0x00, (iWidth * iHeight * 3));

	// イメージをマージする
    margeImageVertical(screen2, iPixels, iPixels, iWidth, iHeight, mod);

	// 面にマッピングして表示する
	ofSetColor(color);
	drawImageMapping(screen2, iWidth, iHeight, 3,2,4,5);

	drawShape1();
}

//--------------------------------------------------------------
void prjApp::drawShape3()
{

    ofStyle style;
	ofSetStyle(style);

	ofColor color;
	int colorDepth = 255;
	color.set(colorDepth, colorDepth, colorDepth);

	int count = slowCounter / 10;
	float mod = ((float) (count % 5)) / 4.0f;

	drawLine(color, positionHolder_.getX(0),positionHolder_.getY(0),positionHolder_.getX(1),positionHolder_.getY(1), 0, mod);
	drawLine(color, positionHolder_.getX(1),positionHolder_.getY(1),positionHolder_.getX(6),positionHolder_.getY(6), 0, mod);
	drawLine(color, positionHolder_.getX(6),positionHolder_.getY(6),positionHolder_.getX(4),positionHolder_.getY(4), 0, mod);
	drawLine(color, positionHolder_.getX(4),positionHolder_.getY(4),positionHolder_.getX(5),positionHolder_.getY(5), 0, mod);
	drawLine(color, positionHolder_.getX(5),positionHolder_.getY(5),positionHolder_.getX(3),positionHolder_.getY(3), 0, mod);
	drawLine(color, positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(2),positionHolder_.getY(2), 0, mod);
	drawLine(color, positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(4),positionHolder_.getY(4), 0, mod);
	drawLine(color, positionHolder_.getX(3),positionHolder_.getY(3),positionHolder_.getX(0),positionHolder_.getY(0), 0, mod);
	drawLine(color, positionHolder_.getX(2),positionHolder_.getY(2),positionHolder_.getX(1),positionHolder_.getY(1), 0, mod);

}


//--------------------------------------------------------------
void prjApp::drawLine(ofColor color, float x1, float y1, float x2, float y2, float startPercentage, float endParcentage)
{
	float x3 = (x2 - x1) * startPercentage + x1;
	float y3 = (y2 - y1) * startPercentage + y1;

	float x4 = (x2 - x1) * endParcentage + x1;
	float y4 = (y2 - y1) * endParcentage + y1;

	// とりあえずグレーのラインを引く
	ofSetColor(64);
	ofLine(x1, y1, x2, y2);

	// 予定した色のラインを引く
	ofSetColor(color);
	ofLine(x3, y3, x4, y4);
}

//--------------------------------------------------------------
void prjApp::mapImage(ofColor color, int pos1, int pos2, int pos3, int pos4)
{
	// マッピングする画像のサイズと実データ（ビットマップ）を取得
	int iWidth = picture.getWidth();
	int iHeight = picture.getHeight();
	unsigned char *iPixels = picture.getPixels();

	// 面にマッピングして表示する
	ofSetColor(color);
	drawImageMapping(iPixels, iWidth, iHeight, pos1, pos2, pos3, pos4);
}

//--------------------------------------------------------------
void prjApp::mapVideo(ofColor color, int pos1, int pos2, int pos3, int pos4)
{

	// マッピングする画像のサイズと実データ（ビットマップ）を取得
	int vWidth = video.getWidth();
	int vHeight = video.getHeight();
    unsigned char *vPixels = video.getPixels();

	// 面にマッピングして表示する
	ofSetColor(color);
	drawImageMapping(vPixels, vWidth, vHeight, pos1, pos2, pos3, pos4);

}

/**
 *   水平方向のマージを行う
 *
 */
void prjApp::margeImageHorizontal(unsigned char *marged, unsigned char *left, unsigned char *right, int width, int height, float position)
{
	if ((position < 0.0f)||(position > 1.0f))
	{
		position = 0.0f;
	}
	int threshold = ((float) width * (1.0f - position));
    for (int y = 0; y < height; y = y + 1)
	{
        int leftX = threshold;
        int rightX = 0;
        for (int x =  0; x < width; x = x + 1)
		{
			if (leftX < width)
			{
                marged[(y * width + x)* 3 + 0] = left[(y * width + leftX)* 3 + 0];  // r
			    marged[(y * width + x)* 3 + 1] = left[(y * width + leftX)* 3 + 1];  // g
			    marged[(y * width + x)* 3 + 2] = left[(y * width + leftX)* 3 + 2];  // b
				leftX = leftX + 1;
			}
			else
			{
                marged[(y * width + x)* 3 + 0] = right[(y * width + rightX)* 3 + 0];  // r
			    marged[(y * width + x)* 3 + 1] = right[(y * width + rightX)* 3 + 1];  // g
			    marged[(y * width + x)* 3 + 2] = right[(y * width + rightX)* 3 + 2];  // b
				rightX = rightX + 1;
			}
		}
	}
}

/**
 *    垂直方向
 *
 */
void prjApp::margeImageVertical(unsigned char *marged, unsigned char *upper, unsigned char *lower, int width, int height, float position)
{
	if ((position < 0.0f)||(position > 1.0f))
	{
		position = 0.0f;
	}
	int threshold = ((float) height * (1.0f - position));
    for (int x = 0; x < width; x = x + 1)
	{
        int upperY = threshold;
        int lowerY = 0;
        for (int y =  0; y < height; y = y + 1)
		{
			if (upperY < height)
			{
                marged[(y * width + x)* 3 + 0] = upper[(upperY * width + x)* 3 + 0];  // r
			    marged[(y * width + x)* 3 + 1] = upper[(upperY * width + x)* 3 + 1];  // g
			    marged[(y * width + x)* 3 + 2] = upper[(upperY * width + x)* 3 + 2];  // b
				upperY = upperY + 1;
			}
			else
			{
                marged[(y * width + x)* 3 + 0] = lower[(lowerY * width + x)* 3 + 0];  // r
			    marged[(y * width + x)* 3 + 1] = lower[(lowerY * width + x)* 3 + 1];  // g
			    marged[(y * width + x)* 3 + 2] = lower[(lowerY * width + x)* 3 + 2];  // b
				lowerY = lowerY + 1;
			}
		}
	}
}


//--------------------------------------------------------------
void prjApp::drawImageMapping(unsigned char *imagePixels, int imageWidth, int imageHeight, int pos1, int pos2, int pos3, int pos4)
{

	int targetWidth = 800;
	int targetHeight = 600;

    // イメージを描画するためのエリアを確保する
	ofImage targetImage;
	targetImage.allocate(targetWidth, targetHeight, OF_IMAGE_COLOR);
    unsigned char *screen = targetImage.getPixels();
	memset(screen,0x00, (targetWidth * targetHeight * 3));

	// 上半分の変換行列
	//
	//   a  b
	//   c  d
	//
	float matA = (positionHolder_.getX(pos2) - positionHolder_.getX(pos3));
	float matC = (positionHolder_.getY(pos2) - positionHolder_.getY(pos3));
	float matB = (positionHolder_.getX(pos1) - positionHolder_.getX(pos2));
	float matD = (positionHolder_.getY(pos1) - positionHolder_.getY(pos2));

	// 下半分の変換行列
	//
	//   e  f
	//   g  h
	//
	float matE = (positionHolder_.getX(pos1) - positionHolder_.getX(pos4));
	float matG = (positionHolder_.getY(pos1) - positionHolder_.getY(pos4));
	float matF = (positionHolder_.getX(pos4) - positionHolder_.getX(pos3));
	float matH = (positionHolder_.getY(pos4) - positionHolder_.getY(pos3));

	float offsetX = positionHolder_.getX(pos3);
	float offsetY = positionHolder_.getY(pos3);

	//-------------------------------------------------------------------
	//  画像を、upperImage と lowerImage の三角形に分割し、
	//  投影面の座標に合わせたマッピング（変形）を行う
	//
    //  lowerImage(lx,ly)   upperImage(x,y)
	//     +                     +----+
	//     |＼                    ＼  |
	//     |  ＼                    ＼|
	//     +----+                     +
	//
	//-------------------------------------------------------------------
    for (int y = 0; y < imageHeight; y = y + 1)
	{
		int startX = ((float) y * ((float) imageWidth / (float) imageHeight)) + 1;
        for (int x =  startX; x < imageWidth; x = x + 1)
		{
			int lx = imageWidth - x;
			int ly = imageHeight - y;

            unsigned char r1 = imagePixels[(y * imageWidth + x)* 3 + 0];
            unsigned char g1 = imagePixels[(y * imageWidth + x)* 3 + 1];
            unsigned char b1 = imagePixels[(y * imageWidth + x)* 3 + 2];

			unsigned char r2 = imagePixels[(ly * imageWidth + lx)* 3 + 0];
            unsigned char g2 = imagePixels[(ly * imageWidth + lx)* 3 + 1];
            unsigned char b2 = imagePixels[(ly * imageWidth + lx)* 3 + 2];

			// 回転行列を使って (x, y) を (x1, y1) に移動させる
			int x1 = (int) (matA * ((float) x / (float) imageWidth) + matB * ((float) y / (float) imageHeight)) + offsetX;
			int y1 = (int) (matC * ((float) x / (float) imageWidth) + matD * ((float) y / (float) imageHeight)) + offsetY;
			int index1 = (y1 * targetWidth + x1) * 3;
			if ((index1 < 0)||(index1 > (targetWidth * targetHeight * 3)))
			{
				index1 = 0;
			}
            screen[index1 + 0] = (unsigned char) r1;
			screen[index1 + 1] = (unsigned char) g1;
			screen[index1 + 2] = (unsigned char) b1;

			// 回転行列を使って (lx, ly) を (x2, y2) に移動させる
			int x2 = (int) (matE * ((float) lx / (float) imageWidth) + matF * ((float) ly / (float) imageHeight)) + offsetX;
			int y2 = (int) (matG * ((float) lx / (float) imageWidth) + matH * ((float) ly / (float) imageHeight)) + offsetY;
			int index2 = (y2 * targetWidth + x2) * 3;
			if ((index2 < 0)||(index2 > (targetWidth * targetHeight * 3)))
			{
				index2 = 0;
			}
            screen[index2 + 0] = (unsigned char) r2;
			screen[index2 + 1] = (unsigned char) g2;
			screen[index2 + 2] = (unsigned char) b2;
		}
	}
	targetImage.setFromPixels(screen, targetWidth, targetHeight, OF_IMAGE_COLOR, true);
	targetImage.draw(0.0f,0.0f);
}


//--------------------------------------------------------------
void prjApp::drawShape(ofColor color, int pos1, int pos2, int pos3, int pos4)
{
	ofSetColor(color);
    ofFill();
	ofSetPolyMode(OF_POLY_WINDING_NONZERO);
	ofBeginShape();
	ofVertex(positionHolder_.getX(pos1),positionHolder_.getY(pos1));
	ofVertex(positionHolder_.getX(pos2),positionHolder_.getY(pos2));
	ofVertex(positionHolder_.getX(pos3),positionHolder_.getY(pos3));
	ofVertex(positionHolder_.getX(pos4),positionHolder_.getY(pos4));
	ofEndShape();
}

//--------------------------------------------------------------
void prjApp::keyPressed(int key)
{
	if (displayMode == 0)
	{
		keyPressedCalibrationMode(key);
		return;
	}

	// 表示モードを切り替える (キャリブレーションモードは、最初だけ表示する）
	displayMode = (displayMode == 9) ? 1 : (displayMode + 1);
}


//--------------------------------------------------------------
void prjApp::keyPressedCalibrationMode(int key)
{
	messageToShow = "";

	switch (key)
	{
      case 'q': case 'Q':
		parameteHolder_.get(0).decrement();
		return;
		break;
      case 'z': case 'Z':
		parameteHolder_.get(0).increment();
		return;
		break;
      case 'a': case 'A':
		parameteHolder_.get(0).reset();
		return;
		break;

	  case 'w': case 'W':
		parameteHolder_.get(1).decrement();
		return;
		break;
      case 'x': case 'X':
		parameteHolder_.get(1).increment();
		return;
		break;
      case 's': case 'S':
		parameteHolder_.get(1).reset();
		return;
		break;

      case 'e': case 'E':
		parameteHolder_.get(2).decrement();
		return;
		break;
      case 'c': case 'C':
		parameteHolder_.get(2).increment();
		return;
		break;
      case 'd': case 'D':
		parameteHolder_.get(2).reset();
		return;
		break;

      case 'r': case 'R':
		parameteHolder_.get(3).decrement();
		return;
		break;
      case 'v': case 'V':
		parameteHolder_.get(3).increment();
		return;
		break;
      case 'f': case 'F':
		parameteHolder_.get(3).reset();
		return;
		break;

      case 't': case 'T':
		parameteHolder_.get(4).decrement();
		return;
		break;
      case 'b': case 'B':
		parameteHolder_.get(4).increment();
		return;
		break;
      case 'g': case 'G':
		parameteHolder_.get(4).reset();
		return;
		break;

      case 'y': case 'Y':
		parameteHolder_.get(5).decrement();
		return;
		break;
      case 'n': case 'N':
		parameteHolder_.get(5).increment();
		return;
		break;
      case 'h': case 'H':
		parameteHolder_.get(5).reset();
		return;
		break;

      case 'u': case 'U':
		parameteHolder_.get(6).decrement();
		return;
		break;
      case 'm': case 'M':
		parameteHolder_.get(6).increment();
		return;
		break;
      case 'j': case 'J':
		parameteHolder_.get(6).reset();
		return;
		break;

      case 'i': case 'I':
		parameteHolder_.get(7).decrement();
		return;
		break;
      case ',': case '<':
		parameteHolder_.get(7).increment();
		return;
		break;
      case 'k': case 'K':
		parameteHolder_.get(7).reset();
		return;
		break;

      case 'o': case 'O':
		parameteHolder_.get(8).decrement();
		return;
		break;
      case '.': case '>':
		parameteHolder_.get(8).increment();
		return;
		break;
      case 'l': case 'L':
		parameteHolder_.get(8).reset();
		return;
		break;

      case 'p': case 'P':
		parameteHolder_.get(9).decrement();
		return;
		break;

	  case '/': case '?':
		parameteHolder_.get(9).increment();
		return;
		break;
	  case ';': case ':':
		parameteHolder_.get(9).reset();
		return;
		break;

	  default:
	    break;
	}

	if (key == '=')
	{
		parameteHolder_.backup();
		messageToShow = "SAVED";
		return;
	}
	if (key == '`')
	{
		resetPosition();
		messageToShow = "RESET";
		return;
	}

	displayMode++;

	return;
}

//--------------------------------------------------------------
void prjApp::keyReleased(int key)
{

}

//--------------------------------------------------------------
void prjApp::mouseMoved(int x, int y)
{

}

//--------------------------------------------------------------
void prjApp::mouseDragged(int x, int y, int button)
{

}

//--------------------------------------------------------------
void prjApp::mousePressed(int x, int y, int button)
{

}

//--------------------------------------------------------------
void prjApp::mouseReleased(int x, int y, int button)
{

}

//--------------------------------------------------------------
void prjApp::windowResized(int w, int h)
{

}

//--------------------------------------------------------------
void prjApp::gotMessage(ofMessage msg)
{

}

//--------------------------------------------------------------
void prjApp::dragEvent(ofDragInfo dragInfo)
{ 

}
