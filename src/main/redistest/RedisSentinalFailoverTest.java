
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by lenovo on 2019/1/16.
 */
@Slf4j
public class RedisSentinalFailoverTest {

    public static void main(String[] args) {
        String masterName = "mymaster";
        Set<String> sentinels = new HashSet<>();
        sentinels.add("192.168.41.129:26379");
        sentinels.add("192.168.41.129:26380");
        sentinels.add("192.168.41.129:26381");

        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName,sentinels);

        while(true) {
            Jedis jedis = null;
            try {
                jedis = jedisSentinelPool.getResource();
                int index = new Random().nextInt(1000000);
                String key = "k- " + index;
                String value = "v- " + index;
                jedis.set(key,value);
                log.info("key:{},value:{}",key,jedis.get(key));
                TimeUnit.MICROSECONDS.sleep(10);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if(jedis != null)
                    jedis.close();
            }
        }
    }
}
