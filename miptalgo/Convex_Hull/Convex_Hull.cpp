#include "Convex_Hull.h"
#include <climits>
#include <algorithm>
#include <omp.h>

Point_t Convex_Hull::pivotalPoint = std::make_pair(0,0);

Convex_Hull::Convex_Hull(ConvexHull_t &vectorOfPoints, int wayOfConstruction)
{
	switch (wayOfConstruction) 
	{
	case 1: {hull = JarvisGiftWrapping(vectorOfPoints); break; }
		case 2: {hull = Graham(vectorOfPoints); break; }
		case 3: {hull = DAC(vectorOfPoints); break; }
	}
}
ConvexHull_t Convex_Hull::getHull() {
	return hull;
}


ConvexHull_t Convex_Hull::JarvisGiftWrapping(ConvexHull_t & vectorOfPoints)
{
	return ConvexHull_t();
}

std::vector<int> Convex_Hull::getIndexesVector()
{
	return indexes;
}

ConvexHull_t Convex_Hull::Graham(ConvexHull_t &vectorOfPoints) {
	ConvexHull_t answer;
	if (vectorOfPoints.size() < 3) {
		return vectorOfPoints;
	}
	//find most left point
	int leastY = 0;
	for (int i = 1; i < vectorOfPoints.size(); ++i) {
		if (vectorOfPoints[i].second < vectorOfPoints[leastY].second) {
			leastY = i;
		}
	}
	
	std::swap(vectorOfPoints[leastY], vectorOfPoints[0]);
	pivotalPoint = vectorOfPoints[0];
	std::sort(++vectorOfPoints.begin(), vectorOfPoints.end(), POLAR_ORDER);

	answer.push_back(vectorOfPoints[0]);
	indexes.push_back(0);
	answer.push_back(vectorOfPoints[1]);
	indexes.push_back(1);
	answer.push_back(vectorOfPoints[2]);
	indexes.push_back(2);

	for (int i = 3; i < vectorOfPoints.size(); ++i) {
		Point_t top = answer.back();
		int ind = indexes.back();
		answer.pop_back();
		indexes.pop_back();
		while (orientation(answer.back(), top, vectorOfPoints[i]) != -1) {
			top = answer.back();
			ind = indexes.back();
			answer.pop_back();
			indexes.pop_back();
		}
		answer.push_back(top);
		indexes.push_back(ind);
		answer.push_back(vectorOfPoints[i]);
		indexes.push_back(i);
	}
	return answer;
}

ConvexHull_t Convex_Hull::DAC(ConvexHull_t & vectorOfPoints)
{
	return ConvexHull_t();
}

double euclidDist(Point_t start, Point_t end) {
	int dx = (end.first - start.first);
	int dy = (end.second - start.second);
	return dx*dx + dy*dy;
}

bool Convex_Hull::POLAR_ORDER(Point_t first, Point_t second) {
	int position = orientation(pivotalPoint, first, second);
	if (position == 0) {
		return euclidDist(pivotalPoint, first) < euclidDist(pivotalPoint, second);
	}
	return (position == -1);
}

int Convex_Hull::orientation(Point_t start, Point_t end1, Point_t end2) {
	int ccw = (end1.first - start.first)*(end2.second - start.second) -
			  (end1.second - start.second)*(end2.first - start.first);
	if (ccw == 0)
		return 0;
	return (ccw > 0) ? -1 : 1;

}

Convex_Hull::~Convex_Hull()
{
}
