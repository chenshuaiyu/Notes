#include<iostream>

using namespace std;

//模板声明中 class 和 typename 均可用 

//模板方法
template <class T> void test(T t)
{
	cout<<t<<endl;
}

//模板类 
template <class T> class Test
{
public:
	Test(T t)
	{
		this->t=t;
	}
	
	~Test(){}
	
	void show();
	
private:
	T t;
};

template <class T> void Test<T>::show()
{
	cout<<t<<endl;
}

int main()
{
	//测试模板方法 
	test<int>(1);
	test<double>(1.5);
	
	//测试模板类 
	Test<int> t1(2);
	t1.show(); 
	Test<double> t2(2.5);
	t2.show(); 
	
	
	return 0;
}
 
