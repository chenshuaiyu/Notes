#include<iostream>
#include<fstream>
using namespace std;

//ios::in 为输入(读)而打开文件 
//ios::out 为输出(写)而打开文件 
//ios::ate 初始位置：文件尾 
//ios::app 所有输出附加在文件末尾 
//ios::trunc 如果文件已存在则先删除该文件 
//ios::binary 二进制方式


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
	out<<"姓名："<<s.name<<" 学号："<<s.number<<" 成绩："<<s.grade<<endl;
	return out;
}

int main()
{
	Stu s[]={Stu("zhangsan","0001",91),Stu("lisi","0002",92),Stu("wangwu","0003",93),Stu("zhaoliu","0004",94)};
	ofstream out("E:/成绩单.txt");
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
		cout<<"写入：文件打开失败"<<endl;
	}
	
	ifstream in("E:/成绩单.txt"); 
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
		cout<<"读出：文件打开失败"<<endl;
	}
	
	
	return 0;
}

