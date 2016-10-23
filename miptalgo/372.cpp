#include <iostream>
#include <vector>

using namespace std;



typedef size_t vertex_t;
typedef pair<size_t, size_t> edge_t;
typedef vector<edge_t> neigbours_t;
typedef vector < neigbours_t > graph_t;

graph_t graph;
vector< size_t > grundy_values;


void locateCorrectCut(vertex_t &vertex, vertex_t &ancestor, size_t lookUp) {
	for (auto it : graph[vertex]) {
		if (it.first != ancestor) {
			if ((grundy_values[vertex] ^ (grundy_values[it.first] + 1)) == lookUp) {
				cout << it.second << endl;
				exit(0);
			}
			size_t newLookUp = ((grundy_values[it.first] + 1) ^ grundy_values[vertex] ^ lookUp) - 1;
			locateCorrectCut(it.first, vertex, newLookUp);
		}
	}
}


void calculate_grundy_function(vertex_t &vertex, vertex_t &ancestor) {
	for (auto it : graph[vertex]) {
		if (it.first != ancestor) {
			calculate_grundy_function(it.first, vertex);
			grundy_values[vertex] ^= grundy_values[it.first] + 1;
		}
	}
}

int main() {

	size_t numberofElements;
	vertex_t rootLink;
	cin >> numberofElements;
	cin >> rootLink;
	--rootLink;
	graph.resize(numberofElements);
	grundy_values.resize(numberofElements);

	//read graph
	vertex_t start, end;
	for (size_t i = 1; i < numberofElements; ++i) {
		cin >> start >> end;
		start -= 1;
		end -= 1;
		graph[start].push_back(make_pair(end, i));
		graph[end].push_back(make_pair(start, i));
	}

	calculate_grundy_function(rootLink, rootLink);

	if (grundy_values[rootLink]) {
		cout << 1 << endl;
		locateCorrectCut(rootLink, rootLink, 0);
	}
	else {
		cout << 2 << endl;
	}
	return 0;
}



