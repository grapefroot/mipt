#pragma once
#include <vector>

typedef std::pair<int, int> Point_t;
typedef std::vector < Point_t > ConvexHull_t;

class Convex_Hull
{
public:
	Convex_Hull(ConvexHull_t &vectorOfPoints, int wayOfConstruction = 1);
	static Point_t pivotalPoint;
	ConvexHull_t getHull();
	std::vector<int> getIndexesVector();
	~Convex_Hull();


private:
	ConvexHull_t hull;
	std::vector<int> indexes;
	ConvexHull_t JarvisGiftWrapping(ConvexHull_t &vectorOfPoints);
	ConvexHull_t Graham(ConvexHull_t &vectorOfPoints);
	ConvexHull_t DAC(ConvexHull_t &vectorOfPoints);
	static bool POLAR_ORDER(Point_t first, Point_t second);
						 
						 
	// 0 - colinear		 
	// 1 - clockwize	 
	// -1 - ccw			 
	static int orientation(Point_t point_1, Point_t point_2, Point_t point_3);
};

