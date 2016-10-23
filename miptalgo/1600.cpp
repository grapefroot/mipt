#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>
#include <math.h>
#include <limits>
#include <iomanip>

#define INF 10000

using namespace std;

typedef pair<float, float> point_t;

typedef vector<point_t> polygon_t;

point_t pivotalPoint = std::make_pair(0, 0);

template<typename T>
struct pt {
    T x, y;

    friend istream &operator>>(istream &istream, pt &p) {
        return istream >> p.x >> p.y;
    }

    double operator*(const pt &point) const {
        return this->x * point.x + this->y * point.y;
    }

    bool operator==(const pt &other) const {
        return this->x == other.x && this->y == other.y;
    }

    pt(T x, T y) : x(x), y(y) { }

    pt() { }

    pt operator-(pt p) {
        pt res = {x - p.x, y - p.y};
        return res;
    }
};

struct line {
    double a, b, c;

    line() = default;

    line(double a, double b, double c) : a(a), b(b), c(c) { }

    friend istream &operator>>(istream &istream, line &l) {
        return istream >> l.a >> l.b >> l.c;
    }

    bool operator==(const line &other) const {
        return this->a == other.a && this->b == other.b && this->c == other.c;
    }
};

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
    return q.first <= max(p.first, r.first) && q.first >= min(p.first, r.first) &&
           q.second <= max(p.second, r.second) && q.second >= min(p.second, r.second);
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
    return o4 == 0 && onSegment(p2, q1, q2);

    // Doesn't fall in any of the above cases
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


pt<double> intersect(line &l1, line &l2) {
    return pt<double>(-(l1.c * l2.b - l2.c * l1.b) / (l1.a * l2.b - l2.a * l1.b),
                      -(l1.a * l2.c - l2.a * l1.c) / (l1.a * l2.b - l2.a * l1.b));
}

struct Relaxer {
    int min;
    pt<int> k;
    pt<int> best;

    line getLine() {
        return line(k.x, k.y, -min);
    }

    Relaxer(pt<int> k, pt<int> st) : k(k), best(st), min(st * k) { }

    void relax(pt<int> p) {
        if (k * p < min) {
            min = k * p;
            best = p;
        }
    }
};

struct circle : pt<double> {
    double r;
};


const long double EPS = 1e-9;

double sqr(double a) {
    return a * a;
}

void tangents(pt<double> c, double r1, double r2, vector<line> &ans) {
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

int directions[8][2] = {{-1, -1},
                        {-1, 0},
                        {-1, 1},
                        {0,  1},
                        {1,  1},
                        {1,  -1},
                        {0,  -1}};

double distanceToLine(const line &_line, const pt<double> &_pt) {
    return (abs(_line.a * _pt.x + _line.b * _pt.y + _line.c)) / (sqrt(_line.a * _line.a + _line.b * _line.b));
}

struct Plane {
    double x, y, z;
    double speedX, speedY, speedZ;

    friend istream &operator>>(istream &stream, Plane &_plane) {
        stream >> _plane.x >> _plane.y >> _plane.z >> _plane.speedX >> _plane.speedY >> _plane.speedZ;
        return stream;
    }
};


long double dist(Plane &plane1, Plane &plane2, double gap) {
    double a = pow(plane1.speedX - plane2.speedX, 2) +
                    pow(plane1.speedY - plane2.speedY, 2) +
                    pow(plane1.speedZ - plane2.speedZ, 2);
    double b = 2 * ((plane1.speedX - plane2.speedX) * (plane1.x - plane2.x) +
                         (plane1.speedY - plane2.speedY) * (plane1.y - plane2.y) +
                         (plane1.speedZ - plane2.speedZ) * (plane1.z - plane2.z));
    double c = pow(plane1.x - plane2.x, 2) +
                    pow(plane1.y - plane2.y, 2) +
                    pow(plane1.z - plane2.z, 2) -
                    pow(gap, 2);
    if (abs(a) < EPS)
        return 0;
    else {
        double disc = b * b - 4 * a * c;
        if (disc < -EPS) {
            return 0;
        } else {
            return max((-b - sqrt(max(disc, (double) 0))) / (2 * a), (double) 0);
        }
    }
}

#define DEBUG

int main() {

    int n;
    double d;
#ifdef DEBUG
    ifstream file("/home/grapefroot/ClionProjects/tryitOut/input.txt", ios::in);
    file >> n >> d;
    vector<Plane> planes(n);
    for (int i = 0; i < n; ++i) {
        file >> planes[i];
    }
#endif

#ifndef DEBUG
    cin >> n;
    cin >> d;
    vector<Plane> planes(n);
    for (int i = 0; i < n; ++i) {
        cin >> planes[i];
    }
#endif
    
    double min = numeric_limits<double>::max();
    for (int i = 0; i < n; ++i) {
        for (int j = i + 1; j < n; ++j) {
            auto result = dist(planes[i], planes[j], d);
            if (result > 0 && result < min) {
                cout << "ALARM!" << endl;
                cout << setprecision(3) << fixed << result << " " << i + 1 << " " << j + 1;
                return 0;

            }
        }
    }
    cout << "OK" << endl;
    return 0;
}