package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.request.ProductRequest;
import com.example.sell_lp.dto.request.ProductUpdateRequest;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.product.ProductService;
import com.example.sell_lp.service.category.CategoryService;
import com.example.sell_lp.service.variant.ColorService;
import com.example.sell_lp.service.variant.RamService;
import com.example.sell_lp.service.variant.RomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminProductController {

    ColorService colorService;

    RamService ramService;

    RomService romService;

    ProductService productService;

    CategoryService categoryService;


    @GetMapping
    public String getAllProducts(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "productId,asc") String sortField) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        String[] sortParams = sortField.split(",");
        String property = sortParams[0];
        String directionStr = (sortParams.length > 1) ? sortParams[1] : "desc";

        Sort.Direction direction = directionStr.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, property);
        PageRequest pageable = PageRequest.of(page - 1, size, sort);

        Page<ProductResponse> productPage = productService.getProductsFiltered(
                categoryId,
                keyword,
                directionStr.toLowerCase(),
                pageable
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("categories", categoryService.findAll());

        return "admin/product-manage";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("rams", ramService.getAllRam());
        model.addAttribute("roms", romService.getAllRom());
        return "admin/product-form";
    }
    @PostMapping("/add")
    public String createProduct(@ModelAttribute("productRequest") ProductRequest request) {
        productService.createProduct(request);
        return "redirect:/admin/products?success";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
}