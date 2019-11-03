# LruCache源码分析

## 目录

1. Lru算法
2. LruCache源码分析
   1. 构造方法
   2. put方法
   3. trimToSize方法
   4. get方法
3. DiskLruCache源码分析
   1. 

------

### 1.Lru算法

LRU是Least Recently Used的缩写，最近最久未使用算法。核心原则是如果一个元素在最近一段时间内没有使用到，那么将来被访问到的可能性也很小，则这个元素将会被优先淘汰掉。

算法原理：使用链表实现，表尾访问数据，表头删除数据，当访问的数据在链表中存在时，则将该数据项移动到表尾，否则在表尾新建一个数据项，当链表容量超过一定阈值时，则移除表头的数据项。

### 2.LruCache源码分析

#### 2.1 构造方法

```java
int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
int cacheSize = maxMemory / 8;
mLruCache = new LruCache<String, Bitmap>(cacheSize) {

    // 定义计算entry大小的规则，默认返回1
    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
    }

    // 当key对应的缓存被删除时回调该方法
    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    // 使用此方法重建缓存，默认返回NULL
    @Override
    protected Bitmap create(String key) {
        return super.create(key);
    }
};
```

```java
public LruCache(int maxSize) {
    if (maxSize <= 0) {
        throw new IllegalArgumentException("maxSize <= 0");
    }
    this.maxSize = maxSize;
    this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
}
```

#### 2.2 put方法

1. 插入元素，当前缓存容量增加
2. 调用trimToSize()，清除表头节点，直到当前缓存容量小于最大容量

```java
public final V put(@NonNull K key, @NonNull V value) {
    // 键值都不能为NULL
    if (key == null || value == null) {
        throw new NullPointerException("key == null || value == null");
    }

    V previous;
    synchronized (this) {
        // put次数+1
        putCount++;
        // 当前size + 新value的size
        size += safeSizeOf(key, value); // ---> 分析1：safeSizeOf(key, value)
        // 当前key对应的previous value
        previous = map.put(key, value);
        // 当前size - previous的size
        if (previous != null) {
            size -= safeSizeOf(key, previous);
        }
    }

   	// 当previous不为空时，调用可重写的entryRemoved
    if (previous != null) {
        // 默认实现为空
        entryRemoved(false, key, previous, value);
    }

    // ---> 见2.3 trimToSize方法
    trimToSize(maxSize);
    return previous;
}
```

```java
// ---> 分析1：safeSizeOf(key, value)
private int safeSizeOf(K key, V value) {
    // 调用重写的sizeOf方法，返回原value的size
    int result = sizeOf(key, value);
    if (result < 0) {
        throw new IllegalStateException("Negative size: " + key + "=" + value);
    }
    return result;
}
```

#### 2.3 trimToSize方法

```java
// 判断当前size是否达到maxsize，若达到，则将map队尾元素（最不常用的元素）remove掉
public void trimToSize(int maxSize) {
    while (true) {
        K key;
        V value;
        synchronized (this) {
            if (size < 0 || (map.isEmpty() && size != 0)) {
                throw new IllegalStateException(getClass().getName()
                                                + ".sizeOf() is reporting inconsistent results!");
            }

            // 未超过maxsize
            if (size <= maxSize || map.isEmpty()) {
                break;
            }

            // 移除map队尾最不常用的元素
            Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
            key = toEvict.getKey();
            value = toEvict.getValue();
            map.remove(key);
            size -= safeSizeOf(key, value);
            evictionCount++;
        }

        entryRemoved(true, key, value, null);
    }
}
```

#### 2.4 get方法

1. 调用LinkedHashMap的get方法，获取对应缓存，并将此节点移至表尾
2. 若缓存不存在，使用create()创建新元素

```java
public final V get(@NonNull K key) {
    if (key == null) {
        throw new NullPointerException("key == null");
    }

    V mapValue;
    synchronized (this) {
        mapValue = map.get(key);
        if (mapValue != null) {
            // 击中元素数量+1
            hitCount++;
            return mapValue;
        }
        missCount++;
    }

    // 可重写create方法
    V createdValue = create(key);
    if (createdValue == null) {
        return null;
    }

    synchronized (this) {
        createCount++;
        mapValue = map.put(key, createdValue);

        if (mapValue != null) {
            // 出现冲突，所以撤销上一次的put操作
            map.put(key, mapValue);
        } else {
            size += safeSizeOf(key, createdValue);
        }
    }

    if (mapValue != null) {
        entryRemoved(false, key, createdValue, mapValue);
        return mapValue;
    } else {
        trimToSize(maxSize);
        return createdValue;
    }
}
```

### 3.DiskLruCache源码分析

```java
File directory = getCacheDir(); // 缓存目录
int appVersion = 1; // 版本号
int valueCount = 1; // 一个key对应的缓存文件数目，如果传入的参数大于1，那么缓存文件后缀就是.0，.1等
long maxSize = 10 * 1024; // 缓存容量上限
DiskLruCache diskLruCache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);

DiskLruCache.Editor editor = diskLruCache.edit(String.valueOf(System.currentTimeMillis()));
BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(editor.newOutputStream(0));
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scenery);
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);

editor.commit();
diskLruCache.flush();
diskLruCache.close();
```

#### 3.1 open方法

```java
public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)
    throws IOException {
    if (maxSize <= 0) {
        throw new IllegalArgumentException("maxSize <= 0");
    }
    if (valueCount <= 0) {
        throw new IllegalArgumentException("valueCount <= 0");
    }

    // 如果备份文件存在
    File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
    if (backupFile.exists()) {
        File journalFile = new File(directory, JOURNAL_FILE);
        if (journalFile.exists()) {
            // 如果journal文件存在，则删除备份文件journal.bkp
            backupFile.delete();
        } else {
            // 如果journal文件不存在，则把备份文件改为journal
            renameTo(backupFile, journalFile, false);
        }
    }

    DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
    // 判断journal文件是否存在
    if (cache.journalFile.exists()) {
        try {
            // 读取journal文件，根据记录中不同的操作类型进行相应的处理
            cache.readJournal();
            // 计算当前缓存容量的大小
            cache.processJournal();
            return cache;
        } catch (IOException journalIsCorrupt) {
            System.out
                .println("DiskLruCache "
                         + directory
                         + " is corrupt: "
                         + journalIsCorrupt.getMessage()
                         + ", removing");
            cache.delete();
        }
    }

    // 创建一个缓存目录
    directory.mkdirs();
    cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
    // 建立新的journal文件
    cache.rebuildJournal();
    return cache;
}
```

#### 3.2 journal文件

缓存目录中除了缓存文件，还有journal文件，是用来记录缓存的操作记录的。

缓存目录/data/data/packagename/cache，未root的手机可以通过以下命令进入到该目录中或者将该目录整体拷贝出来：

```shell
// 进入/data/data/pckagename/cache目录
adb shell
run-as com.your.packagename 
cp /data/data/com.your.packagename/

//将/data/data/pckagename目录拷贝出来
adb backup -noapk com.your.packagename
```

文件内容：

```
libcore.io.DiskLruCache
1
1
1

DIRTY 1517126350519
CLEAN 1517126350519 5325928
REMOVE 1517126350519
```

- 第一行：libcore.io.DiskLruCache，固定字符串
- 第二行：1，DiskLruCache源码版本号
- 第三行：1，App版本号，通过open方法传入
- 第四行：1，每个key对应几个文件，一般为1
- 第五行：空行
- 第六行及以后：缓存操作记录

关于缓存操作记录：

1.  DIRTY表示一个entry正在被写入。写分为两种情况：如果成功会紧接着写入一行CLEAN的记录，如果失败，会增加一行REMOVE记录。单独只有DIRTY的记录是非法的。
2. 手动调用remove(key)的时候也会写入一条REMOVE记录。
3. READ就是说明有一次读取的记录。
4. CLEAN的后面记录了文件的长度，一个key可能会对应多个文件，那么就会有多个数字。

```java
private static final String CLEAN = "CLEAN";
private static final String DIRTY = "DIRTY";
private static final String REMOVE = "REMOVE";
private static final String READ = "READ";
```

#### 3.3 rebuildJournal方法

```java
private synchronized void rebuildJournal() throws IOException {
    if (journalWriter != null) {
        journalWriter.close();
    }

    Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(journalFileTmp), Util.US_ASCII));
    try {
        // 写入文件头
        writer.write(MAGIC);
        writer.write("\n");
        writer.write(VERSION_1);
        writer.write("\n");
        writer.write(Integer.toString(appVersion));
        writer.write("\n");
        writer.write(Integer.toString(valueCount));
        writer.write("\n");
        writer.write("\n");

        for (Entry entry : lruEntries.values()) {
            if (entry.currentEditor != null) {
                writer.write(DIRTY + ' ' + entry.key + '\n');
            } else {
                writer.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n');
            }
        }
    } finally {
        writer.close();
    }

    if (journalFile.exists()) {
        renameTo(journalFile, journalFileBackup, true);
    }
    renameTo(journalFileTmp, journalFile, false);
    journalFileBackup.delete();

    journalWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(journalFile, true), Util.US_ASCII));
}
```

#### 3.4 readJournal方法

```java
private void readJournal() throws IOException {
    StrictLineReader reader = new StrictLineReader(new FileInputStream(journalFile), Util.US_ASCII);
    try {
        // 读取文件头，并进行检验
        String magic = reader.readLine();
        String version = reader.readLine();
        String appVersionString = reader.readLine();
        String valueCountString = reader.readLine();
        String blank = reader.readLine();
        // 检查前5行是否合法
        if (!MAGIC.equals(magic)
            || !VERSION_1.equals(version)
            || !Integer.toString(appVersion).equals(appVersionString)
            || !Integer.toString(valueCount).equals(valueCountString)
            || !"".equals(blank)) {
            throw new IOException("unexpected journal header: [" + magic + ", " + version + ", "
                                  + valueCountString + ", " + blank + "]");
        }

        int lineCount = 0;
        while (true) {
            try {
                // 逐行读取journal文件
                readJournalLine(reader.readLine());
                // 记录读取行数
                lineCount++;
            } catch (EOFException endOfJournal) {
                break;
            }
        }
        // lineCount表示记录总行数
        // lruEntries.size()表示最终缓存的个数
        // redundantOpCount表示非法缓存记录的个数，这些记录会被移除掉
        redundantOpCount = lineCount - lruEntries.size();

        if (reader.hasUnterminatedLine()) {
            rebuildJournal();
        } else {
            journalWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(journalFile, true), Util.US_ASCII));
        }
    } finally {
        Util.closeQuietly(reader);
    }
}
```

```java
private void readJournalLine(String line) throws IOException {
    // 每行记录都是用空格分隔开的，这里取第一个空格出现的位置
    int firstSpace = line.indexOf(' ');
    if (firstSpace == -1) {
        throw new IOException("unexpected journal line: " + line);
    }

    // 第一个空格前面就是CLEAN、READ这些操作类型，接下来针对不同的操作类型进行相应的处理
    int keyBegin = firstSpace + 1;
    int secondSpace = line.indexOf(' ', keyBegin);
    final String key;
    if (secondSpace == -1) {
        key = line.substring(keyBegin);
        // 该条记录以REMOVE开头，则执行删除操作
        if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
            lruEntries.remove(key);
            return;
        }
    } else {
        key = line.substring(keyBegin, secondSpace);
    }

    // 该key不存在，则新建Entry并加入lruEntries
    Entry entry = lruEntries.get(key);
    if (entry == null) {
        entry = new Entry(key);
        lruEntries.put(key, entry);
    }

    // 如果该条记录以CLEAN开头，则初始化entry，并设置 
    // entry.readable = true
    // entry.currentEditor = null
    // 初始化冲长度
    if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
        // 数组中其实是数字，就是文件的大小。因为可以通过valueCount来设置一个key对应的value的个数，所以文件大小也是有valueCount个
        String[] parts = line.substring(secondSpace + 1).split(" ");
        entry.readable = true;
        entry.currentEditor = null;
        entry.setLengths(parts);
    } 
    // 如果该条记录以DIRTY为开头，则设置currentEditor对象
    else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
        entry.currentEditor = new Editor(entry);
    } 
    // 如果该条记录以READ为开头，则什么也不做
    else if (secondSpace == -1 && firstSpace == READ.length() && line.startsWith(READ)) {
        // This work was already done by calling lruEntries.get().
    } else {
        throw new IOException("unexpected journal line: " + line);
    }
}
```

```java
private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<String, Entry>(0, 0.75f, true);
```

1. 如果该条记录以REMOVE为开头，则执行删除操作。
2. 如果该key不存在，则新建Entry并加入lruEntries。
3. 如果该条记录以CLEAN为开头，则初始化entry，并设置entry.readable为true、设置entry.currentEditor为null，初始化entry长度。
4. 如果该条记录以DIRTY为开头。则设置currentEditor对象。
5. 如果该条记录以READ为开头，则什么也不做。

#### 3.5 processJournal方法

```java
private void processJournal() throws IOException {
    // 删除journal.tmp临时文件
    deleteIfExists(journalFileTmp);
    // 遍历缓存集合里的所有元素
    for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext(); ) {
        Entry entry = i.next();
        if (entry.currentEditor == null) {
            // 计算该元素的总大小，并添加到总缓存容量size中去
            for (int t = 0; t < valueCount; t++) {
                size += entry.lengths[t];
            }
        } else {
            // 非法缓存记录，该记录以及对应的缓存文件都会被删掉
            entry.currentEditor = null;
            for (int t = 0; t < valueCount; t++) {
                deleteIfExists(entry.getCleanFile(t));
                deleteIfExists(entry.getDirtyFile(t));
            }
            i.remove();
        }
    }
}
```

#### 3.6 edit方法

```java
private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
    checkNotClosed();
    validateKey(key);
    // 从之前的缓存中读取对应的entry
    Entry entry = lruEntries.get(key);
    // 当前无法写入磁盘缓存
    if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER && (entry == null
                                                          || entry.sequenceNumber != expectedSequenceNumber)) {
        return null; // Snapshot is stale.
    }
    if (entry == null) {
        entry = new Entry(key);
        lruEntries.put(key, entry);
    } else if (entry.currentEditor != null) {
        return null; // Another edit is in progress.
    }

    Editor editor = new Editor(entry);
    entry.currentEditor = editor;

    // Flush the journal before creating files to prevent file leaks.
    journalWriter.write(DIRTY + ' ' + key + '\n');
    journalWriter.flush();
    return editor;
}
```







