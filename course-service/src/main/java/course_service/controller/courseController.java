package course_service.controller;

import course_service.entity.courseEntity;
import course_service.service.courseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class courseController {
    private final courseService service;
    public courseController(courseService service) { this.service = service; }

    @GetMapping
    public List<courseEntity> getAllCourses() { return service.getAllCourses(); }

    @GetMapping("/{id}")
    public courseEntity getCourse(@PathVariable Long id) { return service.getCourseById(id); }

    @PostMapping
    public courseEntity createCourse(@RequestBody courseEntity course) { return service.createCourse(course); }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) { service.deleteCourse(id); }
}
