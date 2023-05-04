### 缓存更新策略
1. 内存淘汰，由redis的过期策略来更新缓存，缓存过期后，下次访问时，从数据库中获取数据，然后更新缓存
2. 主动更新，由应用程序来更新缓存，应用程序定时更新缓存，或者应用程序在更新数据库时，更新缓存
    1. 缓存调用者去更新缓存
    2. 缓存和数据库整合为一个服务，由服务来进行维护一致性
![img.png](src/main/resources/img/refreshCache.png)
   ![img.png](src/main/resources/img/cacheOrder.png)
    3. 调用者只操作缓存，由其他线程异步去将缓存数据持久化到数据库
3. 超时剔除

### 缓存穿透
缓存穿透是指查询一个一定不存在的数据，由于缓存是不命中时才会去数据库查询，所以不存在的数据会一直去数据库查询，导致数据库压力过大。
常用的解决方案：
1. 布隆过滤器，将所有可能存在的数据哈希到一个足够大的bitmap中，一个一定不存在的数据会被这个bitmap拦截掉，从而避免了对底层存储系统的查询压力。
   1. 优点：内存占用少
   2. 缺点：有一定的误判率和删除困难
2. 缓存空对象，当缓存中没有数据时，将空对象缓存起来，下次查询时，直接返回空对象，避免了对底层存储系统的查询压力。
   1. 缓存空对象时，需要设置一个过期时间，防止缓存雪崩。
   2. 可能会造成短期的不一致。![img.png](src/main/resources/img/缓存空对象.png)

### 缓存雪崩
缓存雪崩是指缓存中的数据在同一时间过期或者redis服务宕机，导致大量的请求直接打到数据库上，造成数据库压力过大。
常用的解决方案：
1. 设置不同的过期时间，防止缓存同时过期
2. 设置热点数据永不过期
3. 限流降级，当缓存失效时，限制对数据库的访问，降低数据库的压力
4. 服务降级，当缓存失效时，直接返回空对象，避免了对底层存储系统的查询压力。
5. 给业务增加多级缓存，当缓存失效时，先从二级缓存中获取数据，如果二级缓存中没有数据，再从数据库中获取数据，然后更新缓存。

### 缓存击穿
缓存击穿问题也叫热点key问题，就是一个被高并发访问并且缓存重建业务较复杂的key突然失效了，无数的请求访问这个key，都会去数据库中查询，造成数据库压力过大。
常用的解决方案：
1. 互斥锁，当缓存失效时，加锁，防止多个线程同时去数据库中查询数据，然后更新缓存。![img.png](src/main/resources/img/互斥锁.png)
   1. 性能差，大部分线程均需要等待一个线程去数据库中查询数据，然后更新缓存。
2. 逻辑过期时间，当缓存失效时，设置一个逻辑过期时间，当逻辑过期时间到达时，再去数据库中查询数据，然后更新缓存。
   ![img.png](src/main/resources/img/逻辑过期.png)

### 全局唯一ID
1. 数据库自增ID
   - ID规律性强，容易被猜测
   - ID长度受限于数据库字段类型
2. 全局ID生成器
   - 优点：ID长度不受限于数据库字段类型，ID规律性弱
![img.png](src/main/resources/img/全局ID生成器.png)
   格式如下:
   ![img_1.png](src/main/resources/img/ID生成格式.png)
### 超卖等线程安全问题解决方案
1. 悲观锁：添加同步锁，让线程串行执行
 - 优点：简单粗暴
 - 缺点：性能差，大部分线程均需要等待一个线程执行完毕
2. 乐观锁：使用CAS算法，让线程并行执行，在更新时候判断是否有其他线程在修改数据，如果有则重试，直到成功为止
 - 优点：性能好
 - 缺点：重试次数过多，会影响性能（可以通过判断库存是否大于0来减少重试次数）

### 分布式锁
分布式锁；满足分布式系统或集群模式下多进程可见并且保证互斥
最大的特点：互斥性，同一时间只有一个线程可以执行临界区代码
#### 分布式锁实现
![分布式锁.png](src%2Fmain%2Fresources%2Fimg%2F%B7%D6%B2%BC%CA%BD%CB%F8.png)
- 基于redis的分布式锁
实现分布式锁需要实现的两个基本方法：
- 获取锁：
   - setnx：当key不存在时，设置key的值为value，返回1；当key存在时，不设置key的值，返回0
   - expire：设置key的过期时间
   当redis服务所在的机器宕机时，会出现死锁，所以需要设置过期时间，防止死锁
  但是需要避免在设置锁和设置过期时间之间出现宕机，所以需要将两个操作合并成一个原子操作
  ``set lock thread1 EX 10 NX(设置锁的过期时间为10秒，当key不存在时，设置key的值为thread1，返回1；当key存在时，不设置key的值，返回0)``
   - 采用非阻塞式的方法去获取锁，当获取锁失败时，可以重试，直到获取锁成功为止
- 释放锁：
   - 手动释放 ``del thread1``
   - 超时释放
![redis分布式锁.png](src%2Fmain%2Fresources%2Fimg%2Fredis%B7%D6%B2%BC%CA%BD%CB%F8.png)
#### 分布式锁可能会出现的问题
由于业务代码线程阻塞的时间过长，导致redis分布式锁到期释放，其他线程获取到锁，导致业务代码重复执行
![业务代码超时阻塞.png](src%2Fmain%2Fresources%2Fimg%2F%D2%B5%CE%F1%B4%FA%C2%EB%B3%AC%CA%B1%D7%E8%C8%FB.png)
解决方法：释放锁的时候，判断锁是否是自己的，如果是自己的，则释放锁，否则不释放锁
- 改进redis分布式锁
1. 在获取锁时存入线程标识
2. 在释放锁时判断锁是否是自己的
#### 分布式锁优化
目前存在的问题
1. 不可重入：同一个线程在没有释放锁之前，不能再次获取锁。当线程在执行A方法时，获取到锁，然后在A方法中调用B方法，B方法也需要获取锁，但是由于锁已经被A方法获取，所以B方法获取锁失败，导致B方法无法执行。
    解决：通过借助哈希数据结构来实现可重入锁![可重入锁原理.png](src%2Fmain%2Fresources%2Fimg%2F%BF%C9%D6%D8%C8%EB%CB%F8%D4%AD%C0%ED.png)
   2. 不可重试：获取锁只尝试一次就返回false，没有重试机制
       解决：获取锁时，在重试时间内，借助订阅和信号量机制避免无限制重试，占用cpu
       ```
      if (ttl >= 0L && ttl < time) {
       ((RedissonLockEntry)subscribeFuture.getNow()).getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
        } else {
            ((RedissonLockEntry)subscribeFuture.getNow()).getLatch().tryAcquire(time, TimeUnit.MILLISECONDS);
        }
   ```
3. 超时释放
    利用watchDog，每隔一段时间（releaseTime/3）去检查锁是否过期，如果过期则释放锁
    ```
    private void scheduleExpirationRenewal(long threadId) {
        if (lockWatchdogTimeout > 0) {
            lockExpirationTask = redisson.getCommandExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    renewExpiration();
                }
            }, lockWatchdogTimeout, TimeUnit.MILLISECONDS);
        }
    }
    ```
4. 主从一致性：如果redis提供了主从集群，主从同步存在延迟，此时主节点发生了宕机，如果从并同步中的锁数据还未同步到从节点，此时从节点会被选举为主节点，导致锁数据丢失
    - 借助multiLock联锁机制，将多个锁绑定在一起，只有当所有可重入锁都获取成功时，才算获取成功
```
    RLock lock1 = redisson.getLock("lock1");
    RLock lock2 = redisson.getLock("lock2");
    RLock lock3 = redisson.getLock("lock3");
    RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
```


借助redisson框架解决以上问题，没有必要自己实现锁，直接使用redisson框架提供的锁即可


### 秒杀业务优化
原本业务需要通过tomcat进行库存判断和一人一单，但这样导致业务速度变慢，所以需要将库存判断和一人一单放到redis中，通过异步获取消息队列中的消息进行处理
![img.png](src/main/resources/img/秒杀消息队列.png)
通过lua脚本实现库存判断和一人一单
- 优化
    - 新增秒杀优惠券的同时，将优惠券信息保存到redis中
    - 基于lua脚本，判断秒杀库存，一人一单，决定用户是否抢购成功
    - 如果抢购成功，将优惠券信息和用户id封装后放入阻塞队列中
    - 开启线程任务，不断从阻塞队列中获取信息，实现异步下单功能
![img.png](src/main/resources/img/异步秒杀lua+消息队列.png)
#### 基于阻塞队列的异步秒杀存在哪些问题
- 内存限制问题。 阻塞队列中的数据是在内存中的，如果阻塞队列中的数据过多，会导致内存溢出
- 数据安全问题。
#### 基于PubSub的消息队列
- 优点：采用发布订阅模式，支持多生产多消费
- 缺点：
  - 不支持消息持久化
  - 无法避免消息丢失。当发布者下线时，无法收到消息，导致消息丢失
  - 消息堆积有上线，超出时数据丢失
#### 基于Stream的消息队列
Stream是一种新的数据类型，支持持久化，可以实现一个功能完善的消息队列。
```

  XADD key [NOMKSTREAM] [MAXLEN|MINID [=|~] threshold [LIMIT count]] *|id field value [field value ...]
  summary: Appends a new entry to a stream
  since: 5.0.0
  group: stream
  
  NOMKSTREAM: Don't create the stream if it does not exist.
  id:消息的唯一id，*表示自动生成，格式为时间戳+递增数字，例如：1600000000001-0
  filed:消息的key
   value:消息的value
   
   简单示例：XADD mystream * name jack //向mystream中添加一条消息，id为自动生成，key为name，value为jack
   
   
   XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key ...] ID [ID ...]
   COUNT：表示一次读取的消息数量
    BLOCK：表示阻塞时间，当没有消息时，阻塞时间到了，会返回空
    STREAMS：表示一次读取多个stream的消息
    ID：表示读取的起始id，如果是*表示从最新的消息开始读取，如果是0-0表示从最早的消息开始读取
    
    简单示例：XREAD COUNT 2 STREAMS mystream 0-0 //从mystream中读取2条消息，从最早的消息开始读取
    XREAD COUNT 2 STREAMS mystream $ //从mystream中读取2条消息，从最新的消息开始读取
    XREAD COUNT 1 BLOCK 1000 STREAMS mystream 0-0 //从mystream中读取1条消息，从最早的消息开始读取，如果没有消息，阻塞1秒，如果还没有消息，返回空
    通过这种方式，循环调用XREAD BLOCK，可以实现阻塞队列的功能
```
注意：当我们指定起始ID为$时，代表读取最新的消息，如果我们处理一条消息的过程中，又有超过1条以上的消息到达队列。则下次获取时也只能获取到最新的一条，会出现漏读消息的问题。
特点：
- 消息可持久化
- 消息可以被多个消费者读取
- 消息可以阻塞读取
- 消息有漏读的风险
#### 消费者组
消费者组：将多个消费者划分到一个组中，监听同一个队列。具备下列特点：
- 消息分流：队列中的消息会分流给组内的不同消费者，而不是重复消费，从而加快消费处理的速度
- 消息标识：消费者组会维护一个标示，记录最后一个被处理的消息，哪怕消费者宕机重启，还会从标示之后读取消息，确保每一个消息都会被消费。
- 消费确认：消费者获取消息后，消息确认pending状态，并存入一个pending-list。当处理完成后需要通过XACK命令将消息确认为delivered状态，从而从pending-list中删除。
```
XGROUP CREATE key groupname ID [MKSTREAM]
key：队列名称
groupname：消费者组名称
ID：表示消费者组的标示，如果是0-0表示从最早的消息开始消费，如果是$表示从最新的消息开始消费
MKSTREAM：表示如果队列不存在，是否创建队列

其他常见命令
# 删除指定的消费者组
XGROUP DESTROY key groupname

# 给指定的消费者组添加消费者
XGROUP CREATECONSUMER key groupname consumername

# 删除指定消费者组中的指定消费者
XGROUP DELCONSUMER key groupname consumername

# 从消费者组读取消息
XREADGROUP GROUP groupname consumername COUNT count [BLOCK millseconds] [NOACK] STREAMS key [key ...] ID [ID ...]
count：表示一次读取的消息数量
millseconds：表示阻塞时间，当没有消息时，阻塞时间到了，会返回空
NOACK：表示不需要确认消息
>:表示从最新的消息开始读取

简单示例：XREADGROUP GROUP group1 consumer1 COUNT 1 STREAMS mystream 0-0 //从mystream中读取1条消息，从最早的消息开始读取，如果没有消息，阻塞1秒，如果还没有消息，返回空


# 消息确认
XACK key groupname ID [ID ...]
简单示例：XACK mystream group1 1600000000001-0 //将1600000000001-0消息确认为已消费
```
![redis消息队列.png](src%2Fmain%2Fresources%2Fimg%2Fredis%CF%FB%CF%A2%B6%D3%C1%D0.png)