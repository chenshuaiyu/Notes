#include<iostream>
#include<fstream>
using namespace std;

//ios::in Ϊ����(��)�����ļ� 
//ios::out Ϊ���(д)�����ļ� 
//ios::ate ��ʼλ�ã��ļ�β 
//ios::app ��������������ļ�ĩβ 
//ios::trunc ����ļ��Ѵ�������ɾ�����ļ� 
//ios::binary �����Ʒ�ʽ


class Stu
{
public:
	Stu(char* name,char* number,const int grade)
	{
		this->name=name;
		this->number=number;
		this->grade=grade;
	}
	friend ofstream& operator<<(ofstream& out,Stu &s);

private:	
	char* name;
	char* number;
	int grade;
};

ofstream& operator<<(ofstream& out,Stu &s)
{	
	out<<"������"<<s.name<<" ѧ�ţ�"<<s.number<<" �ɼ���"<<s.grade<<endl;
	return out;
}

int main()
{
	Stu s[]={Stu("zhangsan","0001",91),Stu("lisi","0002",92),Stu("wangwu","0003",93),Stu("zhaoliu","0004",94)};
	ofstream out("E:/�ɼ���.txt");
	if(out.is_open())
	{
		for(int i=0;i<4;i++)
		{
			out<<s[i]; 
		}
		out.close();
	}
	else
	{
		cout<<"д�룺�ļ���ʧ��"<<endl;
	}
	
	ifstream in("E:/�ɼ���.txt"); 
	if(in.is_open())
	{
		while(!in.eof())
		{
			char buf[1024];
			in.getline(buf,1024);
			cout<<buf<<endl;
		}
		in.close(); 
	}
	else
	{
		cout<<"�������ļ���ʧ��"<<endl;
	}
	
	
	return 0;
}

