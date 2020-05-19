package com.areaShop.controller;

import com.areaShop.model.Product;
import com.areaShop.service.ProductService;
import com.areaShop.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class ProductController {

    private static final int INITIAL_PAGE = 0;
    private ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/home")
    public ModelAndView home(@RequestParam("page") Optional<Integer> page) {

        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

        Page<Product> products = productService.findAllProductsPageable(new PageRequest(evalPage, 5));
        Pager pager = new Pager(products);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("products", products);
        modelAndView.addObject("pager", pager);
        modelAndView.setViewName("/home");
        return modelAndView;
    }


    @GetMapping("/productEdit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        model.addAttribute("product", product);
        return "productEdit";
    }

    @PostMapping("/updateProduct/{id}")
    public String updateProduct(
            @PathVariable("id") long id,
            @Valid Product product,
             BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            return "productEdit";
        }
        productService.save(product);
        model.addAttribute("products", productService.findAll());
        return "redirect:/home";
    }


    @GetMapping("/deleteProduct/{id}")
    public ModelAndView deleteProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        productService.delete(product);
        model.addAttribute("product", productService.findAll());
        return home(Optional.of(0));
    }

    @PostMapping("/createProduct")
    public String addProduct(@Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "addProduct";
        }
        productService.save(product);
        model.addAttribute("users", productService.findAll());
        return "redirect:/home";
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.GET)
    public ModelAndView getProductPage() {
        ModelAndView modelAndView = new ModelAndView();
        Product product = new Product();
        modelAndView.addObject("product", product);
        modelAndView.setViewName("/addProduct");
        return modelAndView;
    }

}
