#include <iostream>
#include <string>
#include <vector>

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

void printPref(const vector<int> &prefixes, const string &s2) {
	cout << "No\n";
	for (int i = prefixes.size() - 1; i>0; --i){
		for (int j = prefixes[i]; j < prefixes[i - 1]; ++j) {
			cout << s2[j];
		}
		cout << " ";
	}

	//last prefix
	cout << s2.substr(prefixes[0], s2.size()) << "\n";
}


int main(){
	string s1, s2;
	cin >> s1 >> s2;
	string concat = s1 + "*" + s2;
	vector<int> pf = prefixFunction(concat);
	vector<int> prefixes;
	int position = s2.size() - 1;
	int shift = s1.size() + 1;

	while (pf[shift + position] > 0) {
		position -= pf[shift + position];
		prefixes.push_back(position + 1);
	}
	
	if (position != -1) {
		cout << "Yes";
		return 0;
	}

	printPref(prefixes, s2);
	return 0;
}