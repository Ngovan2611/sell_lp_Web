package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.request.ProductRequest;
import com.example.sell_lp.dto.request.ProductUpdateRequest;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.product.AdminProductService;
import com.example.sell_lp.service.category.CategoryService;
import com.example.sell_lp.service.product.ProductService;
import com.example.sell_lp.service.variant.ColorService;
import com.example.sell_lp.service.variant.ImageUploadService;
import com.example.sell_lp.service.variant.RamService;
import com.example.sell_lp.service.variant.RomService;
import jakarta.validation.Valid;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminProductController {

    ColorService colorService;

    RamService ramService;

    RomService romService;

    AdminProductService adminProductService;

    ProductService productService;
    CategoryService categoryService;
    ImageUploadService imageUploadService;

    @GetMapping
    public String getAllProducts(
            Model model,

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Integer category,

            @RequestParam(required = false) String stock,

            @RequestParam(required = false) Boolean active,

            @RequestParam(required = false, defaultValue = "newest")
            String sort,

            @RequestParam(defaultValue = "1")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        PageRequest pageable = PageRequest.of(page - 1, size);

        Page<ProductResponse> productPage =
                adminProductService.getAdminProducts(
                        keyword,
                        category,
                        stock,
                        active,
                        sort,
                        pageable
                );

        model.addAttribute("products", productPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("stock", stock);
        model.addAttribute("active", active);
        model.addAttribute("sort", sort);
        model.addAttribute("lowStockCount",
                adminProductService.countLowStockProducts());
        model.addAttribute("categories",
                categoryService.findAll());

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
    public String createProduct(@Valid @ModelAttribute("productRequest") ProductRequest request) {
        adminProductService.createProduct(request);
        return "redirect:/admin/products?success";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductUpdateRequest request) {
        ProductResponse response = adminProductService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    // Thêm ImageUploadService vào constructor
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String url = imageUploadService.uploadImage(file); // Service đã viết ở bước trước
                urls.add(url);
            }
            return ResponseEntity.ok(urls);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}