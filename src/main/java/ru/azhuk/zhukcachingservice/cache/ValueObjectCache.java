package ru.azhuk.zhukcachingservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.azhuk.zhukcachingservice.ValueObjectRepository;
import ru.azhuk.zhukcachingservice.ZhukCachingServiceApplication;
import ru.azhuk.zhukcachingservice.model.ValueObject;

import java.util.Optional;

@Component
@CacheConfig(cacheNames={"objectsCache"})
public class ValueObjectCache {

    private static final Logger logger = LoggerFactory.getLogger(ZhukCachingServiceApplication.class);
    private final ValueObjectRepository valueObjectRepository;

    @Autowired
    public ValueObjectCache(final ValueObjectRepository valueObjectRepository) {
        this.valueObjectRepository = valueObjectRepository;
    }

    @Cacheable(key = "#id")
    public Optional<ValueObject> get(String id) {
        logger.info("Getting object from DB by key :" + id);
        return valueObjectRepository.findById(id);
    }

    @CachePut(key = "#object.key")
    public ValueObject save(ValueObject object) {
        logger.info("Saving object to DB with key: " + object.getKey());
        return valueObjectRepository.save(object);
    }

    @CacheEvict(key = "#id")
    public void evictById(String id) {
        logger.info("Evict from cache object with key: " + id);
    }

    @CacheEvict(allEntries = true)
    public void evictAll() {
        logger.info("Clean cache completely");
    }

    @CacheEvict(key = "#id", beforeInvocation = true)
    public void deleteById(String id) {
        logger.info("Delete object both from cache and DB, key: " + id);
        valueObjectRepository.deleteById(id);
    }
}

