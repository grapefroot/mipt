#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>

#define INF 10000

using namespace std;

typedef pair<float, float> point_t;
typedef vector<point_t> polygon_t;

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

struct pt {
    double x, y;

    pt operator-(pt p) {
        pt res = {x - p.x, y - p.y};
        return res;
    }
};

struct circle : pt {
    double r;
};

struct line {
    double a, b, c;
};

const double EPS = 1E-9;

double sqr(double a) {
    return a * a;
}

void tangents(pt c, double r1, double r2, vector<line> &ans) {
    double r = r2 - r1;
    double z = sqr(c.x) + sqr(c.y);
    double d = z - sqr(r);
    if (d < -EPS) return;
    d = sqrt(abs(d));
    line l;
    l.a = (c.x * r + c.y * d) / z;
    l.b = (c.y * r - c.x * d) / z;
    l.c = r1;
    ans.push_back(l);
}

vector<line> tangents(circle a, circle b) {
    vector<line> ans;
    tangents(b - a, a.r * -1, b.r * 1, ans);
    tangents(b - a, a.r * 1, b.r * -1, ans);
    for (size_t i = 0; i < ans.size(); ++i)
        ans[i].c -= ans[i].a * a.x + ans[i].b * a.y;
    return ans;
}

double distanceToLine(const line &_line, const pt &_pt) {
    return (abs(_line.a * _pt.x + _line.b * _pt.y + _line.c)) / (sqrt(_line.a * _line.a + _line.b * _line.b));
}

int main() {

    ifstream file("/home/grapefroot/ClionProjects/tryitOut/input.txt", ios::in);
    int n;
    file >> n;


    //read all inputs
    vector<circle> trees(n);
    double x, y, r;
    circle c;
    for (int i = 0; i < n; ++i) {
        file >> x >> y >> r;
        c.x = x;
        c.y = y;
        c.r = r;
        trees[i] = c;
    }


    bool hasLine;

    for (auto it: trees) {
        for (auto it_1: trees) {
            if(it_1.x == it.x && it.y == it_1.y && it.r == it_1.r) {
                continue;
            }
            auto tang = tangents(it, it_1);
            hasLine = true;
            for (auto tree: trees) {
                pt temp = tree;
                auto firstTang = distanceToLine(tang[0], temp);
                auto secondTang = distanceToLine(tang[1], temp);
                if (firstTang < tree.r || secondTang < tree.r) {
                    hasLine = false;
                    break;
                }
            }
            if (hasLine) {
                cout << "NO";
                return 0;
            }
        }
    }

    cout << "YES";
    return 0;
}