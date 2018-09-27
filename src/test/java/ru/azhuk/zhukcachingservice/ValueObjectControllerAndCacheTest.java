package ru.azhuk.zhukcachingservice;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.azhuk.zhukcachingservice.cache.ValueObjectCache;
import ru.azhuk.zhukcachingservice.model.ValueObject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// This works more like integration test than unit test
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ValueObjectControllerAndCacheTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValueObjectRepository objectRepository;

    @Autowired
    private ValueObjectCache objectCache;

    @Autowired
    private CacheManager cacheManager;

    private final ValueObject VALUE_OBJECT = new ValueObject("1", "1");
    private final String KEY_VALUE_RESPONSE = "{\"key\":\"1\"}";
    private final String VALUE_OBJECT_JSON = new Gson().toJson(VALUE_OBJECT);

    @Before
    public void setUp() {
        cleanAll();
    }

    void cleanAll() {
        objectCache.evictAll();
        objectRepository.deleteAll();
    }

    @Test
    public void whenTryingToFindAbsentObject() throws Exception {
        cleanAll();
        mockMvc.perform(get("/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(KEY_VALUE_RESPONSE)));
    }

    private void setUpValueObject() {
        objectRepository.save(VALUE_OBJECT);
    }

    @Test
    public void whenTryingToFindExistingObject() throws Exception {
        setUpValueObject();
        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"key\":\"1\",\"value\":\"1\"}")));
    }

    @Test
    public void whenUsingPostToAddObject() throws Exception {
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON).content(VALUE_OBJECT_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(KEY_VALUE_RESPONSE)));
    }

    @Test
    public void shouldGetIntoCacheAfterPost() throws Exception {
        // assert that object not in cache
        cleanAll();
        Cache cache = cacheManager.getCache("objectsCache");
        assertNotNull(cache);
        assertNull(cache.get("1"));

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON).content(VALUE_OBJECT_JSON))
                .andExpect(status().isOk());
        ValueObject objectFromCache = (ValueObject) cache.get("1").get();
        assertEquals(objectFromCache, VALUE_OBJECT);
    }

    @Test
    public void shouldGetIntoCacheAfterGet() throws Exception {
        // assert that object is in DB but not in cache
        setUpValueObject();
        objectCache.evictAll();
        Cache cache = cacheManager.getCache("objectsCache");
        assertNotNull(cache);
        assertNull(cache.get("1"));

        mockMvc.perform(get("/1"));
        ValueObject objectFromCache = (ValueObject) cache.get("1").get();
        assertEquals(objectFromCache, VALUE_OBJECT);
    }

    @Test
    public void shouldBeSameObjectInstanceInCacheAfterSeveralGets() throws Exception {
        setUpValueObject();
        objectCache.evictAll();
        Cache cache = cacheManager.getCache("objectsCache");
        assertNotNull(cache);
        mockMvc.perform(get("/1"));
        ValueObject objectFromCacheAfterFirstGet = (ValueObject) cache.get("1").get();
        mockMvc.perform(get("/1"));
        ValueObject objectFromCacheAfterSecondGet = (ValueObject) cache.get("1").get();

        assertSame(objectFromCacheAfterFirstGet, objectFromCacheAfterSecondGet);
    }

    @Test
    public void shouldBeDifferentObjectInCacheAfterUpdateViaPost() throws Exception {
        setUpValueObject();
        objectCache.evictAll();
        Cache cache = cacheManager.getCache("objectsCache");
        assertNotNull(cache);
        mockMvc.perform(get("/1"));
        ValueObject objectFromCacheAfterFirstGet = (ValueObject) cache.get("1").get();
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new ValueObject("1", "2"))));
        ValueObject objectFromCacheAfterSecondGet = (ValueObject) cache.get("1").get();

        assertNotEquals(objectFromCacheAfterFirstGet, objectFromCacheAfterSecondGet);
    }

/*
    TODO: does not work with current Spring Boot config, requires changes to DispatcherServlet and work with ResponseEntityExceptionHandler
    @Test(expected = MethodArgumentNotValidException.class)
    public void whenUsingPostToAddWrongObject() throws Exception {
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new WrongObject("1", "1"))));
    }

    class WrongObject implements Serializable {
        private String notKey;
        private String notValue;

        public WrongObject(String notKey, String notValue) {
            this.notKey = notKey;
            this.notValue = notValue;
        }

        public String getNotKey() {
            return notKey;
        }

        public void setNotKey(String notKey) {
            this.notKey = notKey;
        }

        public String getNotValue() {
            return notValue;
        }

        public void setNotValue(String notValue) {
            this.notValue = notValue;
        }
    }
*/
}