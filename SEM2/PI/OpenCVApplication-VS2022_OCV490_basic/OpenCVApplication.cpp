// OpenCVApplication.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "common.h"
#include <opencv2/core/utils/logger.hpp>
#include <queue>
#include <random>
#include <functional>
#include <numeric>
#include <string>
#include <vector>
#include <math.h>

using namespace std;
wchar_t* projectPath;

void testOpenImage()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src;
		src = imread(fname);
		imshow("image", src);
		waitKey();
	}
}

void testOpenImagesFld()
{
	char folderName[MAX_PATH];
	if (openFolderDlg(folderName) == 0)
		return;
	char fname[MAX_PATH];
	FileGetter fg(folderName, "bmp");
	while (fg.getNextAbsFile(fname))
	{
		Mat src;
		src = imread(fname);
		imshow(fg.getFoundFileName(), src);
		if (waitKey() == 27) //ESC pressed
			break;
	}
}

void testImageOpenAndSave()
{
	_wchdir(projectPath);

	Mat src, dst;

	src = imread("Images/Lena_24bits.bmp", IMREAD_COLOR);	// Read the image

	if (!src.data)	// Check for invalid input
	{
		printf("Could not open or find the image\n");
		return;
	}

	// Get the image resolution
	Size src_size = Size(src.cols, src.rows);

	// Display window
	const char* WIN_SRC = "Src"; //window for the source image
	namedWindow(WIN_SRC, WINDOW_AUTOSIZE);
	moveWindow(WIN_SRC, 0, 0);

	const char* WIN_DST = "Dst"; //window for the destination (processed) image
	namedWindow(WIN_DST, WINDOW_AUTOSIZE);
	moveWindow(WIN_DST, src_size.width + 10, 0);

	cvtColor(src, dst, COLOR_BGR2GRAY); //converts the source image to a grayscale one

	imwrite("Images/Lena_24bits_gray.bmp", dst); //writes the destination to file

	imshow(WIN_SRC, src);
	imshow(WIN_DST, dst);

	waitKey(0);
}

void testNegativeImage()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount(); // Get the current time [s]

		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = Mat(height, width, CV_8UC1);
		// Accessing individual pixels in an 8 bits/pixel image
		// Inefficient way -> slow
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				uchar val = src.at<uchar>(i, j);
				uchar neg = 255 - val;
				dst.at<uchar>(i, j) = neg;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("negative image", dst);
		waitKey();
	}
}

void testNegativeImageFast()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = src.clone();

		double t = (double)getTickCount(); // Get the current time [s]

		// The fastest approach of accessing the pixels -> using pointers
		uchar* lpSrc = src.data;
		uchar* lpDst = dst.data;
		int w = (int)src.step; // no dword alignment is done !!!
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				uchar val = lpSrc[i * w + j];
				lpDst[i * w + j] = 255 - val;
			}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("negative image", dst);
		waitKey();
	}
}

void testColor2Gray()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname);

		int height = src.rows;
		int width = src.cols;

		Mat dst = Mat(height, width, CV_8UC1);

		// Accessing individual pixels in a RGB 24 bits/pixel image
		// Inefficient way -> slow
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Vec3b v3 = src.at<Vec3b>(i, j);
				uchar b = v3[0];
				uchar g = v3[1];
				uchar r = v3[2];
				dst.at<uchar>(i, j) = (r + g + b) / 3;
			}
		}

		imshow("input image", src);
		imshow("gray image", dst);
		waitKey();
	}
}

void testBGR2HSV()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname);
		int height = src.rows;
		int width = src.cols;

		// HSV components
		Mat H = Mat(height, width, CV_8UC1);
		Mat S = Mat(height, width, CV_8UC1);
		Mat V = Mat(height, width, CV_8UC1);

		// Defining pointers to each matrix (8 bits/pixels) of the individual components H, S, V 
		uchar* lpH = H.data;
		uchar* lpS = S.data;
		uchar* lpV = V.data;

		Mat hsvImg;
		cvtColor(src, hsvImg, COLOR_BGR2HSV);

		// Defining the pointer to the HSV image matrix (24 bits/pixel)
		uchar* hsvDataPtr = hsvImg.data;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				int hi = i * width * 3 + j * 3;
				int gi = i * width + j;

				lpH[gi] = hsvDataPtr[hi] * 510 / 360;	// lpH = 0 .. 255
				lpS[gi] = hsvDataPtr[hi + 1];			// lpS = 0 .. 255
				lpV[gi] = hsvDataPtr[hi + 2];			// lpV = 0 .. 255
			}
		}

		imshow("input image", src);
		imshow("H", H);
		imshow("S", S);
		imshow("V", V);

		waitKey();
	}
}

void testResize()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src;
		src = imread(fname);
		Mat dst1, dst2;
		//without interpolation
		resizeImg(src, dst1, 320, false);
		//with interpolation
		resizeImg(src, dst2, 320, true);
		imshow("input image", src);
		imshow("resized image (without interpolation)", dst1);
		imshow("resized image (with interpolation)", dst2);
		waitKey();
	}
}

void testCanny()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src, dst, gauss;
		src = imread(fname, IMREAD_GRAYSCALE);
		double k = 0.4;
		int pH = 50;
		int pL = (int)k * pH;
		GaussianBlur(src, gauss, Size(5, 5), 0.8, 0.8);
		Canny(gauss, dst, pL, pH, 3);
		imshow("input image", src);
		imshow("canny", dst);
		waitKey();
	}
}

void testVideoSequence()
{
	_wchdir(projectPath);

	VideoCapture cap("Videos/rubic.avi"); // off-line video from file
	//VideoCapture cap(0);	// live video from web cam
	if (!cap.isOpened()) {
		printf("Cannot open video capture device.\n");
		waitKey(0);
		return;
	}

	Mat edges;
	Mat frame;
	char c;

	while (cap.read(frame))
	{
		Mat grayFrame;
		cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
		Canny(grayFrame, edges, 40, 100, 3);
		imshow("source", frame);
		imshow("gray", grayFrame);
		imshow("edges", edges);
		c = waitKey(100);  // waits 100ms and advances to the next frame
		if (c == 27) {
			// press ESC to exit
			printf("ESC pressed - capture finished\n");
			break;  //ESC pressed
		};
	}
}


void testSnap()
{
	_wchdir(projectPath);

	VideoCapture cap(0); // open the deafult camera (i.e. the built in web cam)
	if (!cap.isOpened()) // openenig the video device failed
	{
		printf("Cannot open video capture device.\n");
		return;
	}

	Mat frame;
	char numberStr[256];
	char fileName[256];

	// video resolution
	Size capS = Size((int)cap.get(CAP_PROP_FRAME_WIDTH),
		(int)cap.get(CAP_PROP_FRAME_HEIGHT));

	// Display window
	const char* WIN_SRC = "Src"; //window for the source frame
	namedWindow(WIN_SRC, WINDOW_AUTOSIZE);
	moveWindow(WIN_SRC, 0, 0);

	const char* WIN_DST = "Snapped"; //window for showing the snapped frame
	namedWindow(WIN_DST, WINDOW_AUTOSIZE);
	moveWindow(WIN_DST, capS.width + 10, 0);

	char c;
	int frameNum = -1;
	int frameCount = 0;

	for (;;)
	{
		cap >> frame; // get a new frame from camera
		if (frame.empty())
		{
			printf("End of the video file\n");
			break;
		}

		++frameNum;

		imshow(WIN_SRC, frame);

		c = waitKey(10);  // waits a key press to advance to the next frame
		if (c == 27) {
			// press ESC to exit
			printf("ESC pressed - capture finished");
			break;  //ESC pressed
		}
		if (c == 115) { //'s' pressed - snap the image to a file
			frameCount++;
			fileName[0] = NULL;
			sprintf(numberStr, "%d", frameCount);
			strcat(fileName, "Images/A");
			strcat(fileName, numberStr);
			strcat(fileName, ".bmp");
			bool bSuccess = imwrite(fileName, frame);
			if (!bSuccess)
			{
				printf("Error writing the snapped image\n");
			}
			else
				imshow(WIN_DST, frame);
		}
	}
}


void testLab1Problema3()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = Mat(height, width, CV_8UC1);
		int factorAditiv;
		cout << "Factorul aditiv este:" << endl;
		cin >> factorAditiv;
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				uchar val = src.at<uchar>(i, j);
				uchar newVal;
				if (factorAditiv + val < 0) {
					newVal = 0;
				}
				else if (factorAditiv + val > 255) {
					newVal = 255;
				}
				else {
					newVal = factorAditiv + val;
				}
				dst.at<uchar>(i, j) = newVal;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("negative image", dst);
		waitKey();
	}
}

void testLab1Problema4()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = Mat(height, width, CV_8UC1);
		float factorMultiplicativ;
		cout << "Factorul multiplicativ este:" << endl;
		cin >> factorMultiplicativ;
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				uchar val = src.at<uchar>(i, j);
				uchar newVal;
				if (factorMultiplicativ < 0 || factorMultiplicativ * val < 0) {
					newVal = 0;
				}
				else if (factorMultiplicativ * val > 255) {
					newVal = 255;
				}
				else {
					newVal = factorMultiplicativ + val;
				}
				dst.at<uchar>(i, j) = newVal;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("negative image", dst);
		waitKey();
	}
}

void testLab1Problema5()
{
	double t = (double)getTickCount();

	Mat src = Mat(256, 256, CV_8UC3);
	int height = src.rows;
	int width = src.cols;
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			if (i < 128 && j < 128)
				src.at<Vec3b>(i, j) = Vec3b(255, 255, 255);
			if (i < 128 && j > 128)
				src.at<Vec3b>(i, j) = Vec3b(0, 0, 255);
			if (i > 128 && j < 128)
				src.at<Vec3b>(i, j) = Vec3b(0, 255, 0);
			if (i > 128 && j > 128)
				src.at<Vec3b>(i, j) = Vec3b(0, 255, 255);
		}
	}

	// Get the current time again and compute the time difference [s]
	t = ((double)getTickCount() - t) / getTickFrequency();
	// Print (in the console window) the processing time in [ms] 
	printf("Time = %.3f [ms]\n", t * 1000);

	imshow("input image", src);
	waitKey();
}

void testLab1Problema6()
{
	double t = (double)getTickCount();

	Mat src = Mat(3, 3, CV_32FC1);
	int height = src.rows;
	int width = src.cols;
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			float val;
			cin >> val;
			src.at<float>(i, j) = val;
		}
	}
	cout << src << endl;
	cout << src.inv() << endl;
	getchar();
	getchar();
}

void testLab2Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		Mat R = Mat(height, width, CV_8UC1);
		Mat G = Mat(height, width, CV_8UC1);
		Mat B = Mat(height, width, CV_8UC1);

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Vec3b colors = src.at<Vec3b>(i, j);
				B.at<uchar>(i, j) = colors[0];
				G.at<uchar>(i, j) = colors[1];
				R.at<uchar>(i, j) = colors[2];
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("RED image", R);
		imshow("GREEN image", G);
		imshow("BLUE image", B);


		waitKey();
	}
}


void testLab2Problema2()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		Mat GRAY = Mat(height, width, CV_8UC1);

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Vec3b colors = src.at<Vec3b>(i, j);
				GRAY.at<uchar>(i, j) = (colors[0] + colors[1] + colors[2]) / 3;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("GRAYSCALE image", GRAY);


		waitKey();
	}
}


void testLab2Problema3()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat BW = Mat(height, width, CV_8UC1);
		int threshold;
		cin >> threshold;
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				uchar colors = src.at<uchar>(i, j);
				if (colors < threshold) {
					BW.at<uchar>(i, j) = 0;
				}
				else {
					BW.at<uchar>(i, j) = 255;
				}

			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("BLACK AND WHITE image", BW);


		waitKey();
	}
}



void testLab2Problema4()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		Mat matrixH = Mat(height, width, CV_8UC1);
		Mat matrixS = Mat(height, width, CV_8UC1);
		Mat matrixV = Mat(height, width, CV_8UC1);

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Vec3b colors = src.at<Vec3b>(i, j);
				float r = (float)colors[2] / 255;
				float g = (float)colors[1] / 255;
				float b = (float)colors[0] / 255;
				float M = MAX(r, MAX(g, b));
				float m = MIN(r, MIN(g, b));
				float C = M - m;
				float V = M;
				float S = 0, H = 0;
				if (V != 0)
					S = C / V;
				if (C != 0) {
					if (M == r)
						H = 60 * (g - b) / C;
					if (M == g)
						H = 120 + 60 * (b - r) / C;
					if (M == b)
						H = 240 + 60 * (r - g) / C;
				}
				if (H < 0) {
					H = H + 360;
				}
				matrixH.at<uchar>(i, j) = H * 255 / 360;
				matrixS.at<uchar>(i, j) = S * 255;
				matrixV.at<uchar>(i, j) = V * 255;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		imshow("H image", matrixH);
		imshow("S image", matrixS);
		imshow("V image", matrixV);




		waitKey();
	}
}


bool isInside(Mat& src, int i, int j) {
	if (j < 0 || i < 0 || i > src.rows || j > src.cols)
		return false;
	else
		return true;
}

void testLab2Problema5()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		cout << width << "x" << height << endl;
		int X = 0, Y = 0;
		while (X != -1000 || Y != -1000) {
			cin >> X >> Y;
			if (isInside(src, X, Y) == true) {
				cout << "1" << endl;
			}
			else {
				cout << "0" << endl;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);

		waitKey();
	}
}


/* Histogram display function - display a histogram using bars (simlilar to L3 / Image Processing)
Input:
name - destination (output) window name
hist - pointer to the vector containing the histogram values
hist_cols - no. of bins (elements) in the histogram = histogram image width
hist_height - height of the histogram image
Call example:
showHistogram ("MyHist", hist_dir, 255, 200);
*/
void showHistogram(const std::string& name, int* hist, const int  hist_cols, const int hist_height)
{
	Mat imgHist(hist_height, hist_cols, CV_8UC3, CV_RGB(255, 255, 255)); // constructs a white image

	//computes histogram maximum
	int max_hist = 0;
	for (int i = 0; i < hist_cols; i++)
		if (hist[i] > max_hist)
			max_hist = hist[i];
	double scale = 1.0;
	scale = (double)hist_height / max_hist;
	int baseline = hist_height - 1;

	for (int x = 0; x < hist_cols; x++) {
		Point p1 = Point(x, baseline);
		Point p2 = Point(x, baseline - cvRound(hist[x] * scale));
		line(imgHist, p1, p2, CV_RGB(255, 0, 255)); // histogram bins colored in magenta
	}

	imshow(name, imgHist);
}

void showHistogram(const std::string& name, double* hist, const int  hist_cols, const int hist_height)
{
	Mat imgHist(hist_height, hist_cols, CV_8UC3, CV_RGB(255, 255, 255)); // constructs a white image

	//computes histogram maximum
	double max_hist = 0;
	for (int i = 0; i < hist_cols; i++)
		if (hist[i] > max_hist)
			max_hist = hist[i];
	double scale = 1.0;
	scale = (double)hist_height / max_hist;
	int baseline = hist_height - 1;

	for (int x = 0; x < hist_cols; x++) {
		Point p1 = Point(x, baseline);
		Point p2 = Point(x, baseline - cvRound(hist[x] * scale));
		line(imgHist, p1, p2, CV_RGB(255, 0, 255)); // histogram bins colored in magenta
	}

	imshow(name, imgHist);
}


void testLab3Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		int m;
		cin >> m;
		int hist[256] = { 0 };
		double fdpHist[256] = { 0.0 };
		int redHist[256] = { 0 };


		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				hist[src.at<uchar>(i, j)] += 1;
			}

		}
		for (int i = 0; i < 256; i++) {
			fdpHist[i] = (double)hist[i] / (height * width);
		}
		for (int i = 0; i < 256; i++) {
			redHist[(int)(i / 256.0f * m)] += hist[i];
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image", src);
		showHistogram("histograma ", hist, 256, 200);
		showHistogram("histograma FDP", fdpHist, 256, 200);
		showHistogram("histograma redusa", redHist, m, 200);






		waitKey();
	}
}
void testLab3Problema2()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		double t = (double)getTickCount();

		Mat src = imread(fname, IMREAD_ANYCOLOR);
		int height = src.rows;
		int width = src.cols;
		int WH = 5;
		int k = 0;
		float TH = 0.0003f;

		int hist[256] = { 0 };
		double fdpHist[256] = { 0.0f };
		double fdpMax[256] = { 0.0f };

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				hist[src.at<uchar>(i, j)] += 1;
			}

		}
		for (int i = 0; i < 256; i++) {
			fdpHist[i] = (double)hist[i] / (height * width);
		}

		for (int i = WH; i <= 255 - WH; i++) {
			double avg = 0.0;
			int lower = 0;
			for (int j = i - WH; j <= i + WH; j++) {
				if (fdpHist[i] < fdpHist[j])
					lower = 1;
				avg += (double)fdpHist[j] / (2 * WH + 1);
			}

			if ((fdpHist[i] > (avg + TH)) && lower == 0) {
				fdpMax[k++] = i;
			}
		}
		fdpMax[k++] = 255.0f;
		fdpMax[k++] = 0.0f;

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);


		imshow("input image", src);

		showHistogram("histogram", fdpHist, 256, 200);
		showHistogram("reduced", fdpMax, 256, 200);

		waitKey();
	}
}
void lab4function(int event, int x, int y, int flags, void* param) {
	if (event == EVENT_LBUTTONDOWN) {
		Mat src = *((Mat*)param);
		Mat srcBW = Mat(src.rows, src.cols, CV_8UC1);

		Vec3b selectedPixel = src.at<Vec3b>(y, x);
		if (selectedPixel == Vec3b(255, 255, 255)) return;
		int aria = 0, rows = 0, cols = 0;
		double rowsN, colsN, numitor = 0, numarator = 0, P = 0;

		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				Vec3b currentPixel = src.at<Vec3b>(i, j);
				if (selectedPixel == currentPixel) {
					srcBW.at<uchar>(i, j) = 0;
					aria++;
					cols += j;
					rows += i;
				}
				else
					srcBW.at<uchar>(i, j) = 255;
			}
		}
		rowsN = 1.0 * rows / aria;
		colsN = 1.0 * cols / aria;

		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				Vec3b currentPixel = src.at<Vec3b>(i, j);
				if (selectedPixel == currentPixel) {
					numarator += (i - rowsN) * (j - colsN);
					double temp1 = (j - colsN) * (j - colsN);
					double temp2 = (i - rowsN) * (i - rowsN);
					double temp = temp1 - temp2;
					numitor += temp;
				}
				int ok = 0;
				for (int k = i - 1; k <= i + 1; k++) {
					for (int l = j - 1; l <= j + 1; l++) {
						if ((isInside(src, k, l) == 1)) {
							ok = 1;
						}
					}
				}
				if (ok == 1) {
					P++;
				}
			}
		}
		numarator *= 2;
		P = P * (PI / 4);


		printf("A=%d, C(%.2f, %.2f), Phi=%f, P=%.2lf", aria, rowsN, colsN, atan2(numarator, numitor) / PI * 90, P);
		imshow("proj image", srcBW);
	}
}

void testLab4Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_COLOR);
		int height = src.rows;
		int width = src.cols;

		imshow("input image", src);

		setMouseCallback("input image", lab4function, &src);

		waitKey();
	}
}

void testLab5Problema1(int neigh)
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;



		int label = 0;
		Mat labels = Mat::zeros(height, width, CV_8UC1);
		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				if (src.at<uchar>(i, j) == 0 && labels.at<uchar>(i, j) == 0) {
					label++;
					queue<pair<int, int>> Q;
					labels.at<uchar>(i, j) = label;
					Q.push(pair<int, int>(i, j));
					while (!Q.empty()) {
						pair<int, int> q = Q.front();
						Q.pop();
						int qi = q.first;
						int qj = q.second;
						if (neigh == 8) {
							for (int ni = i - 1; ni < i + 1; ni++) {
								for (int nj = j - 1; ni < j + 1 && isInside(src, ni, nj); nj++) {
									if (labels.at<uchar>(ni, nj) == 0 && src.at<uchar>(ni, nj) == 0) {
										labels.at<uchar>(ni, nj) = label;
										Q.push(pair<int, int>(ni, nj));
									}
								}
							}
						}
						else
							if (neigh == 4) {
								if (labels.at<uchar>(i + 1, j) == 0 && src.at<uchar>(i + 1, j) == 0) {
									labels.at<uchar>(i + 1, j) = label;
									Q.push(pair<int, int>(i + 1, j));
								}
								if (labels.at<uchar>(i - 1, j) == 0 && src.at<uchar>(i - 1, j) == 0) {
									labels.at<uchar>(i - 1, j) = label;
									Q.push(pair<int, int>(i - 1, j));
								}
								if (labels.at<uchar>(i, j + 1) == 0 && src.at<uchar>(i, j + 1) == 0) {
									labels.at<uchar>(i, j + 1) = label;
									Q.push(pair<int, int>(i, j + 1));
								}
								if (labels.at<uchar>(i, j - 1) == 0 && src.at<uchar>(i, j - 1) == 0) {
									labels.at<uchar>(i, j - 1) = label;
									Q.push(pair<int, int>(i, j - 1));
								}
							}
					}
				}
			}
		}
		Mat src8N = Mat(height, width, CV_8UC3);
		default_random_engine gen;
		uniform_int_distribution<int> d(0, 255);
		Vec3b colors[1000];
		for (int i = 0; i < 1000; i++) {
			colors[i] = Vec3b(d(gen), d(gen), d(gen));
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				src8N.at<Vec3b>(i, j) = colors[(int)labels.at<uchar>(i, j)];
			}
		}
		imshow("input image", src8N);
		waitKey();
	}
}


void testLab6Problema1(int vec)
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;

		short dir = 7;
		if (vec == 4) {
			dir = 0;
		}
		short cod[] = { 0 };
		int k = 0;
		//            0   1   2   3   4   5   6   7
		int di[] = { +0, -1, -1, -1, +0, +1, +1, +1 };
		int dj[] = { +1, +1, +0, -1, -1, -1, +0, +1 };

		int startI = -1;
		int startJ = -1;
		int firstFound = 0;
		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				uchar currentPixel = src.at<uchar>(i, j);
				if (currentPixel == 0 && firstFound != 1) {
					startI = i;
					startJ = j;
					firstFound = 1;
				}
			}
		}
		cod[k++] = dir;
		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				uchar currentPixel = src.at<uchar>(i, j);
				if (currentPixel == 0) {
					if (vec == 8) {
						if (dir % 2 == 0) {
							dir = (dir + 7) % 8;
						}
						else {
							dir = (dir + 6) % 8;
						}
						for (int l = 0; l < 8; l++) {
							uchar neighPixel = src.at<uchar>(i + di[(dir + l) % 8], j + dj[(dir + l) % 8]);
							if (neighPixel == currentPixel) {

							}
						}
					}
				}
				else if (vec == 4) {

				}
			}

		}
		imshow("input image", src);
		waitKey();
	}
}
Mat dilatare(Mat src) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat(height, width, CV_8UC1, Scalar(255));
	for (int i = 1; i < src.rows - 1; i++) {
		for (int j = 1; j < src.cols - 1; j++) {
			uchar currentPixel = src.at<uchar>(i, j);
			if (currentPixel == 0) {
				for (int ni = i - 1; ni <= i + 1; ni++) {
					for (int nj = j - 1; nj <= j + 1; nj++) {
						dst.at<uchar>(ni, nj) = 0;
					}
				}
			}
		}
	}
	return dst;
}

Mat eroziune(Mat src) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat(height, width, CV_8UC1, Scalar(255));

	for (int i = 1; i < src.rows - 1; i++) {
		for (int j = 1; j < src.cols - 1; j++) {
			uchar currentPixel = src.at<uchar>(i, j);
			if (currentPixel == 0) {
				bool shouldBreak = false;
				for (int ni = i - 1; ni <= i + 1; ni++) {
					for (int nj = j - 1; nj <= j + 1; nj++) {
						if (src.at<uchar>(ni, nj) == 255) {
							shouldBreak = true;
							break;
						}
					}
					if (shouldBreak)
						break;
				}
				if (!shouldBreak)
					dst.at<uchar>(i, j) = 0;
			}
		}
	}
	return dst;
}

Mat deschidere(Mat src) {
	return dilatare(eroziune(src));
}
Mat inchidere(Mat src) {
	return eroziune(dilatare(src));
}
void testLab7Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int n;
		cin >> n;

		Mat dilatare1Mat = dilatare(src);
		Mat eroziune1Mat = eroziune(src);
		Mat inchidere1Mat = inchidere(src);
		Mat deschidere1Mat = deschidere(src);


		Mat dilatareNMat = src;
		Mat eroziuneNMat = src;
		Mat inchidereNMat = src;
		Mat deschidereNMat = src;

		while (n > 0) {
			dilatareNMat = dilatare(dilatareNMat);
			eroziuneNMat = eroziune(eroziuneNMat);
			inchidereNMat = inchidere(inchidereNMat);
			deschidereNMat = deschidere(deschidereNMat);
			n--;
		}
		imshow("input image", src);

		imshow("dilatare 1 image", dilatare1Mat);
		imshow("eroziune 1 image", eroziune1Mat);
		imshow("inchidere 1 image", inchidere1Mat);
		imshow("deschidere 1 image", deschidere1Mat);

		imshow("dilatare N image", dilatareNMat);
		imshow("eroziune N image", eroziuneNMat);
		imshow("inchidere N image", inchidereNMat);
		imshow("deschidere N image", deschidereNMat);

		waitKey();
	}
}
void umplere(int event, int x, int y, int flags, void* param) {
	if (event == EVENT_LBUTTONDOWN) {
		Mat src = *((Mat*)param);
		uchar selectedPixel = src.at<uchar>(y, x);
		if (selectedPixel == 0) return;
		int height = src.rows;
		int width = src.cols;
		Mat matUmplere = Mat(height, width, CV_8UC1, Scalar(255));
		matUmplere.at<uchar>(y, x) = 0;
		Mat temp = matUmplere.clone();
		bool modified = false;
		do {
			modified = false;
			Mat matDilatare = dilatare(temp);
			matUmplere = temp.clone();
			for (int i = 0; i < src.rows; i++) {
				for (int j = 0; j < src.cols; j++) {
					uchar srcPixel = src.at<uchar>(i, j);
					uchar dilaarePixel = matDilatare.at<uchar>(i, j);
					if (dilaarePixel == 0 && srcPixel == 255) {
						temp.at<uchar>(i, j) = 0;
						modified = true;
					}
				}
			}
		} while (modified);
		imshow("Umplere", matUmplere);
	}
}
void testLab7Problema2()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat matEroziune = eroziune(src);
		Mat matContur = Mat(height, width, CV_8UC1, Scalar(255));

		for (int i = 0; i < src.rows; i++) {
			for (int j = 0; j < src.cols; j++) {
				uchar srcPixel = src.at<uchar>(i, j);
				uchar eroziunePixel = matEroziune.at<uchar>(i, j);
				if (srcPixel == 0 && eroziunePixel == 255) {
					matContur.at<uchar>(i, j) = 0;
				}
			}
		}
		imshow("input image", src);

		imshow("Contur", matContur);

		setMouseCallback("Contur", umplere, &matContur);

		waitKey();
		destroyAllWindows();
	}
}
int* calculHistograma(Mat src) {
	int height = src.rows;
	int width = src.cols;
	int* hist = (int*)calloc(256, sizeof(int));
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			hist[src.at<uchar>(i, j)] += 1;
		}
	}
	return hist;
}
float calculMedie(int* hist, int j, int k, int N) {
	float med = 0.0f;
	for (int i = j; i <= k; i++) {
		med += (i * hist[i]);
	}
	med /= N;
	return med;
}

float calculDeviatie(int* hist, float med, int N) {
	float dev = 0.0f;
	for (int i = 0; i < 256; i++) {
		dev += (((i - med) * (i - med)) * hist[i]);
	}
	dev /= N;
	dev = sqrt(dev);
	return dev;
}
int calculHistogramaCumulativaAtK(int* hist, int k) {
	int histCum = 0;
	for (int i = 0; i < k; i++) {
		histCum += hist[i];
	}
	return histCum;
}
int* calculHistogramaCumulativa(int* hist) {
	int* histCum = (int*)calloc(256, sizeof(int));
	for (int i = 0; i < 256; i++) {
		histCum[i] = calculHistogramaCumulativaAtK(hist, i);
	}
	return histCum;
}




void testLab8Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int* hist = calculHistograma(src);
		float med = calculMedie(hist, 0, 255, height * width);
		float dev = calculDeviatie(hist, med, height * width);
		int* histCum = calculHistogramaCumulativa(hist);
		imshow("input image", src);
		showHistogram("histograma ", hist, 256, 200);
		showHistogram("histograma cumulativa", histCum, 256, 200);
		printf("%.2f ", med);
		printf("%.2f ", dev);
		waitKey();
		destroyAllWindows();
	}
}
int calculMinim(int* hist, int k) {
	int min = 0;
	for (int i = k; i < 256; i++) {
		if (hist[i] != 0)
			min = i;
	}
	return min;
}

int calculMaxim(int* hist, int k) {
	int max = 0;
	for (int i = 255; i >= k; i--) {
		if (hist[i] != 0)
			max = i;
	}
	return max;
}

int calculN(int* hist, int j, int k) {
	int N = 0;
	for (int i = j; i <= k; i++) {
		N += hist[i];
	}
	return N;
}

void testLab8Problema2()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = Mat::zeros(height, width, CV_8UC1);
		int* hist = calculHistograma(src);
		int min = calculMinim(hist, 0);
		int max = calculMaxim(hist, 0);
		float prag;
		cin >> prag;
		float* T = (float*)calloc(1000, sizeof(float));
		int i = 0;
		while (true) {
			if (i == 0) {
				T[i++] = (min + max) / 2.0f;
				continue;
			}
			int Tkminus1 = floor(T[i - 1]);
			int N1 = calculN(hist, 0, Tkminus1);
			float med1 = calculMedie(hist, 0, Tkminus1, N1);
			int N2 = calculN(hist, Tkminus1 + 1, 255);
			float med2 = calculMedie(hist, Tkminus1 + 1, 255, N2);
			T[i] = (med1 + med2) / 2.0f;
			if (abs(T[i] - T[i - 1]) < prag)
				break;
			i++;
		}
		float Tfinal = T[i - 1];
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (src.at<uchar>(i, j) < Tfinal) {
					dst.at<uchar>(i, j) = 0;
				}
				else {
					dst.at<uchar>(i, j) = 255;
				}
			}
		}
		printf("%f", Tfinal);
		imshow("input image", src);
		imshow("input image st", dst);
		waitKey();
		destroyAllWindows();
	}
}

Mat calculIdentitate(Mat src) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);

	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			uchar currentPixel = src.at<uchar>(i, j);
			dst.at<uchar>(i, j) = currentPixel;
		}
	}
	return dst;
}
Mat calculNegativ(Mat src) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);

	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			uchar currentPixel = src.at<uchar>(i, j);
			dst.at<uchar>(i, j) = 255 - currentPixel;
		}
	}
	return dst;
}

Mat calculModificareContrast(Mat src, int iOutMin, int iOutMax) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	int* hist = calculHistograma(src);
	int min = calculMinim(hist, 0);
	int max = calculMaxim(hist, 0);
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			uchar currentPixel = src.at<uchar>(i, j);
			uchar newVal = iOutMin + (currentPixel - min) * ((iOutMax - iOutMin) / (max - min));

			if (newVal < 0) {
				newVal = 0;
			}
			else {
				if (newVal > 255) {
					newVal = 255;
				}
			}
			dst.at<uchar>(i, j) = newVal;
		}
	}
	return dst;
}
Mat calculCorectieGamma(Mat src, float gamma) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			uchar currentPixel = src.at<uchar>(i, j);
			dst.at<uchar>(i, j) = 255 * pow((currentPixel / 255), gamma);
		}
	}
	return dst;
}

Mat calculModificareLuminozitate(Mat src, int offset) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			uchar currentPixel = src.at<uchar>(i, j);
			uchar newVal;
			if (offset + currentPixel < 0) {
				newVal = 0;
			}
			else if (offset + currentPixel > 255) {
				newVal = 255;
			}
			else {
				newVal = offset + currentPixel;
			}
			dst.at<uchar>(i, j) = newVal;
		}
	}
	return dst;
}

void testLab8Problema3()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;

		int iOutMin, iOutMax;
		float gamma, iModificat;
		cin >> iOutMin >> iOutMax >> gamma >> iModificat;
		Mat identitate = calculIdentitate(src);
		Mat negativ = calculNegativ(src);
		Mat modificareContrast = calculModificareContrast(src, iOutMin, iOutMax);
		Mat corectieGamma = calculCorectieGamma(src, gamma);
		Mat modificareLuminozitate = calculModificareLuminozitate(src, iModificat);
		imshow("original", src);
		showHistogram("histograma original", calculHistograma(src), 256, 200);
		imshow("negativ", negativ);
		showHistogram("histograma negativ", calculHistograma(negativ), 256, 200);
		imshow("modificare contrast", modificareContrast);
		showHistogram("histograma modificare contrast", calculHistograma(modificareContrast), 256, 200);
		imshow("corectie gamma", corectieGamma);
		showHistogram("corectie gamma", calculHistograma(corectieGamma), 256, 200);
		imshow("modificare luminozitate", modificareLuminozitate);
		showHistogram("histograma modificare luminozitate", calculHistograma(modificareLuminozitate), 256, 200);
		waitKey();
		destroyAllWindows();
	}
}


void testLab9Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname)) {
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		const int N = 3;
		Mat dstFTJA = Mat::zeros(height, width, CV_8UC1);

		float FTJA[N][N] = { 0.0f };
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				FTJA[i][j] = 1.0f / (N * N);
			}
		}


		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sum = 0;

				for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
					for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
						uchar currentPixel = src.at<uchar>(ni, nj);
						sum += currentPixel * FTJA[ni - i + N / 2][nj - j + N / 2];
					}
				}
				dstFTJA.at<uchar>(i, j) = sum;

			}
		}

		Mat dstFTJGaussian = Mat::zeros(height, width, CV_8UC1);
		float FTJGaussian[N][N] = { {1.0f, 2.0f, 1.0f} };
		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sum = 0;
				for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
					for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
						uchar currentPixel = src.at<uchar>(ni, nj);
						sum += currentPixel * FTJGaussian[ni - i + N / 2][nj - j + N / 2];
					}
				}
				dstFTJGaussian.at<uchar>(i, j) = sum;

			}
		}


		imshow("sursa", src);
		imshow("FTJ Gaussian", dstFTJGaussian);
		waitKey();
		destroyAllWindows();
	}
}

Mat calculMatFiltrata(Mat src, const int N) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	for (int i = N / 2; i < height - N / 2; i++)
	{
		for (int j = N / 2; j < width - N / 2; j++)
		{
			const int vecN = N * N;
			vector<uchar> vec;
			uchar k = 0;
			for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
				for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
					uchar currentPixel = src.at<uchar>(ni, nj);
					vec.push_back(currentPixel);
				}
			}
			sort(vec.begin(), vec.end());
			dst.at<uchar>(i, j) = vec[vecN / 2];
		}
	}
	return dst;
}

void testLab10Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname)) {
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		double t = (double)getTickCount();



		Mat srcFiltrata = calculMatFiltrata(src, N);
		t = ((double)getTickCount() - t) / getTickFrequency();
		printf("T = %.3f", t * 1000);


		imshow("sursa", src);
		imshow("sursa filtrata", srcFiltrata);
		waitKey();
		destroyAllWindows();
	}
}

float** creareFiltru(int x, int y)
{
	float** G = (float**)calloc(x, sizeof(float*));
	for (int i = 0; i < x; i++) {
		G[i] = (float*)calloc(y, sizeof(float));
	}
	return G;
}
void testLab10Problema2()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname)) {
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat dst = Mat::zeros(height, width, CV_8UC1);
		const int vecN = N * N;
		float** G = creareFiltru(N, N);
		float eps = N / 6.0f;
		int x = N / 2;
		int y = N / 2;

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				G[i][j] = (exp(-((i - x) * (i - x) + (j - y) * (j - y)) / (2 * eps * eps)) * 1.0f) / (2 * PI * eps * eps);
			}
		}


		float sumG = 0.0f;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				sumG += G[i][j];
			}
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				G[i][j] /= sumG;
				cout << G[i][j] << " ";
			}
		}


		double t = (double)getTickCount();
		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sum = 0;

				for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
					for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
						uchar currentPixel = src.at<uchar>(ni, nj);
						sum += currentPixel * G[ni - i + N / 2][nj - j + N / 2];
					}
				}
				dst.at<uchar>(i, j) = sum;

			}
		}
		t = ((double)getTickCount() - t) / getTickFrequency();
		printf("T = %.3f", t * 1000);

		Mat dst1 = Mat::zeros(height, width, CV_8UC1);
		Mat dst2 = Mat::zeros(height, width, CV_8UC1);

		float* G1 = (float*)calloc(N, sizeof(float));
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				G1[i] = (exp(-((i - x) * (i - x)) / (2 * eps * eps)) * 1.0f) / (sqrt(2 * PI) * eps);
			}
		}


		float sumG1 = 0.0f;
		for (int i = 0; i < N; i++) {
			sumG1 += G1[i];
		}

		for (int i = 0; i < N; i++) {
			G1[i] /= sumG1;
		}


		double t1 = (double)getTickCount();
		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sum = 0;

				for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
					uchar currentPixel = src.at<uchar>(ni, j);
					sum += currentPixel * G1[ni - i + N / 2];
				}
				dst1.at<uchar>(i, j) = sum;

			}
		}


		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sum = 0;
				for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
					uchar currentPixel = dst1.at<uchar>(i, nj);
					sum += currentPixel * G1[nj - j + N / 2];
				}
				dst2.at<uchar>(i, j) = sum;

			}
		}


		t1 = ((double)getTickCount() - t1) / getTickFrequency();
		printf("T1 = %.3f/n", t1 * 1000);






		imshow("sursa", src);
		imshow("sursa filtrata gaussian 2D", dst);
		imshow("sursa filtrata gaussian 1D", dst2);

		waitKey();
		destroyAllWindows();
	}
}



void testLab11Problema1()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname)) {
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		const int N = 3;
		Mat prewittMatX = Mat::zeros(height, width, CV_32FC1);
		Mat prewittMatY = Mat::zeros(height, width, CV_32FC1);


		float prewittX[N][N] =
		{
			{-1, 0, 1},
			{-1, 0, 1},
			{-1, 0, 1}
		};

		float prewittY[N][N] =
		{
			{ 1,  1,  1},
			{ 0,  0,  0},
			{-1, -1, -1}
		};
		Mat prewittMatXAfisare = Mat::zeros(height, width, CV_8UC1);
		Mat prewittMatYAfisare = Mat::zeros(height, width, CV_8UC1);

		for (int i = N / 2; i < height - N / 2; i++)
		{
			for (int j = N / 2; j < width - N / 2; j++)
			{
				float sumX = 0;
				float sumY = 0;
				for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
					for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
						uchar currentPixel = src.at<uchar>(ni, nj);
						sumX += currentPixel * prewittX[ni - i + N / 2][nj - j + N / 2];
						sumY += currentPixel * prewittY[ni - i + N / 2][nj - j + N / 2];
					}
				}
				prewittMatX.at<float>(i, j) = sumX;
				prewittMatY.at<float>(i, j) = sumY;
				prewittMatXAfisare.at<uchar>(i, j) = 1.0f / 6 * sumX + 127;
				prewittMatYAfisare.at<uchar>(i, j) = 1.0f / 6 * sumY + 127;

			}
		}


		Mat mag = Mat::zeros(height, width, CV_32FC1);
		Mat theta = Mat::zeros(height, width, CV_32FC1);

		for (int i = 1; i < height - 1; i++)
		{
			for (int j = 0; j < width; j++)
			{
				mag.at<float>(i, j) = (float)sqrt(prewittMatX.at<float>(i, j) * prewittMatX.at<float>(i, j) + prewittMatY.at<float>(i, j) * prewittMatY.at<float>(i, j));
				theta.at<float>(i, j) = (float)atan2(prewittMatY.at<float>(i, j), prewittMatX.at<float>(i, j));
				if (theta.at<float>(i, j) < 0) {
					theta.at<float>(i, j) += 2 * PI;
				}
			}
		}

		imshow("sursa", src);
		imshow("Prewitt Mat X Afisare", prewittMatXAfisare);
		imshow("Prewitt Mat Y Afisare", prewittMatYAfisare);
		imshow("Mag Afisare", mag);
		imshow("Theta Afisare", theta);
		waitKey();
		destroyAllWindows();
	}
}




void MyCallBackFunc(int event, int x, int y, int flags, void* param)
{
	//More examples: http://opencvexamples.blogspot.com/2014/01/detect-mouse-clicks-and-moves-on-image.html
	Mat* src = (Mat*)param;
	if (event == EVENT_LBUTTONDOWN)
	{
		printf("Pos(x,y): %d,%d  Color(RGB): %d,%d,%d\n",
			x, y,
			(int)(*src).at<Vec3b>(y, x)[2],
			(int)(*src).at<Vec3b>(y, x)[1],
			(int)(*src).at<Vec3b>(y, x)[0]);
	}
}

void testMouseClick()
{
	Mat src;
	// Read image from file 
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		src = imread(fname);
		//Create a window
		namedWindow("My Window", 1);

		//set the callback function for any mouse event
		setMouseCallback("My Window", MyCallBackFunc, &src);

		//show the image
		imshow("My Window", src);

		// Wait until user press some key
		waitKey(0);
	}
}
int main()
{
	cv::utils::logging::setLogLevel(cv::utils::logging::LOG_LEVEL_FATAL);
	projectPath = _wgetcwd(0, 0);

	int op;
	do
	{
		system("cls");
		destroyAllWindows();
		printf("Menu:\n");
		printf(" 1 - Open image\n");
		printf(" 2 - Open BMP images from folder\n");
		printf(" 3 - Image negative\n");
		printf(" 4 - Image negative (fast)\n");
		printf(" 5 - BGR->Gray\n");
		printf(" 6 - BGR->Gray (fast, save result to disk) \n");
		printf(" 7 - BGR->HSV\n");
		printf(" 8 - Resize image\n");
		printf(" 9 - Canny edge detection\n");
		printf(" 10 - Edges in a video sequence\n");
		printf(" 11 - Snap frame from live video\n");
		printf(" 12 - Mouse callback demo\n");
		printf("-------------------------\n");
		printf(" 13 - Lab1 Problema 3\n");
		printf(" 14 - Lab1 Problema 4\n");
		printf(" 15 - Lab1 Problema 5\n");
		printf(" 16 - Lab1 Problema 6\n");
		printf("-------------------------\n");
		printf(" 17 - Lab2 Problema 1\n");
		printf(" 18 - Lab2 Problema 2\n");
		printf(" 19 - Lab2 Problema 3\n");
		printf(" 20 - Lab2 Problema 4\n");
		printf(" 21 - Lab2 Problema 5\n");
		printf("-------------------------\n");
		printf(" 22 - Lab3 Problema 1\n");
		printf(" 23 - Lab3 Problema 2\n");
		printf("-------------------------\n");
		printf(" 24 - Lab4 Problema 1\n");
		printf("-------------------------\n");
		printf(" 25 - Lab5 Problema 1\n");
		printf("-------------------------\n");
		printf(" 26 - Lab6 Problema 1\n");
		printf("-------------------------\n");
		printf(" 27 - Lab7 Problema 1\n");
		printf(" 28 - Lab7 Problema 2\n");
		printf("-------------------------\n");
		printf(" 29 - Lab8 Problema 1\n");
		printf(" 30 - Lab8 Problema 2\n");
		printf(" 32 - Lab8 Problema 3\n");
		printf("-------------------------\n");
		printf(" 33 - Lab9 Problema 1\n");
		printf("-------------------------\n");
		printf(" 34 - Lab10 Problema 1\n");
		printf(" 35 - Lab10 Problema 2\n");
		printf("-------------------------\n");
		printf(" 36 - Lab11 Problema 1\n");
		printf(" 37 - Lab11 Problema 2\n");


		printf(" 0 - Exit\n\n");
		printf("Option: ");
		scanf("%d", &op);
		switch (op)
		{
		case 1:
			testOpenImage();
			break;
		case 2:
			testOpenImagesFld();
			break;
		case 3:
			testNegativeImage();
			break;
		case 4:
			testNegativeImageFast();
			break;
		case 5:
			testColor2Gray();
			break;
		case 6:
			testImageOpenAndSave();
			break;
		case 7:
			testBGR2HSV();
			break;
		case 8:
			testResize();
			break;
		case 9:
			testCanny();
			break;
		case 10:
			testVideoSequence();
			break;
		case 11:
			testSnap();
			break;
		case 12:
			testMouseClick();
			break;
		case 13:
			testLab1Problema3();
			break;
		case 14:
			testLab1Problema4();
			break;
		case 15:
			testLab1Problema5();
			break;
		case 16:
			testLab1Problema6();
			break;
		case 17:
			testLab2Problema1();
			break;
		case 18:
			testLab2Problema2();
			break;
		case 19:
			testLab2Problema3();
			break;
		case 20:
			testLab2Problema4();
			break;
		case 21:
			testLab2Problema5();
			break;
		case 22:
			testLab3Problema1();
			break;
		case 23:
			testLab3Problema2();
			break;
		case 24:
			testLab4Problema1();
			break;
		case 25:
			testLab5Problema1(8);
			break;
		case 26:
			testLab6Problema1(4);
			break;
		case 27:
			testLab7Problema1();
			break;
		case 28:
			testLab7Problema2();
			break;
		case 29:
			testLab8Problema1();
			break;
		case 30:
			testLab8Problema2();
			break;
		case 31:
			testLab8Problema3();
			break;
		case 33:
			testLab9Problema1();
			break;
		case 34:
			testLab10Problema1();
			break;
		case 35:
			testLab10Problema2();
			break;
		case 36:
			testLab11Problema1();
			break;
		}
	} while (op != 0);
	return 0;
}