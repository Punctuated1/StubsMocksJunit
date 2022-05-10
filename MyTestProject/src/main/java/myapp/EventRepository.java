package myapp;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"prod","qa","int","dev"})
public interface EventRepository extends CrudRepository<EventDetail, String> {

}
