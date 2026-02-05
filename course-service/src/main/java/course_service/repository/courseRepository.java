package course_service.repository;

import course_service.entity.courseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface courseRepository  extends JpaRepository<courseEntity,Long> {

}
