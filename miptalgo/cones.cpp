#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>
#include <numeric>
#include <limits.h>
#include <queue>

#define PI 3.14159265
#define INF 1000000000

using namespace std;

struct pt {
    double x, y;

    friend istream &operator>>(istream &istream, pt &p) {
        return istream >> p.x >> p.y;
    }
};

struct point {
    double x, y, z;

    point(double _x, double _y, double _z) : x(_x), y(_y), z(_z) { }

    point() = default;
};


bool cmp(pt a, pt b) {
    return a.x < b.x || a.x == b.x && a.y < b.y;
}

bool cw(pt a, pt b, pt c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) < 0;
}

bool ccw(pt a, pt b, pt c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) > 0;
}

void convex_hull(vector<pt> &a) {
    if (a.size() == 1) return;
    sort(a.begin(), a.end(), &cmp);
    pt p1 = a[0], p2 = a.back();
    vector<pt> up, down;
    up.push_back(p1);
    down.push_back(p1);
    for (size_t i = 1; i < a.size(); ++i) {
        if (i == a.size() - 1 || cw(p1, a[i], p2)) {
            while (up.size() >= 2 && !cw(up[up.size() - 2], up[up.size() - 1], a[i]))
                up.pop_back();
            up.push_back(a[i]);
        }
        if (i == a.size() - 1 || ccw(p1, a[i], p2)) {
            while (down.size() >= 2 && !ccw(down[down.size() - 2], down[down.size() - 1], a[i]))
                down.pop_back();
            down.push_back(a[i]);
        }
    }
    a.clear();
    for (size_t i = 0; i < up.size(); ++i)
        a.push_back(up[i]);
    for (size_t i = down.size() - 2; i > 0; --i)
        a.push_back(down[i]);
}

double euclidDist(const pt &a, const pt &b) {
    return sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2));
}


struct cone {
    pt center;
    double r, h;

    cone() = default;

    friend istream &operator>>(istream &istream, cone &c) {
        return istream >> c.center >> c.r >> c.h;
    }

    bool inter(const cone &c1, const cone &c2) {
        point first(c1.center.x, c1.center.y, c1.h);
        point second(c2.center.x, c2.center.y, c2.h);
        if (first.z > second.z) swap(first, second);
        if (first.z > h) return false;
        double a, b, c;
        auto sqr = [](double x) { return x * x; };
        c = sqr(first.x - center.x) + sqr(first.y - center.y);
        b = 2 * ((first.x - center.x) * (second.x - first.x) + (first.y - center.y) * (second.y - first.y));
        a = sqr(second.x - first.x) + sqr(second.y - first.y);
        c *= sqr(h);
        b *= sqr(h);
        a *= sqr(h);
        c += -sqr(h - first.z) * sqr(r);
        b += 2 * (h - first.z) * (second.z - first.z) * sqr(r);
        a += -sqr(second.z - first.z) * sqr(r);
        double t_bound = 1;
        if (second.z != first.z) t_bound = min(t_bound, (h - first.z) / (second.z - first.z));
        if (a == 0) {
            double t = -(double) c / (double) b;
            return (t >= 0 && t <= t_bound);
        } else {
            double discriminant = sqrt(sqr(b) - 4 * a * c);
            double t1 = (-b - discriminant) / (2 * a), t2 = (-b + discriminant) / (2 * a);
            if (t1 >= 0 && t1 <= t_bound || t2 >= 0 && t2 <= t_bound) return true;
            if (a > 0 && t1 < 0 && t2 > t_bound) return true;
            return false;
        }
    }

};

typedef vector<vector<bool>> graph_t;

int BFS(graph_t &graph) {
    queue<pair<int, int>> q;
    vector<bool> used(graph.size(), false);
    q.push(make_pair(0, 0));
    used[0] = true;
    while (!q.empty()) {
        auto cur = q.front();
        q.pop();
        if (cur.first == graph.size() - 1) {
            return cur.second;
        }
        for (int i = 0; i < graph.size(); ++i) {
            if (!used[i] && graph[cur.first][i]) {
                q.push(make_pair(i, cur.second + 1));
                used[i] = true;
            }
        }
    }
}


#define DEBUG

int main() {

    int n;
    vector<cone> cones;

#ifdef DEBUG
    ifstream file("/home/grapefroot/ClionProjects/eolimp/input.txt", ios::in);
    file >> n;
    cones.resize(n);
    for (int i = 0; i < n; ++i) {
        file >> cones[i];
    }
#endif

#ifndef DEBUG
    cin >> n;
    cones.resize(n);
    for (int i = 0; i < n; ++i) {
        cin >> cones[i];
    }
#endif

    graph_t paths(n, vector<bool>(n, true));

    //construct adjacency matrix
    for (int i = 0; i < cones.size(); ++i) {
        for (int j = 0; j < cones.size(); ++j) {
            for (int k = 0; k < cones.size(); ++k) {
                if (k == i || k == j) continue;
                if (cones[k].inter(cones[i], cones[j])) {
                    paths[i][j] = false;
                    paths[j][i] = false;
                    break;
                }
            }
        }
    }

    //shortest path using BFS
    cout << BFS(paths) << endl;

    return 0;
}
