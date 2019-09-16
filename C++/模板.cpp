#include<iostream>

using namespace std;

//ģ�������� class �� typename ������ 

//ģ�巽��
template <class T> void test(T t)
{
	cout<<t<<endl;
}

//ģ���� 
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
	//����ģ�巽�� 
	test<int>(1);
	test<double>(1.5);
	
	//����ģ���� 
	Test<int> t1(2);
	t1.show(); 
	Test<double> t2(2.5);
	t2.show(); 
	
	
	return 0;
}
 
