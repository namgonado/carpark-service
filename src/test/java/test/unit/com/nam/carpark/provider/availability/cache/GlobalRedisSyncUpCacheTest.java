package test.unit.com.nam.carpark.provider.availability.cache;

import com.nam.carpark.provider.availability.cache.GlobalRedisSyncUpCache;
import com.nam.carpark.utils.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalRedisSyncUpCacheTest {

    @InjectMocks
    private GlobalRedisSyncUpCache globalRedisSyncUpCache;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getCacheType() {
        assertEquals("GLOBAL_REDIS", globalRedisSyncUpCache.getCacheType());
    }

    @Test
    void contains() {
        when(redisTemplate.hasKey("BM29")).thenReturn(true);
        assertTrue(globalRedisSyncUpCache.contains("BM29"));
        verify(redisTemplate, times(1)).hasKey("BM29");
    }

    @Test
    void getUpdateTime() {
        String dateTimeStr = "2024-12-17T12:40:27";
        when(valueOperations.get("BM29")).thenReturn(dateTimeStr);
        LocalDateTime updateTime = globalRedisSyncUpCache.getUpdateTime("BM29");
        assertNotNull(updateTime);
        assertEquals(LocalDateTime.parse(dateTimeStr), updateTime);
        verify(valueOperations, times(1)).get("BM29");
    }

    @Test
    void putUpdateTime() {
        LocalDateTime updateTime = LocalDateTime.of(2024, 12, 17, 12, 40, 27);
        globalRedisSyncUpCache.putUpdateTime("BM29", updateTime);
        verify(valueOperations, times(1)).set("BM29", DateTimeUtils.getDateTimeAsString(updateTime));
    }
}