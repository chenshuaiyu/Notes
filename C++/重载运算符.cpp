#include<iostream>
using namespace std;

class Complex
{
public:
	Complex(){}
	
	Complex(int real,int imag)
	{
		this->real=real;
		this->imag=imag;
	}
	~Complex(){}
	
//	//���� ǰ��++ 
//	Complex operator++()
//	{
//		this->real++;
//		this->imag++;
//		return *this;
//	}
//	
//	//���� ����++ 
//	Complex operator++(int)
//	{
//		Complex c=*this;
//		this->real++;
//		this->imag++;
//		return c;
//	}

	//��Ԫ���� ǰ��++ 
	friend Complex operator++(Complex &c);

	//��Ԫ���� ����++ 
	friend Complex operator++(Complex &c,int);
	
	//���� + 
	friend Complex operator+(Complex &c1,Complex &c2);
	
	//���� << 
	friend ostream & operator<<(ostream &o,Complex &c);
	
private:
	int real;
	int imag;
};

Complex operator++(Complex &c)
{
	c.real++;
	c.imag++;
	return c;
}

Complex operator++(Complex &c,int)
{
	Complex c1=c;
	c.real++;
	c.imag++;
	return c1;
}

Complex operator+(Complex &c1,Complex &c2)
{
	return Complex(c1.real+c2.real,c1.imag+c2.imag);
}

ostream & operator<<(ostream &o,Complex &c)
{
	o<<c.real<<","<<c.imag<<endl;
	return o;
}

int main()
{
	Complex c1(1,2),c2(1,2);
	
	Complex c = c1 + c2;
	
	Complex c3=++c;
	cout << c3 << c;

	Complex c4=c++;
	cout << c4 << c;
	
	
} 

