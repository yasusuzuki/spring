package demo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "todolist", path = "todolist")

public interface  ToDoRepository extends  PagingAndSortingRepository<Todo, Long>{

}
