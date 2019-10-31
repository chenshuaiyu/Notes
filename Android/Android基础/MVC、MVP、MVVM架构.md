# MVC、MVP 、MVVM架构

### 1.MVC

- View：对应于 XML 布局。
- Model：数据层，负责存储、检索、操作数据。
- Cotroller：对应于Activity/Fragment中的业务逻辑。

缺点：

- **Activity/Fragment臃肿**：Activity/Fragment是View和Controller的合体，既要负责视图的显示又要加入控制逻辑，承担的功能太多。

### 2.MVP

- Model：数据层，负责存储、检索、操作数据。
- View：对应于Activity/Fragment，负责显示数据，向Presenter报告用户行为。
- Presenter：作为View与Model交互的中间纽带，从Model拿数据，应用到UI层，管理UI的状态，响应用户的行为。

相比于MVC的优点：

1. **分离视图与业务逻辑**：在Presenter中持有View的引用，然后在Presenter中调用View暴露的接口对视图进行操作，分离了视图和业务逻辑，降低了耦合。
2. **Activity/Fragment成为真正View**：MVP能够让Activity称为真正的View而不是View和Controller的合体，Activity只需要做UI相关的事，代码变得更加简洁。
3. **接口抽象**：View逻辑和业务逻辑分别抽象到View和Presenter的接口中去，提高代码的可阅读性。
4. **单元测试**：Presenter被抽象成接口，可以有多种具体的实现，方便进行单元测试。
5. **避免异常**：把业务逻辑抽象到Presenter中去，避免后台线程引用着Activity导致Activity的资源无法被系统回收从而引起内存泄露和OOM。

缺点：

1. **View和Presenter大量互相回调**：Activity需要实现各种和UI相关的接口，同时要在Activity中编写大量的事件，然后在事件处理中调用Presenter的业务处理方法，View和Presenter只是互相持有引用并互相做回调，代码不美观。
2. **视图和逻辑耦合性太高**：程序的主角是UI，通过UI的事件触发对数据进行处理，更新UI就有考虑线程的问题。而且UI改变后牵扯的逻辑耦合度太高，一旦控件更改，牵扯的更新UI的接口就必须得更换。
3. **Presenter臃肿**：复杂的业务同时会导致Presenter层太大，代码臃肿的问题。

### 3.MVVM

利用数据绑定（Data Binding）、依赖属性（Dependency Property）、命令（Command）、路由事件（Routed Eent）等特性，打造一个更加灵活高效的架构。

优点：

1. **数据驱动**：以前开发模式中必须先处理业务数据，然后根据数据变化，去获取UI的引用然后更新UI，同时也是通过UI来获取用户输入，而在MVVM中，数据和业务逻辑处于一个独立的ViewModel中，ViewModel只关注数据与业务逻辑，不需要和UI或者控件打交道。由数据自动去驱动UI去自动更新UI，UI的改变又同时自动反馈到数据，数据成为主导因素，这样使得在业务逻辑处理时只要关心数据，方便而且简单很多。
2. **低耦合度**：MVVM模式中，数据是独立于UI的，ViewModel只负责处理和提供数据，UI想怎么处理数据都由UI自己决定，ViewModel不涉及任何和UI相关的事也不持有UI控件的引用，即使控件改变（TextView换成EditText），ViewModel几乎不需要更改任何代码，专注于自己的数据处理就可以。如果是MVP遇到UI更改，就可能需要改变获取U的方式，改变更新UI的接口，改变从UI上获取输入的代码，可能还需要更改访问UI对象的属性代码等。
3. **更新UI**：在MVVM中，可以从工作线程中直接修改ViewModel的数据（只要数据是线程安全的），剩下的数据绑定框架帮你搞定，很多事情都不用关心。
4. **团队协作**：MVVM的分工是非常明显的，由于View和ViewModel之间是松散耦合的， 一个处理业务和数据，一个是专门的UI处理。完全由两个人分工来做，一个做UI（xml和Activity），一个写ViewModel，效率更高。
5. **可复用性**：一个ViewModel复用到多个View中，同样的一份数据，用不同的View去做展示，对于版本迭代频繁的UI改动，只需要更改View层就行。
6. **单元测试**：ViewModel里面是数据和业务逻辑，View中关注的是UI，这样做测试是很方便的，完全没有彼此的依赖，UI和业务逻辑的单元测试都是低耦合的。