package com.sociopool.usermanagement.controller;

import com.sociopool.usermanagement.CustomerRepository;
import com.sociopool.usermanagement.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @GetMapping("/registration")
    public String register(Model model){
        model.addAttribute("customer",new Customer());
        return "register.html";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute Customer customer, Model model){
        Optional<Customer> one = Optional.ofNullable(repository.findByEmail(customer.getEmail()));
        if(one.isPresent())
        {
            model.addAttribute("msg","Email already registered");
            return "register.html";
        }
        customer.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        repository.save(customer);
        return "login.html";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            return "redirect:/home";
        }
        model.addAttribute("customer",new Customer());
        return "login.html";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Customer customer, HttpServletRequest request){
        Optional<Customer> one = Optional.ofNullable(repository.findByEmail(customer.getEmail()));
        if(!one.isPresent()) return "login.html";
        HttpSession session = request.getSession();
        session.setAttribute("name",one.get().getName());
        if (new BCryptPasswordEncoder().matches(customer.getPassword(), one.get().getPassword()))
            return "redirect:/home";
        else return "login.html";
    }

    @GetMapping("/home")
    public String home(){
        return "home.html";
    }

    @GetMapping("/logout")
    public String Logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "login.html";
    }

}
