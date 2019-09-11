# AQS

AbstractQueueSynchronizer提供了一个基于FIFO队列，可以用于构建锁或者其他相关同步装置的基础框架。该同步器利用了一个int来表示状态，期望它能够成为实现大部分同步需求的基础。使用的方法是继承，子类通过继承同步器并需要实现它的方法来管理其状态，管理的方式就是通过类似acquire和release的方式操纵状态。然而在多线程环境中对状态的操纵必须确保原子性，因此子类对于状态的把握，需要使用和这个同步器提供的以下三个方法对状态进行操作。

```java
java.util.concurrent.locks.AbstractQueuedSynchronizer.getState()
java.util.concurrent.locks.AbstractQueuedSynchronizer.setState(int)
java.util.concurrent.locks.AbstractQueuedSynchronizer.compareAndSetState(int, int)
```

