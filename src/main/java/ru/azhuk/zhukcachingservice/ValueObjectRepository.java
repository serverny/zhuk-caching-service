package ru.azhuk.zhukcachingservice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.azhuk.zhukcachingservice.model.ValueObject;

/**
 * Created by zhuk1 on 26.09.2018.
 */
@Repository
@Component
public interface ValueObjectRepository extends CrudRepository<ValueObject, String> {

}
