package com.areaShop.controller;

import com.areaShop.model.ConfirmationToken;
import com.areaShop.model.User;
import com.areaShop.repository.ConfirmationTokenRepository;
import com.areaShop.service.EmailSenderService;
import com.areaShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public UserController(UserService userService, EmailSenderService emailSenderService, ConfirmationTokenRepository confirmationTokenRepository) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("/registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        User userForCheck = userService.findByUsername(user.getUsername());
        ModelAndView modelAndView = new ModelAndView();
        if (userForCheck != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
            modelAndView.setViewName("/registration");

        } else {
            userService.saveUser(user);

            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("/registration");
        }
        return modelAndView;
    }

    @GetMapping("/userEdit")
    public ModelAndView userGet() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("/userEdit");
        return modelAndView;
    }

    @GetMapping("/findUser")
    public ModelAndView findUser(@Valid User userDetails, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (userService.findByUsername(userDetails.getUsername()) == null) {
            bindingResult
                    .rejectValue("username", "error.user",
                    "There is no registered user with the username provided");
        } else {

            modelAndView.addObject("user", userService.findByUsername(userDetails.getUsername()));
        }
        modelAndView.setViewName("/userEdit");
        return modelAndView;
    }

    @PostMapping("/updateUser")
    public ModelAndView updateProduct(
            @Valid User user,
            BindingResult result ){
        ModelAndView modelAndView = new ModelAndView();
        User userForSave = userService.findByUsername(user.getUsername());
        if (result.hasErrors()) {
            user.setUsername(user.getUsername());
            modelAndView.setViewName("/userEdit");
        }
        userForSave.setUsername(user.getUsername());
        userForSave.setLastName(user.getLastName());
        userForSave.setActive(user.getActive());
        userForSave.setPassword(user.getPassword());
        userForSave.setName(user.getName());
        if(user.getEmail() != null) {
            userForSave.setEmail(user.getEmail());
        }
        userService.save(userForSave);
        return userGet();
    }

    @GetMapping("/delete/{username}")
    public ModelAndView deleteUser(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        ModelAndView modelAndView = new ModelAndView();
        if (user == null){
            modelAndView.addObject("username", "User was not deleted");
            modelAndView.setViewName("/userEdit");
            return modelAndView;
        }
        userService.delete(user);
        return userGet();
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    // Receive the address and send an email
    @RequestMapping(value="/forgot-password", method=RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            // Create token
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
            confirmationTokenRepository.save(confirmationToken);
            emailSenderService.sendEmail(existingUser, confirmationToken);

            modelAndView.addObject("message", "Request to reset password received. Check your inbox for the reset link.");
            modelAndView.setViewName("successForgotPassword");

        } else {
            modelAndView.addObject("message", "This email address does not exist!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null) {
            User user = userService.findByEmail(token.getUser().getEmail());
            userService.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("emailId", user.getEmail());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }


    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {

        if(user.getEmail() != null) {
            User tokenUser = userService.findByEmail(user.getEmail());
            tokenUser.setPassword(userService.getPasswordEncoder().encode(user.getPassword()));
            userService.save(tokenUser);
            modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
            modelAndView.setViewName("successResetPassword");
        } else {
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}
