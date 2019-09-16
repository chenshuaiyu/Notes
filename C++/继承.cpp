#include<iostream>
using namespace std;

//			public			protected		private
//���м̳�	public			protected		���ɼ� 
//˽�м̳�	private			private			���ɼ�
//�����̳�	protected		protected		���ɼ�
 
class Person
{
public:
	Person()
	{
		cout<<"Person construct is running "<<endl; 
	}
	
	~Person()
	{
		cout<<"Person destruct is running "<<endl; 
	}

	void show();
	
private:
	int p;
};

class Father:virtual public Person
{
public:
	Father()
	{
		cout<<"Father construct is running "<<endl; 
	}
	
	~Father()
	{
		cout<<"Father destruct is running "<<endl; 
	}
	
	void show()
	{
		cout<<"Father show"<<endl; 
	}
	
private:
	int f;
};

class Mother:virtual public Person
{
public:
	Mother()
	{
		cout<<"Mother construct is running "<<endl; 
	}
	
	~Mother()
	{
		cout<<"Mother destruct is running "<<endl; 
	}
	
	void show()
	{
		cout<<"Mother show"<<endl; 
	}
	
private:
	int m;
};

class Son:public Father,public Mother
{
public:
	Son()
	{
		cout<<"Son construct is running "<<endl; 
	}
	
	~Son()
	{
		cout<<"Son destruct is running "<<endl; 
	}
	
	void show()
	{
		cout<<"Son show"<<endl;
	} 
	
private:
	int s;
};

int main()
{
	Son s;
	s.show();
	s.Father::show();
	s.Mother::show();
	
	return 0;
}
