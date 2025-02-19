// OpenCVApplication.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "common.h"
#include <opencv2/core/utils/logger.hpp>
using namespace std;
wchar_t* projectPath;


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

int* calculHistograma(Mat src) {
	int height = src.rows;
	int width = src.cols;
	int* hist = (int*)calloc(257, sizeof(int));
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
	int sum = 0;
	for (int i = j; i <= k; i++) {
		sum += (i * hist[i]);
	}
	med = (float)sum / N;
	return med;
}

int calculMinim(int* hist, int k) {
	int min = 255;
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

float calculDeviatie(int* hist, float med, int N) {
	float dev = 0.0f;
	for (int i = 0; i < 256; i++) {
		dev += (((i - med) * (i - med)) * hist[i]);
	}
	dev /= N;
	dev = sqrt(dev);
	return dev;
}
Mat calculMatBinarizata(Mat src, float prag) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	int* hist = calculHistograma(src);
	int min = calculMinim(hist, 0);
	int max = calculMaxim(hist, 0);
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
	float Tfinal = T[i];
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
	cout << "Threshold: " << Tfinal << endl;
	return dst;
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

void binarizare()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = calculMatBinarizata(src, 0.1);
		int* hist = calculHistograma(src);
		imshow("sursa", src);
		imshow("sursa binarizata", dst);
		showHistogram("hist sursa", hist, 256, 200);
		waitKey();
		destroyAllWindows();
	}
}

void doarBinarizare()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat dst = calculMatBinarizata(src, 0.1);
		Mat dst2 = calculMatFiltrata(dst, N);
		Mat dst3 = calculMatFiltrata(src, N);
		int* hist = calculHistograma(src);
		imshow("sursa", src);
		imshow("Imagine binarizata", dst);
		showHistogram("hist sursa", hist, 256, 200);

		waitKey();
		destroyAllWindows();
	}
}


void binarizareSiFiltrare()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat dst = calculMatBinarizata(src, 0.1);
		Mat dst2 = calculMatFiltrata(dst, N);
		int* hist = calculHistograma(src);

		imshow("sursa", src);
		imshow("Imagine binarizata", dst);
		imshow("Imagine binarizata si filtrata", dst2);
		showHistogram("hist sursa", hist, 256, 200);

		waitKey();
		destroyAllWindows();
	}
}

void doarFiltrare()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat dst = calculMatFiltrata(src, N);
		imshow("Filtrare sursa", dst);
		waitKey();
		destroyAllWindows();
	}
}

int** kernel(int n, int p) {
	int** dst = (int**)calloc(n + 1, sizeof(int*));
	for (int i = 0; i < n; i++) {
		dst[i] = (int*)calloc(n + 1, sizeof(int));
	}

	int valoare = -1;
	for (int i = 0; i < (n + 1) / 2; i++) {
		for (int j = i; j < n - i; j++) {
			dst[i][j] = valoare;
			dst[j][n - i - 1] = valoare;
			dst[n - i - 1][n - j - 1] = valoare;
			dst[n - j - 1][i] = valoare;
		}
		valoare -= 1;
	}

	int sum = 0;
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++) {
			sum += abs(dst[i][j]);
		}
	}

	dst[n / 2][n / 2] = sum + dst[n / 2][n / 2] + p;

	return dst;
}
Mat aplicareKernel(Mat src, int** kernelMatrix, int N) {
	int height = src.rows;
	int width = src.cols;
	Mat dst = Mat::zeros(height, width, CV_8UC1);
	for (int i = N / 2; i < height - N / 2; i++)
	{
		for (int j = N / 2; j < width - N / 2; j++)
		{
			float sumFiltered = 0;
			for (int ni = i - N / 2; ni <= i + N / 2; ni++) {
				for (int nj = j - N / 2; nj <= j + N / 2; nj++) {
					uchar currentPixel = src.at<uchar>(ni, nj);
					sumFiltered += currentPixel * kernelMatrix[ni - i + N / 2][nj - j + N / 2];
				}
			}
			dst.at<uchar>(i, j) = saturate_cast<uchar>(sumFiltered);
		}
	}
	return dst;
}
void afisareMatrice(int** mat, int n) {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			cout << mat[i][j] << " ";
		}
		cout << endl;
	}
	cout << endl;
}

Mat calculMatAdaptata(Mat src, int N)
{
	int height = src.rows;
	int width = src.cols;
	float* cors = (float*)calloc(11, sizeof(float));

	Mat* adaptedMat = (Mat*)calloc(11, sizeof(Mat));

	int** adaptedHist = (int**)calloc(11, sizeof(int*));

	for (int i = 0; i < 11; i++) {
		adaptedHist[i] = (int*)calloc(1, sizeof(int));
	}

	int* histSrc = calculHistograma(src);


	float max = -FLT_MAX;
	int maxIndex = -1;
	for (int p = 1; p <= 10; p++)
	{
		int** kernelMatrix = kernel(N, p);
		Mat srcAdaptata = aplicareKernel(src, kernelMatrix, N);
		int sum = 0;

		int* histSrcAdaptata = calculHistograma(srcAdaptata);

		float medieSrc = calculMedie(histSrc, 0, 255, width * height);
		float medieSrcAdaptata = calculMedie(histSrcAdaptata, 0, 255, width * height);


		for (int i = N / 2; i < height - N / 2; i++) {
			for (int j = N / 2; j < width - N / 2; j++) {
				uchar srcPixelCurent = src.at<uchar>(i, j);
				uchar srcAdaptataPixelCurent = srcAdaptata.at<uchar>(i, j);

				sum += ((srcPixelCurent - medieSrc) * (srcAdaptataPixelCurent - medieSrcAdaptata));
			}

		}
		sum /= (height * width);
		float devSrc = calculDeviatie(histSrc, medieSrc, width * height);
		float devSrcAdaptata = calculDeviatie(histSrcAdaptata, medieSrcAdaptata, width * height);

		float cor = sum / (devSrc * devSrcAdaptata);
		cors[p] = cor;
		adaptedMat[p] = srcAdaptata;
		adaptedHist[p] = histSrcAdaptata;

		if (cor > max) {
			max = cor;
			maxIndex = p;
		}
	}
	cout << endl << "Lista factori corelatie: ";
	for (int i = 1; i <= 10; i++) {
		cout << cors[i] << " ";
	}
	cout << endl;

	return adaptedMat[maxIndex];
}


void Adaptare() {
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat matAdaptata = calculMatAdaptata(src, N);
		int* histSrcAdaptata = calculHistograma(matAdaptata);
		int* histSrc = calculHistograma(src);


		imshow("Imagine sursa", src);
		imshow("Imagine adaptata", matAdaptata);
		showHistogram("hist sursa", histSrc, 256, 200);
		showHistogram("hist adaptata", histSrcAdaptata, 256, 200);
		waitKey();
		destroyAllWindows();
	}
}



void AdaptareBinarizareFiltrare() {
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, IMREAD_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		int N;
		cin >> N;
		Mat matAdaptata = calculMatAdaptata(src, N);
		int* histSrcAdaptata = calculHistograma(matAdaptata);
		int* histSrc = calculHistograma(src);

		Mat matAdaptataBinarizata = calculMatBinarizata(matAdaptata, 0.1);
		Mat matAdaptataBinarizataFiltrata = calculMatFiltrata(matAdaptataBinarizata, N);
		Mat matBinarizata = calculMatBinarizata(src, 0.1);


		imshow("Imagine sursa", src);
		imshow("Imagine adaptata", matAdaptata);
		imshow("Imagine binarizata(sursa)", matBinarizata);
		imshow("Imagine finala", matAdaptataBinarizataFiltrata);

		showHistogram("hist sursa", histSrc, 256, 200);
		showHistogram("hist adaptata", histSrcAdaptata, 256, 200);
		waitKey();
		destroyAllWindows();
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
		printf(" 1 - binarizre + filtrare\n");
		printf(" 2 - binarizre\n");
		printf(" 3 - filtrare\n");
		printf(" 4 - adaptare\n");
		printf(" 5 - adaptare + binarizare + filtrare\n");
		printf(" 0 - Exit\n\n");
		printf("Option: ");
		scanf("%d", &op);
		switch (op)
		{
		case 1:
			binarizareSiFiltrare();
			break;
		case 2:
			doarBinarizare();
			break;
		case 3:
			doarFiltrare();
			break;
		case 4:
			Adaptare();
			break;
		case 5:
			AdaptareBinarizareFiltrare();
			break;
		}
	} while (op != 0);
	return 0;
}