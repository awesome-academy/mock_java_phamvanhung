package sun.asterisk.booking_tour.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.asterisk.booking_tour.dto.category.CategoryRequest;
import sun.asterisk.booking_tour.dto.category.CategoryResponse;
import sun.asterisk.booking_tour.dto.category.CategoryStatsResponse;
import sun.asterisk.booking_tour.dto.common.PageResponse;
import sun.asterisk.booking_tour.entity.Category;
import sun.asterisk.booking_tour.enums.CategoryStatus;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.repository.CategoryRepository;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        
        List<CategoryResponse> responses = categoryPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<CategoryResponse>builder()
                .content(responses)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .isLast(categoryPage.isLast())
                .isFirst(categoryPage.isFirst())
                .hasNext(categoryPage.hasNext())
                .hasPrevious(categoryPage.hasPrevious())
                .build();
    }

    public List<CategoryResponse> getAllCategoriesList() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy category với ID: " + id));
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Kiểm tra trùng tên
        String slug = generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Category với tên '" + request.getName() + "' đã tồn tại");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(slug)
                .status(CategoryStatus.ACTIVE)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category: {}", savedCategory.getName());
        
        return mapToResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy category với ID: " + id));

        // Kiểm tra trùng tên nếu tên thay đổi
        String newSlug = generateSlug(request.getName());
        if (!category.getSlug().equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            throw new IllegalArgumentException("Category với tên '" + request.getName() + "' đã tồn tại");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(newSlug);

        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category: {}", updatedCategory.getName());
        
        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy category với ID: " + id));

        // Kiểm tra xem category có tour không
        if (category.getTours() != null && !category.getTours().isEmpty()) {
            throw new IllegalStateException("Không thể Delete category đang có tour. Vui lòng chuyển trạng thái sang INACTIVE");
        }

        categoryRepository.delete(category);
        log.info("Deleted category: {}", category.getName());
    }

    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy category với ID: " + id));

        CategoryStatus newStatus = category.getStatus() == CategoryStatus.ACTIVE 
                ? CategoryStatus.INACTIVE 
                : CategoryStatus.ACTIVE;
        
        category.setStatus(newStatus);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Toggled category status: {} -> {}", category.getName(), newStatus);
        
        return mapToResponse(updatedCategory);
    }

    public CategoryStatsResponse getCategoryStats() {
        long total = categoryRepository.count();
        long active = categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == CategoryStatus.ACTIVE)
                .count();
        long inactive = total - active;

        return CategoryStatsResponse.builder()
                .totalCategories(total)
                .activeCategories(active)
                .inactiveCategories(inactive)
                .build();
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .status(category.getStatus())
                .tourCount(category.getTours() != null ? (long) category.getTours().size() : 0L)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String generateSlug(String name) {
        // Normalize Vietnamese characters
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");
        
        // Convert to lowercase and replace special characters
        return withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
