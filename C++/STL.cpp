#include<iostream>
#include<vector>
#include<algorithm>
using namespace std;

void PrintVector(vector<int> ve);

int main()
{
	vector<int> vec1;//创建一个空的vector 
	vector<int> vec2(vec1);//创建一个vector，并用vec1初始化vec2
	vector<int> vec3(10);//创建一个有n个数据的vector 
	vector<int> vec4(10,0);//创建含有10个数据的vector，并全初始化为0 
//	vec4.~vector();//销毁所有数据，释放资源 
	
	//在vector尾部添加元素
	vec1.push_back(4); 
	vec1.push_back(6); 
	vec1.push_back(8); 
	vec1.push_back(1); 
	vec1.push_back(2); 
	PrintVector(vec1);
	
	//在尾部删除元素
	vec1.pop_back();
	PrintVector(vec1);
	
	//在vector头部添加元素，无法完成，因为vector的数据结构是数组，无法在头部插入元素，否则需要整个数组前移 
	
	//在vector头部删除元素，无法完成，理由同上。
	
	//取vector中某位置的元素值
	cout<<"在1位置的元素值为："<<vec1.at(1)<<endl;
	cout<<"在1位置的元素值为："<<vec1[1]<<endl;
	
	//begin():指向容器最开始位置数据的指针，详见PrintVector
	//end():指向容器最后一个数据单元的指针+1，详见PrintVector
	
	//返回尾部数据的引用
	cout<<"尾部数据的值为："<<vec1.back()<<endl; 
	//返回头部数据的引用
	cout<<"头部数据的值为："<<vec1.front()<<endl; 
	
	cout<<"vector中的最大容量为："<<vec1.max_size()<<endl;
	cout<<"vector中的元素个数为："<<vec1.size()<<endl;

	cout<<"vector是否为空："<<vec1.empty()<<endl;
	
	//交换两个容器中的数据
	vector<int> vecSwap;
	vecSwap.push_back(1);
	vecSwap.push_back(2);
	PrintVector(vec1);
	PrintVector(vecSwap);
	vec1.swap(vecSwap);
	PrintVector(vec1);
	PrintVector(vecSwap);
	
	//重新再交换回来 
	vec1.swap(vecSwap);
	
	//对vector进行升序排序 
	sort(vec1.begin(),vec1.end());
	PrintVector(vec1);

	//对vector进行降序排序 
	reverse(vec1.begin(),vec1.end());
	PrintVector(vec1);
	
	//修改数组的某个元素
	vec1[2]=99;
	PrintVector(vec1);
	vec1.at(3)=88;
	PrintVector(vec1);
	
	//删除数组的某个元素
	//为什么要使用iterator来进行定位，因为数组如果要删除一个元素或者折辱一个元素，会导致其他元素移动，所有不能直接进行删除
	vector<int>::iterator vItera=vec1.begin();
	vItera=vItera+2;
	vec1.erase(vItera);
	PrintVector(vec1);

	//vector插入某元素，要使用iterator来定位某个位置
	vector<int>::iterator vInsert=vec1.begin();
	vInsert=vInsert+2;
	vec1.insert(vInsert,777);
	PrintVector(vec1);
	
	//重新设置vector的容量
	vec1.resize(10);
	
	//清除所有数据
	vec1.clear();
	PrintVector(vec1);
	cout<<"vector是否为空："<<vec1.empty()<<endl;
	
	//测试vector去重
	int arr1[]={6,4,1,1,6,6,9,9,6,6};
	vector<int> arr1Vec(arr1,arr1+sizeof(arr1)/sizeof(int));
	sort(arr1Vec.begin(),arr1Vec.end());
	vector<int>::iterator arr1Ite =unique(arr1Vec.begin(),arr1Vec.end());
	arr1Vec.erase(arr1Ite,arr1Vec.end());
	PrintVector(arr1Vec);
	
	
	return 0;
}

void PrintVector(vector<int> ve)
{
	cout<<"Vector中的数据为：";
	vector<int>::iterator veIterator;
	
	for(veIterator=ve.begin();veIterator!=ve.end();veIterator++)
	{
		cout<<*veIterator<<" ";
	}
	cout<<endl;
}



