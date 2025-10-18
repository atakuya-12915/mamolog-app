package com.example.mamolog.controller.auth;


import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.mamolog.entity.Role;
import com.example.mamolog.entity.User;
import com.example.mamolog.repository.RoleRepository;
import com.example.mamolog.repository.UserRepository;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;        
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
    	  // メール重複チェック
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            model.addAttribute("error", "このメールアドレスは既に登録されています");
            return "register";
        }

        // パスワードをハッシュ化
        user.setPassword(passwordEncoder.encode(user.getPassword()));

     // デフォルトRoleを取得（例: ID=2がUSER/標準ユーザー）
        Role defaultRole = roleRepository.findById(2L).orElse(null);
        if(defaultRole == null){
            model.addAttribute("error", "ユーザー権限が設定されていません。管理者に連絡してください。");
            return "register";
        }
        user.setRole(defaultRole);


        // DBに保存
        userRepository.save(user);

        model.addAttribute("success", "アカウントが作成されました。ログインしてください");
        return "redirect:/login";
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication auth) {
        if(auth == null || auth.getName() == null){
            model.addAttribute("error", "ログイン情報が取得できません");
            return "login";
        }

        String email = auth.getName();

        // null安全にユーザー取得
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            model.addAttribute("error", "ユーザー情報が見つかりません");
            return "login";
        }

        model.addAttribute("userEmail", email);
        model.addAttribute("user", user);
        return "mypage";
    }

}
