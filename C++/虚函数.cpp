#include<iostream>
using namespace std;

class Father
{
public:
	Father(){} 
	
	//���麯������� =0 
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
	//û��ʵ��show������Father�Ǹ������� 
//	Father f;
	
	//Sonʵ����show���� 
	Son s;
	s.show();
	
	
	return 0;
}
