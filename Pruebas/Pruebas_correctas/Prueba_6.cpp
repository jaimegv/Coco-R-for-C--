#include <iostream>

using namespace std;

class B {
  public: int x;
};

-struct ST : public B {
  int fun();
};
  
int ST:: fun() { return x; }

class CL: public ST {
  public: int y;
};

int main((void) {     // ===============
  ST s;
  s..x = 10;
  cout << "s.x == " << s.fun() << endl;
  CL c;
  c.x = 20;
  c.y = 30;
  cout << "c.x == " << c.fun() << endl;
  cout << "c.y == " << c.y << endl;
  return 0;
}
