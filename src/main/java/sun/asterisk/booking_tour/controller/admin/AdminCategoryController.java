package sun.asterisk.booking_tour.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.asterisk.booking_tour.dto.category.CategoryRequest;
import sun.asterisk.booking_tour.dto.category.CategoryResponse;
import sun.asterisk.booking_tour.dto.category.CategoryStatsResponse;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesList();
            CategoryStatsResponse stats = categoryService.getCategoryStats();
            
            model.addAttribute("categories", categories);
            // Pass individual values to avoid Spring 6 ACL restrictions
            model.addAttribute("totalCategories", stats.getTotalCategories());
            model.addAttribute("activeCategories", stats.getActiveCategories());
            model.addAttribute("inactiveCategories", stats.getInactiveCategories());
            
            log.info("Fetched {} categories", categories.size());
            return "admin/categories";
        } catch (Exception e) {
            log.error("Error fetching categories", e);
            model.addAttribute("error", "Error loading category list: " + e.getMessage());
            return "admin/categories";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCategoriesApi() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesList();
            CategoryStatsResponse stats = categoryService.getCategoryStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching categories API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            CategoryResponse category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching category by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            CategoryResponse category = categoryService.createCategory(request);
            log.info("Created category: {}", category.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating category: " + e.getMessage()));
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            CategoryResponse category = categoryService.updateCategory(id, request);
            log.info("Updated category: {}", category.getName());
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating category: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            log.info("Deleted category with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting category: " + e.getMessage()));
        }
    }

    @PatchMapping("/api/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable Long id) {
        try {
            CategoryResponse category = categoryService.toggleCategoryStatus(id);
            log.info("Toggled category status: {} -> {}", category.getName(), category.getStatus());
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error toggling category status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error changing category status: " + e.getMessage()));
        }
    }
}
