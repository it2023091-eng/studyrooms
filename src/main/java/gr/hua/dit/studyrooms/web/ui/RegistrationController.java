package gr.hua.dit.studyrooms.web.ui;


import gr.hua.dit.studyrooms.core.model.User;
import gr.hua.dit.studyrooms.core.model.UserRole;
import gr.hua.dit.studyrooms.core.repository.UserRepository;
import gr.hua.dit.studyrooms.web.ui.model.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("form") RegisterForm form,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmailIgnoreCase(form.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists");
            return "register";
        }

        User user = new User();
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole(UserRole.valueOf(form.getRole()));

        userRepository.save(user);

        return "redirect:/login";
    }
}
