#include <iostream>
using namespace std;

namespace Chen
{
	int i;
	void show();
	
	class Test
	{
	public:
		Test();
				
		void show();
		
	private:
		
	};

}

void Chen::show()
{
	cout<<"Chen::show"<<endl;
} 

Chen::Test::Test()
{
	cout<<"Chen::Test construct is running"<<endl;
}

void Chen::Test::show()
{
	cout<<"Test show"<<endl;
}

using namespace Chen;

int main()
{
//	Chen::show();
//	Chen::Test t;
//	t.show();

	show();
	Test t;
	t.show();

	return 0;
}
