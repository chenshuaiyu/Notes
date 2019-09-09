# Git

Git是目前世界上最先进的分布式版本控制系统。

SVN和Git的区别：

- SVN是集中式版本控制系统，版本库是集中放在中央服务器的，工作时首先要从中央服务器哪里得到最新的版本，之后再推送到中央服务器。集中式版本控制系统是必须联网才能工作，如果在局域网还可以，带宽够大，速度够快。
- GIt是分布式版本控制系统，没有中央服务器，每个人的电脑就是一个完整的版本库，这样就不需要联网了，因为版本都是在自己的电脑上。多人如何协作时，自己在电脑上改了文件A，其他人也在电脑上改了文件A，这时，只需把各自的修改推送给对方，就可以互相看到对方的修改了。

![git工作流程](https://github.com/chenshuaiyu/Notes/blob/master/Git/assets/git工作流程.png)

- WorkSpace：工作区
- Index / Stage：暂存区
- Repository：仓库区（或本地仓库）
- Remote：远程仓库

### 一、创建版本库

```
git init	创建版本库
git init [projectname]	新建一个目录，将其初始化为Git代码库
git config --list	显示当前Git配置
git config --global user.name ""	设置提交代码时的用户名
git config --global user.email ""	设置提交代码时的邮箱
git add filename	添加某文件到暂存区
git commit -m ""	提交到本地仓库
git status	查看暂存区文件状态
git diff	查看修改但未暂存的文件
git diff filename	查看某个文件修改内容
```

### 二、版本回退

```
git log	历史记录
git log –pretty=oneline	简易历史记录
git reset --hard HEAD^	回退到上个版本
git reset --hard HEAD^^	回退到上上个版本
...
git reset --hard HEAD~100	回退到前100个版本
git reset --hard 版本号	回退到某个版本号
git reflog	获取所有版本号
```

### 三、工作区与暂存区的区别

- 工作区：工作文件下的所有文件（.git文件夹除外）。
- 版本库：.git文件夹。
- Git创建第一个分支master，以及指向master的一个指针HEAD。

```
git commit -m ""	一次性将暂存区所有修改提交到分支上
```

### 四、Git撤销修改和删除文件操作

```
git checkout -- filename 
1：把文件在工作区做的修改全部撤销
2；此file已被删除，执行命令后，文件恢复
```

### 五、远程仓库

#### 1.先创建本地仓库，再创建远程仓库，进行关联

```
git remote add origin https://github.com/chenshuaiyu/testgit.git
git push -u origin master
```

#### 2.先创建远程仓库，再创建本地仓库，进行关联

```
git clone https://github.com/chenshuaiyu/testgit.git
```

### 六、创建分支与合并分支

```
git checkout -b dev	创建并切换分支
等价于
git branch dev	创建分支
git checkout dev	切换分支

git branch	查看当前分支
git merge dev	将dev分支上的内容合并到分支master上
git branch -d dev	删除dev分支
```

### 七、BUG分支

```
git stash	隐藏工作现场
git stash list	查看工作现场
git stash apply	恢复工作现场，恢复后stash内容并不删除，需要使用命令git stash drop	删除
git stash pop 恢复的同时把stash内容删除
```

### 八、多人协作

```
git remote 查看远程库的信息
git remote –v 查看远程库的详细信息
```
