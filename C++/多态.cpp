#include<iostream>
using namespace std;
 
class Father
{
public:
	Father()
	{
//		cout<<"Father construct is running "<<endl; 
	}
	
	~Father()
	{
//		cout<<"Father destruct is running "<<endl; 
	}

//	�麯������Ҫ���þ���"����ʱ��̬" 
	virtual void show()
	{
		cout<<"Father show"<<endl; 
	}
	
private:
	
};


class Son:public Father
{
public:
	Son()
	{
//		cout<<"Son construct is running "<<endl; 
	}
	
	~Son()
	{
//		cout<<"Son destruct is running "<<endl; 
	}
	
	void show()
	{
		cout<<"Son show"<<endl;
	}
	
private:
	int f;
};

void test(Father &f)//ע�� ���� 
{
	f.show();
}

int main()
{
	Father f;
	test(f);
	
	Son s;
	test(s);
	
	return 0;
}
