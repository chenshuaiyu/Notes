# SQLite

### 1.  SQLite的事务处理

SQLite在做CRUD操作是都默认开启了事务，然后把SQL语句翻译成对应的SQLiteStatement并调用其相应的CRUD方法，此时整个操作还是在rollback journal这个临时文件上进行，只有操作顺利完成才会更新.db数据库，否则会被回滚。

### 2. 使用SQLite做批量操作的方法

使用SQLiteDatabase的beginTransaction()方法开启一个事务，将批量操作SQL语句转换成SQLiteStatement并进行批量操作，结束后endTransaction()。

### 3. 删除SQLite中表的一个字段

SQLite数据库只允许增加表字段而不允许修改和删除表字段，只能采取复制表思想，即创建一个新表保留原表想要的字段，再将原表删除。

### 4. 使用SQLite时优化操作

- 使用事务做批量操作
- 及时关闭Cursor，避免内存泄露。
- 耗时操作异步化：数据库的操作属于本地IO，通常比较耗时，建议将这些耗时操作放入异步线程中处理。
- ContentValues的容量调整：ContentValues内部采用HashMap来存储Key-Value数据，ContentValues初始容量为8，扩容时翻倍。因此建议对ContentValues填入的内容进行估量，设置合理的初始化容量，减少不必要的内部扩容操作。
- 使用索引加快检索速度：对于查询操作量级较大，业务对查询要求较高的推荐使用索引。