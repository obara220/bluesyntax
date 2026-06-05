package com.panda.aoodds.esport.common.service;

import com.panda.aoodds.esport.common.config.RedisConfig;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis操作实现类
 * Created by macro on 2020/3/3.
 */
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    Random random = new Random();

    /**
     * 加锁脚本
     */
    String SRIPT_LOCK = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end else return 0 end";
    /**
     * 解锁脚本
     */
    String SRIPT_UNLOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final Long SUCCESS = 1L;

    @Override
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    @Override
    public Long delete(Set<String> keys) {
        return redisTemplate.delete(keys);
    }


    @Override
    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value) {
        //默认过期时间1天
        redisTemplate.opsForValue().set(key, value, RedisConfig.REDIS_DEFAULT_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void setPermanent(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }
    @Override
    public Boolean hSetField(String key,String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key,hashKey, value);
        redisTemplate.opsForHash().put("expire_"+key, hashKey, System.currentTimeMillis()+(time*1000L));
        RedisKeyConstant.redisHashKey.add("expire_"+key);
        return expire(key, time);
    }
    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public <V, E> Map<V, E> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public <E> Boolean hSetAll(String key, Map<String, E> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    @Override
    public void hSetAll(String key, Map<String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long sAdd(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lPush(String key, Object value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }

    @Override
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long lPushAll(String key, Long time, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * @param key        需要加锁的对象，如赛事id
     * @param hashValue  锁ID（可以理解成自己随机生成的,用于标识）
     *                   当前redis锁时谁占有的,如linkId
     * @param expireTime key 失效时间 (秒)
     * @param timeOut    重试时间 (秒)
     * @return
     */
    @Override
    public boolean tryLock(String key, String hashValue, int expireTime, int timeOut) {
        //log.info("开始尝试获取锁，key：{},hashValue:{}",key,hashValue);
        Long waitMax = TimeUnit.SECONDS.toMillis(timeOut);
        Long waitAlready = 0L;
        // 尝试操作
        boolean succeed = false;
        while (waitAlready <= waitMax) {
            succeed = lock(key, hashValue, expireTime);
            if (succeed) {
                break;
            }
            try {
                //如果一个Client无法获得锁，它将在一个随机延时后开始重试。
                // 使用随机延时的目的是为了与其他申请同一个锁的Client错开申请时间，减少脑裂(split brain)发生的可能性
                int randomTime = random.nextInt(10) + 2;
                Thread.sleep(randomTime);
                waitAlready += randomTime;
            } catch (Exception e) {
                log.info(String.format("分布式锁创建失败, key: %s value: %s", key, hashValue), e);
            }
        }
        if (!succeed) {
            log.info(String.format("获取分布式锁返回false, key: %s value: %s", key, hashValue));
        }
        return succeed;
    }

    /**
     * @param key       需要释放锁的对象，如赛事id
     * @param hashValue 锁ID（可以理解成自己随机生成的,用于标识）
     *                  当前redis锁时谁占有的,如linkId
     * @return
     */
    @Override
    public boolean unLock(String key, String hashValue) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SRIPT_UNLOCK, Long.class);
        Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(key), hashValue);
        return SUCCESS.equals(result);
    }

    @Override
    public boolean tryLockOnce(String key, String hashValue, int expireTime) {
        return lock(key, hashValue, expireTime);
    }

    private boolean lock(String key, String lockId, int expireTime) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(SRIPT_LOCK);
            redisScript.setResultType(Long.class);
            Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(key), lockId, expireTime);
            return SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("创建redis锁时发生异常", e);
        }
        return false;
    }


    /**
     * 判断X分钟内只能消费一次
     *
     * @param key          key
     * @param expireMillis 过期时间
     * @return
     */
    public Boolean barrier(String key, Long expireMillis) {
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (1 == count) {
            redisTemplate.expire(key, expireMillis, TimeUnit.MILLISECONDS);
        }
        if (count > 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
