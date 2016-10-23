#include "Convex_Hull.h"
#include <iostream>
#include <stdlib.h>
#include <time.h>
#include <climits>
#include <omp.h>
#include <fstream>


using namespace std;

double areaOfPolygon(vector<Point_t> &polygon) {
	double area = 0;
	for (size_t i = 0; i < polygon.size(); ++i) {
		int module = polygon.size();
		area += (polygon[i].first + polygon[(i + 1) % module].first)*(polygon[i].second - polygon[(i + 1) % module].second);
	}
	return abs(area / 2);
}

double areaOfPolygonParallel(vector<Point_t> &polygon) {
	double area = 0;
#pragma omp parallel shared(area, polygon)
	for (int i = 0; i < polygon.size(); ++i) {
		int module = polygon.size();
		area += (polygon[i].first + polygon[(i + 1) % module].first)*(polygon[i].second - polygon[(i + 1) % module].second);
	}
	return abs(area / 2);
}

int modulus(int base, int number) {
	if (number >= 0) {
		return number % base;
	}
	else {
		return base + number;
	}
}

double solveParallel(vector<Point_t> &polygon, Convex_Hull &convexHull) {
	double area = areaOfPolygon(convexHull.getHull());
	double maxarea = 0;
	double temparea = 0;
	auto indexes = convexHull.getIndexesVector();
	int base = indexes.size();
#pragma omp parallel for private(temparea) shared(area, maxarea, base, indexes, polygon, convexHull)
	for (int i = 1; i < indexes.size(); ++i) {
		double trainglearea = areaOfPolygonParallel(vector<Point_t>({ polygon[indexes[i - 1]], polygon[indexes[i]], polygon[indexes[modulus(base,(i + 1))]] }));
		vector<Point_t> newpolygon;

		if (i + 2 > indexes.size()) {
			for (int j = 0; i <= indexes[0]; ++j) {
				if (j == indexes[i]) continue;
				newpolygon.push_back(polygon[j]);
			}
		}
		else {
			for (int j = indexes[i]; j <= indexes[i + 1]; ++j) {
				if (j == indexes[i]) continue;
				newpolygon.push_back(polygon[j]);
			}
		}
		for (int j = indexes[i - 1]; j <= indexes[i]; ++j) {
			if (j == indexes[i]) continue;
			newpolygon.push_back(polygon[j]);
		}

		auto newpolygonArea = areaOfPolygonParallel(Convex_Hull(newpolygon, 2).getHull());
		auto minusedArea = area - trainglearea + newpolygonArea;
		if (minusedArea > maxarea) {
			maxarea = minusedArea;
		}
	}
	return maxarea;
}

double solve(vector<Point_t> &polygon, Convex_Hull &convexHull) {
	double area = areaOfPolygon(convexHull.getHull());
	double maxarea = 0;
	double temparea = 0;
	auto indexes = convexHull.getIndexesVector();
	int base = indexes.size();
	for (int i = 1; i < indexes.size(); ++i) {
		double trainglearea = areaOfPolygon(vector<Point_t>({ polygon[indexes[i - 1]], polygon[indexes[i]], polygon[indexes[modulus(base,(i + 1))]] }));
		vector<Point_t	> newpolygon;

		if (i + 2 > indexes.size()) {
			for (int j = 0; i <= indexes[0]; ++j) {
				if (j == indexes[i]) continue;
				newpolygon.push_back(polygon[j]);
			}
		}
		else {
			for (int j = indexes[i] + 1; j <= indexes[i + 1]; ++j) {
				if (j == indexes[i]) continue;
				newpolygon.push_back(polygon[j]);
			}
		}
		for (int j = indexes[i - 1]; j < indexes[i]; ++j) {
			if (j == indexes[i]) continue;
			newpolygon.push_back(polygon[j]);
		}

		auto newpolygonArea = areaOfPolygon(Convex_Hull(newpolygon, 2).getHull());
		auto minusedArea = area - trainglearea + newpolygonArea;
		if (minusedArea > maxarea) {
			maxarea = minusedArea;
		}
	}
	return maxarea;
}

void printTestData(int n, ofstream &singleThreadFile, ofstream &parallelFile) {
	//	cout.precision(10);
		vector<Point_t> polygon;
		for (size_t i = 0; i < 100000; ++i) {
			//cin >> x >> y;
			polygon.push_back({ rand() % 10000000, rand() % 10000000 });
		}
		auto convexTime = omp_get_wtime();
		auto hull = Convex_Hull(polygon, 2);
		cout << "Building time: " << omp_get_wtime() - convexTime << endl;
		for (int i = 0; i < n; ++i) {
		auto time = omp_get_wtime();
		cout << solve(polygon, hull) << endl;
		auto SingleThreadTime = omp_get_wtime() - time;
		//cout << "SingleThreadTime: " << SingleThreadTime << endl;
		singleThreadFile << SingleThreadTime << endl;

		time = omp_get_wtime();
		cout << solveParallel(polygon, hull) << endl;
		auto ParallelTime = omp_get_wtime() - time;
		//cout << "ParallelTime: " << ParallelTime << endl;
		parallelFile << ParallelTime << endl;
		//cout << endl << endl << endl;
	}
}

//#define DEBUG

int main() {
#ifndef DEBUG
	omp_set_num_threads(4);
	srand(time(NULL));
	ofstream parallelOutput;
	ofstream singleThreadOuput;
	parallelOutput.open("parallelOutput4th.txt", fstream::app);
	singleThreadOuput.open("singleThreadOutput4th.txt", fstream::app);
	printTestData(100, singleThreadOuput, parallelOutput);
	singleThreadOuput.close();
	parallelOutput.close();
#endif

#ifdef DEBUG
	int n;
	int x, y;
	cin >> n;
	vector<Point_t> polygon;
	for (size_t i = 0; i < n; ++i) {
		cin >> x >> y;
		polygon.push_back({ x, y });
	}
	cin >> x;
	cout.setf(std::ios::fixed);
	cout.precision(2);
	auto ch = Convex_Hull(polygon, 2);
	cout << solve(polygon, ch) << endl;
#endif
	auto procs = omp_get_num_procs();
	auto nthreads = omp_get_num_threads();
	auto maxt = omp_get_max_threads();
	auto inpar = omp_in_parallel();
	auto dynamic = omp_get_dynamic();
	auto nested = omp_get_nested();

	// Print environment information 
	printf("Number of processors = %d\n", procs);
	printf("Number of threads = %d\n", nthreads);
	printf("Max threads = %d\n", maxt);
	printf("In parallel? = %d\n", inpar);
	printf("Dynamic threads enabled? = %d\n", dynamic);
	printf("Nested parallelism supported? = %d\n", nested);

}