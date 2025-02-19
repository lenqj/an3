package SINU.sinu.controller;
import SINU.sinu.dto.MyUserAuthDTO;
import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.mapper.StudentMapper;
import SINU.sinu.service.EmailService;
import SINU.sinu.service.MyUserService;
import SINU.sinu.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/api/auth")
@AllArgsConstructor
public class AuthController {
    static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MyUserService myUserService;


    @GetMapping("register")
    public String showRegisterForm(Model model){
        model.addAttribute("myUserAuthDTO", new MyUserAuthDTO());
        return "auth/register";
    }

    @PostMapping("register")
    public String registration(@Valid @ModelAttribute MyUserAuthDTO myUserAuthDTO, Model model, RedirectAttributes redirectAttributes){
        MyUserDTO myUserDTO = studentService.findStudentDTOByEmail(myUserAuthDTO.getEmailAddress());
        if(myUserDTO != null){
            model.addAttribute("errors", "This email address is already in use");
            logger.warning("Email address already in use: " + myUserAuthDTO.getEmailAddress());
            return "auth/register";
        }
        StudentDTO myStudentDTOForSave = studentMapper.myUserAuthToStudentDTO(myUserAuthDTO);
        Student myStudentForSave = studentMapper.toEntity(myStudentDTOForSave, passwordEncoder.encode(myUserAuthDTO.getPassword()));
        studentService.createStudent(myStudentForSave);
        emailService.sendRegistrationEmail(myUserAuthDTO);
        redirectAttributes.addAttribute("registrationSuccess", "Success");
        logger.info("Registration successful for email: " + myUserAuthDTO.getEmailAddress());
        return "redirect:/api/auth/login";
    }

    @GetMapping("login")
    public String login(){
        return "auth/login";
    }
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication)
    {
        if(authentication != null){
            MyUserDTO userDto = myUserService.getLoginUser();
            model.addAttribute("user", userDto);
            logger.info("Accessing profile for user: " + userDto.getUsername());
        }
        return "profile/index";
    }
    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError", true);
        return "auth/login";
    }

}
