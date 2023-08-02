package com.kfh.kfh.controller;

import com.kfh.kfh.dao.CourseRepository;
import com.kfh.kfh.entity.Course;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @GetMapping
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        return courseRepository.save(existingCourse);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }


    @PatchMapping("/{id}")
    public Course patchCourse(@PathVariable Long id, @RequestBody Course patchedCourse) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));

        // Apply partial updates only for non-null properties
        BeanUtils.copyProperties(patchedCourse, existingCourse, getNullPropertyNames(patchedCourse));

        return courseRepository.save(existingCourse);
    }

    // Helper method to get the names of null properties in the patchedCourse
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl (source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<> ();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    @GetMapping("/list")
    public ResponseEntity<List<Course>> getCoursesByIdList() {
        List<Course> courses = courseRepository.findAll ();

        if (!courses.isEmpty()) {
            return ResponseEntity.ok(courses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
