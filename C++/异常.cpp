#include<iostream>
#include<exception>
using namespace std;

//自定义异常 
class MyException:public exception  
{
public:
	const char* what()const throw()
	{
		return "custom exception";
	}
	
};

void test()
{
	try
	{
		throw 1;
//		throw "error";
	}
	catch(int i) 
	{
		cout<<i<<endl;
	}
	catch(const char* c) 
	{
		cout<<c<<endl;
	}
	
} 

void test1()
{
	try
	{
		throw MyException();
	}
	catch(MyException e)
	{
		cout<<e.what()<<endl;
	}
}

int main()
{
	test(); 
	
	test1();
	
	return 0;
}
