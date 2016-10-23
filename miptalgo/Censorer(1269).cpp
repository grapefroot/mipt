#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <iomanip>
#include <map>
#include <fstream>
#include <unordered_map>

using namespace std;

const int INF = 10000000;

class trie
{
public:
	trie()
	{
		vertices[0].ancestor_hash = 0;
		total_vertices = 1;
	}

	void add_string(const string &word)
	{
		int current_vertex = 0;
		for (int pos = 0; pos < word.size(); pos++)
		{
			if (transition.find(hash_int_and_char(current_vertex, word[pos])) == transition.end())
				add_vertex(current_vertex, word[pos]);
			current_vertex = letterTransition(current_vertex, word[pos]);
		}
		vertices[current_vertex].length_of_word_ending_here = (int)word.size();
	}

	int checkNextLine()
	{
		get_termin_link(vertices[0]);
		int current_position = 0;
		int best_result = INF;

		char letter = 0;

		for (int letter_number = 0; letter != '\n'; letter_number++)
		{
			cin.get(letter);
			current_position = letterTransition(current_position, letter);
			int current_vertex = current_position;
			while (current_vertex != 0)
			{
				if (vertices[current_vertex].length_of_word_ending_here)
					best_result = min(best_result, letter_number + 2 - vertices[current_vertex].length_of_word_ending_here);
				current_vertex = get_termin_link(vertices[current_vertex]);
			}
		}
		return best_result;
	}

private:
	struct trie_vertex
	{
		int length_of_word_ending_here;
		int suffix_link;
		int termin_link;
		int ancestor_hash;

		trie_vertex()
		{
			length_of_word_ending_here = 0;
			suffix_link = INF;
			termin_link = INF;
		}

		int get_ancestor()
		{
			return ancestor_hash / MAXIMUM_CHAR_VALUE;
		}

		char get_letter_from_ancestor()
		{
			return ancestor_hash % MAXIMUM_CHAR_VALUE;
		}
	};

	int hash_int_and_char(int integer, char character)
	{
		int character_as_int = character;
		if (character_as_int < 0)
			character_as_int += MAXIMUM_CHAR_VALUE;
		return integer * MAXIMUM_CHAR_VALUE + character_as_int;
	}

	void add_vertex(int ancestor, char letter_from_ancestor)
	{
		int vertex_number = total_vertices;
		total_vertices++;
		vertices[vertex_number].ancestor_hash = hash_int_and_char(ancestor, letter_from_ancestor);
		transition[hash_int_and_char(ancestor, letter_from_ancestor)] = vertex_number;
	}

	int get_termin_link(trie_vertex &current_vertex)
	{
		if (current_vertex.termin_link == INF)
			construct_links(current_vertex);
		return current_vertex.termin_link;
	}

	int get_suffix_link(trie_vertex &current_vertex)
	{
		if (current_vertex.suffix_link == INF)
			construct_links(current_vertex);
		return current_vertex.suffix_link;
	}

	int construct_links(trie_vertex &current_vertex)
	{
		if (current_vertex.suffix_link == INF)
		{
			if (current_vertex.get_ancestor() == 0)
			{
				current_vertex.suffix_link = 0;
				current_vertex.termin_link = 0;
			}
			else
			{
				construct_links(vertices[current_vertex.get_ancestor()]);
				current_vertex.suffix_link = letterTransition(construct_links(vertices[current_vertex.get_ancestor()]), current_vertex.get_letter_from_ancestor());
				construct_links(vertices[current_vertex.suffix_link]);
			}

			if (vertices[current_vertex.suffix_link].length_of_word_ending_here)
				current_vertex.termin_link = current_vertex.suffix_link;
			else
				current_vertex.termin_link = vertices[current_vertex.suffix_link].termin_link;
		}

		return current_vertex.suffix_link;
	}

	int letterTransition(int vertex, int letter)
	{
		unordered_map<int, int> ::iterator to = transition.find(hash_int_and_char(vertex, letter));
		if (to != transition.end())
			return to->second;
		else
			return (vertex == 0 ? 0 : letterTransition(get_suffix_link(vertices[vertex]), letter));
	}

	const static int TRIE_SIZE = 100000;
	const static int MAXIMUM_CHAR_VALUE = 256;
	trie_vertex vertices[TRIE_SIZE];
	int total_vertices;
	unordered_map<int, int> transition;
};

void read_words(trie &bad_words)
{
	int words_number;
	cin >> words_number;
	cin.get();
	for (int word_number = 0; word_number < words_number; word_number++)
	{
		string word;
		getline(cin, word);
		bad_words.add_string(word);
	}
}

void solve(trie &bad_words)
{
	int number_of_lines_in_input;
	cin >> number_of_lines_in_input;
	cin.get();
	for (int line_number = 0; line_number < number_of_lines_in_input; line_number++)
	{
		int bad_word_position = bad_words.checkNextLine();
		if (bad_word_position != INF)
		{
			cout << line_number + 1 << " " << bad_word_position << "\n";
			return;
		}
	}

	cout << "Passed";
}

int main()
{
	trie bad_words;
	read_words(bad_words);
	solve(bad_words);
}