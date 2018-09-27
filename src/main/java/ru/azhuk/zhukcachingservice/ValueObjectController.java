package ru.azhuk.zhukcachingservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.azhuk.zhukcachingservice.cache.ValueObjectCache;
import ru.azhuk.zhukcachingservice.model.ValueObject;
import ru.azhuk.zhukcachingservice.model.ValueObjectImpostor;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by zhuk1 on 26.09.2018.
 */

@RestController
public class ValueObjectController {

    private final ValueObjectCache valueObjectCache;

    @Autowired
    public ValueObjectController(final ValueObjectCache valueObjectCache) {
        this.valueObjectCache = valueObjectCache;
    }

    @GetMapping("/{id}")
    public ResponseEntity getValueObject(@PathVariable("id") final String id) {
        Optional<ValueObject> result = valueObjectCache.get(id);
        ResponseEntity response;
        if (result.isPresent()) {
            response = new ResponseEntity(result.get(), HttpStatus.OK);
        } else {
            response = new ResponseEntity(new ValueObjectImpostor(id), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PostMapping("/")
    public ResponseEntity saveValueObject(@Valid @RequestBody ValueObject valueObject) {
        valueObjectCache.save(valueObject);
        return new ResponseEntity(new ValueObjectImpostor(valueObject.getKey()), HttpStatus.OK);
    }

}
