package com.example.mamolog.controller.auth;


import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.mamolog.entity.User;
import com.example.mamolog.repository.UserRepository;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication auth) {
        // Authentication からログインユーザー名（メールアドレス）を取得　＊Spring Security
    	String email = auth.getName();

    	// DBからユーザー情報を取得したい場合（任意）
        User user = userRepository.findByEmail(email).orElseThrow();

        // 画面に渡す値を設定
        model.addAttribute("userEmail", email);  // ← これでメールアドレス表示OK
        model.addAttribute("user", user);        // ← 必要ならユーザー全体も渡す

    	    return "mypage";
    }

}
