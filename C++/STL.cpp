#include<iostream>
#include<vector>
#include<algorithm>
using namespace std;

void PrintVector(vector<int> ve);

int main()
{
	vector<int> vec1;//����һ���յ�vector 
	vector<int> vec2(vec1);//����һ��vector������vec1��ʼ��vec2
	vector<int> vec3(10);//����һ����n�����ݵ�vector 
	vector<int> vec4(10,0);//��������10�����ݵ�vector����ȫ��ʼ��Ϊ0 
//	vec4.~vector();//�����������ݣ��ͷ���Դ 
	
	//��vectorβ�����Ԫ��
	vec1.push_back(4); 
	vec1.push_back(6); 
	vec1.push_back(8); 
	vec1.push_back(1); 
	vec1.push_back(2); 
	PrintVector(vec1);
	
	//��β��ɾ��Ԫ��
	vec1.pop_back();
	PrintVector(vec1);
	
	//��vectorͷ�����Ԫ�أ��޷���ɣ���Ϊvector�����ݽṹ�����飬�޷���ͷ������Ԫ�أ�������Ҫ��������ǰ�� 
	
	//��vectorͷ��ɾ��Ԫ�أ��޷���ɣ�����ͬ�ϡ�
	
	//ȡvector��ĳλ�õ�Ԫ��ֵ
	cout<<"��1λ�õ�Ԫ��ֵΪ��"<<vec1.at(1)<<endl;
	cout<<"��1λ�õ�Ԫ��ֵΪ��"<<vec1[1]<<endl;
	
	//begin():ָ�������ʼλ�����ݵ�ָ�룬���PrintVector
	//end():ָ���������һ�����ݵ�Ԫ��ָ��+1�����PrintVector
	
	//����β�����ݵ�����
	cout<<"β�����ݵ�ֵΪ��"<<vec1.back()<<endl; 
	//����ͷ�����ݵ�����
	cout<<"ͷ�����ݵ�ֵΪ��"<<vec1.front()<<endl; 
	
	cout<<"vector�е��������Ϊ��"<<vec1.max_size()<<endl;
	cout<<"vector�е�Ԫ�ظ���Ϊ��"<<vec1.size()<<endl;

	cout<<"vector�Ƿ�Ϊ�գ�"<<vec1.empty()<<endl;
	
	//�������������е�����
	vector<int> vecSwap;
	vecSwap.push_back(1);
	vecSwap.push_back(2);
	PrintVector(vec1);
	PrintVector(vecSwap);
	vec1.swap(vecSwap);
	PrintVector(vec1);
	PrintVector(vecSwap);
	
	//�����ٽ������� 
	vec1.swap(vecSwap);
	
	//��vector������������ 
	sort(vec1.begin(),vec1.end());
	PrintVector(vec1);

	//��vector���н������� 
	reverse(vec1.begin(),vec1.end());
	PrintVector(vec1);
	
	//�޸������ĳ��Ԫ��
	vec1[2]=99;
	PrintVector(vec1);
	vec1.at(3)=88;
	PrintVector(vec1);
	
	//ɾ�������ĳ��Ԫ��
	//ΪʲôҪʹ��iterator�����ж�λ����Ϊ�������Ҫɾ��һ��Ԫ�ػ�������һ��Ԫ�أ��ᵼ������Ԫ���ƶ������в���ֱ�ӽ���ɾ��
	vector<int>::iterator vItera=vec1.begin();
	vItera=vItera+2;
	vec1.erase(vItera);
	PrintVector(vec1);

	//vector����ĳԪ�أ�Ҫʹ��iterator����λĳ��λ��
	vector<int>::iterator vInsert=vec1.begin();
	vInsert=vInsert+2;
	vec1.insert(vInsert,777);
	PrintVector(vec1);
	
	//��������vector������
	vec1.resize(10);
	
	//�����������
	vec1.clear();
	PrintVector(vec1);
	cout<<"vector�Ƿ�Ϊ�գ�"<<vec1.empty()<<endl;
	
	//����vectorȥ��
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
	cout<<"Vector�е�����Ϊ��";
	vector<int>::iterator veIterator;
	
	for(veIterator=ve.begin();veIterator!=ve.end();veIterator++)
	{
		cout<<*veIterator<<" ";
	}
	cout<<endl;
}



