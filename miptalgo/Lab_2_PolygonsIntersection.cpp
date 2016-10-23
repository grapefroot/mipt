#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>

#define INF 10000

using namespace std;

typedef pair <float, float> point_t;
typedef vector <point_t> polygon_t;

point_t pivotalPoint = std::make_pair(0, 0);

double euclidDist(point_t start, point_t end) {
    int dx = (end.first - start.first);
    int dy = (end.second - start.second);
    return dx * dx + dy * dy;
}

int orientation(point_t start, point_t end1, point_t end2) {
    int ccw = (end1.first - start.first) * (end2.second - start.second) -
              (end1.second - start.second) * (end2.first - start.first);
    if (ccw == 0)
        return 0;
    return (ccw > 0) ? -1 : 1;
}

bool POLAR_ORDER(point_t first, point_t second) {
    int position = orientation(pivotalPoint, first, second);
    if (position == 0) {
        return euclidDist(pivotalPoint, first) < euclidDist(pivotalPoint, second);
    }
    return (position == -1);
}

bool onSegment(point_t p, point_t q, point_t r) {
    if (q.first <= max(p.first, r.first) && q.first >= min(p.first, r.first) &&
        q.second <= max(p.second, r.second) && q.second >= min(p.second, r.second))
        return true;
    return false;
}

bool doIntersect(point_t p1, point_t q1, point_t p2, point_t q2) {
    // Find the four orientations needed for general and
    // special cases
    int o1 = orientation(p1, q1, p2);
    int o2 = orientation(p1, q1, q2);
    int o3 = orientation(p2, q2, p1);
    int o4 = orientation(p2, q2, q1);

    // General case
    if (o1 != o2 && o3 != o4)
        return true;

    // Special Cases
    // p1, q1 and p2 are colinear and p2 lies on segment p1q1
    if (o1 == 0 && onSegment(p1, p2, q1)) return true;

    // p1, q1 and p2 are colinear and q2 lies on segment p1q1
    if (o2 == 0 && onSegment(p1, q2, q1)) return true;

    // p2, q2 and p1 are colinear and p1 lies on segment p2q2
    if (o3 == 0 && onSegment(p2, p1, q2)) return true;

    // p2, q2 and q1 are colinear and q1 lies on segment p2q2
    if (o4 == 0 && onSegment(p2, q1, q2)) return true;

    return false; // Doesn't fall in any of the above cases
}

bool isInside(polygon_t polygon, point_t p) {
    auto n = polygon.size();
    if (n < 3) return false;

    point_t extreme = {INF, p.second};

    int count = 0, i = 0;
    do {
        int next = (i + 1) % n;

        if (doIntersect(polygon[i], polygon[next], p, extreme)) {
            if (orientation(polygon[i], p, polygon[next]) == 0)
                return onSegment(polygon[i], p, polygon[next]);

            count++;
        }
        i = next;
    } while (i != 0);

    return count & 1;
}


polygon_t readPolygon(ifstream & inputStream) {
    int n;
    point_t input;
    inputStream >> n;
    vector <point_t> polygon(n);
    for (int i = 0; i < n; ++i) {
        inputStream >> input.first >> input.second;
        polygon[i] = input;
    }
    return polygon;
}

polygon_t merge(const polygon_t &polygon1, const polygon_t &polygon2) {
    int i = 0;
    int j = 0;
    polygon_t merged;
    while (i < polygon1.size() | j < polygon2.size()) {
        if (POLAR_ORDER(polygon1[i], polygon2[j])) {
            merged.push_back(polygon1[i]);
            ++i;
        } else {
            merged.push_back(polygon2[j]);
            ++j;
        }
    }
    return merged;
}

int main() {

    ifstream file("/home/grapefroot/ClionProjects/tryitOut/input.txt", ios::in);

    auto polygon1 = readPolygon(file);
    auto polygon2 = readPolygon(file);
    polygon_t polygon3 = polygon_t(polygon2.size());

    transform(polygon2.begin(), polygon2.end(), polygon3.begin(),
              [](point_t x) {
                  return make_pair(-x.first, -x.second);
              });

    polygon_t merged(polygon1.size() + polygon2.size());
    merge(polygon1.begin(), polygon1.end(), polygon3.begin(), polygon3.end(), merged.begin(), POLAR_ORDER);
    polygon_t sum;
    sum.push_back(merged[0]);
    for (int i = 0; i < merged.size() - 1; ++i) {
        sum.push_back(make_pair(merged[i].second, merged[i + 1].first));
    }
    sum.push_back(make_pair(merged[merged.size() - 1].second, merged[0].first));
    //minkowski summ calculated

    if (isInside(sum, make_pair(0, 0))) {
        cout << "YES";
    } else {
        cout << "NO";
    }

    return 0;
}