package course_service.service;

import course_service.entity.courseEntity;
import course_service.repository.courseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class courseService {
    private final courseRepository repo;

    public courseService(courseRepository repo) {
        this.repo = repo;
    }

    public List<courseEntity> getAllCourses() { return repo.findAll(); }

    public courseEntity getCourseById(Long id) { return repo.findById(id).orElse(null); }

    public courseEntity createCourse(courseEntity course) { return repo.save(course); }

    public void deleteCourse(Long id) { repo.deleteById(id); }
}
