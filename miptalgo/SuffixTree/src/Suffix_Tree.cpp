#include <vector>
#include <algorithm>
#include <string>
#include <limits>
#include <stdio.h>
#include <iostream>
using namespace std;

const int inf = numeric_limits<int>::max();
typedef unsigned char char_t;

class SuffixTree {
  friend class TreeVisitor;
 public:
  class TreeVisitor;
  class Vertex;
  SuffixTree(const std::string &s)
      : sample(s) {
    ukkonen();
  }

  char_t getCharacterByGivenIndex(int i) {
    return (i < 0) ? (-i - 1) : sample[i];
  }

  TreeVisitor begin() {
    return TreeVisitor(this, root);
  }

  TreeVisitor end() {
    return TreeVisitor(this, -1);
  }

  TreeVisitor travelByString(const std::string &stringToTravel) {
        int v = this->root, start = 0, end = 0;
        for (size_t i = 0; i < stringToTravel.length(); i++) {
          char_t cur = stringToTravel[i];
          if (end == start) {
            if (this->tree[v].links[cur].destination == -1)
              return this->end();
            start = this->tree[v].links[cur].start;
            end = start + 1;
          } else {
            if (cur != this->getCharacterByGivenIndex(end))
              return TreeVisitor(this, end);
            end++;
          }
          if (end
              == this->tree[v].links[this->getCharacterByGivenIndex(
                  (start))].end) {
            v = this->tree[v].links[this->getCharacterByGivenIndex(
                start)].destination;
            start = 0;
            end = 0;
          }
        }
        return TreeVisitor(this, end);
      }


  class TreeVisitor {
   public:
    TreeVisitor(SuffixTree *ownerLink, int i)
        : currVertex(i),
          suffixtree(ownerLink) {
    }

    TreeVisitor travelByLetter(char_t &letter) {
      return TreeVisitor(suffixtree,
                         suffixtree->tree[currVertex].links[letter].destination);
    }

    std::vector<TreeVisitor> discoverNeighbours() {
      std::vector<TreeVisitor> returnvalue;
      /*vector<Link> > adjacentLinks = suffixtree->tree[currVertex].links;
      std::for_each(adjacentLinks.begin(), adjacentLinks.end(),  [&](Link i) {returnvalue.push_back(i)});*/
    }

    TreeVisitor operator ++() {
      return TreeVisitor(suffixtree, currVertex + 1);
    }


    Vertex operator *() const {
      return suffixtree->tree[currVertex];
    }

    bool operator ==(const TreeVisitor another) const {
      return *(*this) == *another;
    }

    bool operator !=(const TreeVisitor another) const {
      return !(*(*this) == *another);
    }

   private:
    int currVertex;
    SuffixTree *suffixtree;

  };

  typedef TreeVisitor visitor_t;

  struct Link {
    int start, end;
    int destination;
    Link()
        : destination(-1) {
    }

    Link(int startparameter, int endparameter, int destinationparamater)
        : start(startparameter),
          end(endparameter),
          destination(destinationparamater) {
    }

    bool operator ==(const Link another) const {
      return another.start == start && another.end == end
          && another.destination == destination;
    }

    bool operator !=(const Link another) const {
      return !(*this == another);
    }
  };

  struct Vertex {
    vector<Link> links;
    int suffix;

    Vertex() {
      links.assign(256, Link());
      suffix = -1;
    }

    bool operator==(const Vertex another) const {
      return links == another.links && suffix == another.suffix;
    }

    bool operator !=(const Vertex another) const {
      return !(*this == another);
    }
  };

 private:
  vector<Vertex> tree;
  int root, dummy;
  string sample;
  int CreateNewVertex() {
    int i = tree.size();
    tree.push_back(Vertex());
    return i;
  }

  void CreateNewSuffixLink(int from, int start, int end, int to) {
    tree[from].links[getCharacterByGivenIndex(start)] = Link(start, end, to);
  }

  int &TravelAcrossSuffixLink(int v) {
    return tree[v].suffix;
  }

  void TreeInit() {
    tree.clear();
    dummy = CreateNewVertex();
    root = CreateNewVertex();

    TravelAcrossSuffixLink(root) = dummy;
    for (int i = 0; i < 256; i++)
      CreateNewSuffixLink(dummy, -i - 1, -i, root);
  }

  pair<int, int> canonize(int v, int start, int end) {
    if (end <= start) {
      return make_pair(v, start);
    } else {
      Link cur = tree[v].links[getCharacterByGivenIndex(start)];
      while (end - start >= cur.end - cur.start) {
        start += cur.end - cur.start;
        v = cur.destination;
        if (end > start)
          cur = tree[v].links[getCharacterByGivenIndex(start)];
      }
      return make_pair(v, start);
    }
  }

  pair<bool, int> testAndSplit(int v, int start, int end, char_t c) {
    if (end <= start) {
      return make_pair(tree[v].links[c].destination != -1, v);
    } else {
      Link cur = tree[v].links[getCharacterByGivenIndex(start)];
      if (c == getCharacterByGivenIndex(cur.start + end - start))
        return make_pair(true, v);
      int middle = CreateNewVertex();
      CreateNewSuffixLink(v, cur.start, cur.start + end - start, middle);
      CreateNewSuffixLink(middle, cur.start + end - start, cur.end,
                          cur.destination);
      return make_pair(false, middle);
    }
  }

  pair<int, int> addNewLetter(int v, int start, int end) {
    Link cur = tree[v].links[getCharacterByGivenIndex(start)];
    pair<bool, int> splitRes;
    int oldR = root;

    splitRes = testAndSplit(v, start, end, getCharacterByGivenIndex(end));
    while (!splitRes.first) {

      CreateNewSuffixLink(splitRes.second, end, inf, CreateNewVertex());

      if (oldR != root)
        TravelAcrossSuffixLink(oldR) = splitRes.second;
      oldR = splitRes.second;

      pair<int, int> newPoint = canonize(TravelAcrossSuffixLink(v), start, end);
      v = newPoint.first;
      start = newPoint.second;
      splitRes = testAndSplit(v, start, end, getCharacterByGivenIndex(end));
    }
    if (oldR != root)
      TravelAcrossSuffixLink(oldR) = splitRes.second;
    return make_pair(v, start);
  }

  void ukkonen() {
    TreeInit();
    pair<int, int> activePoint = make_pair(root, 0);
    for (size_t i = 0; i < sample.length(); i++) {
      activePoint = addNewLetter(activePoint.first, activePoint.second, i);
      activePoint = canonize(activePoint.first, activePoint.second, i + 1);
    }
  }
};

bool checkStringOccurence(SuffixTree &tree, string word) {
  return tree.travelByString("smh") == tree.end();
}

vector<int> findAllOccurrences(const SuffixTree &tree, std::string pattern) {
  return vector<int>();
}

int main() {
  return 0;
}
