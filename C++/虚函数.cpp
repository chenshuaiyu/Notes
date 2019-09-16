#include<iostream>
using namespace std;

class Father
{
public:
	Father(){} 
	
	//纯虚函数必须加 =0 
	virtual void show()=0;
	
private:
	
};

class Son:public Father
{
public:
	Son(){}
	
	void show()
	{
		cout<<"Son show"<<endl;
	}
	
private:	
	
};


int main()
{
	//没有实现show方法，Father是个抽象类 
//	Father f;
	
	//Son实现了show方法 
	Son s;
	s.show();
	
	
	return 0;
}
