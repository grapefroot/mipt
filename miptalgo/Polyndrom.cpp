#include <iostream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

vector<int> prefixFunction(const string &s) {
	int j = 0;
	vector<int> result(s.size());
	result[0] = 0;
	for (int i = 1; i < s.size(); ++i){
		while (j > 0 && s[j] != s[i]) {
			j = result[j - 1];
		}

		if (s[j] == s[i]) {
			++j;
		}
		result[i] = j;
	}
	return result;
}

void print(const string &s, int index) {
	for (int i = index - 1; i >= 0; --i)
		cout << s[i];
}

int main() {
	string s1;
	cin >> s1;
	string s2(s1);
	reverse(s1.begin(), s1.end());
	string concat = s1 + "#" + s2;
	vector<int> prefix = prefixFunction(concat);
	int border = s1.size() - prefix[prefix.size() - 1];
	cout << s2;
	if (border != 0) {
		print(s2, border);
	}	else {
		int size = s1.size() - prefix[s1.size() - 1];
		print(s2, size);
	}
	return 0;
}