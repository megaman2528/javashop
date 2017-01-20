package ru.pelmegov.javashop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.pelmegov.javashop.api.service.RoleService;
import ru.pelmegov.javashop.api.service.UserService;
import ru.pelmegov.javashop.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/user")
public class UserController {

    private String indexView = "/admin/user/index";
    private String updateUserView = "/admin/user/updateUser";
    private String addUserView = "/admin/user/addUser";

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView(indexView);
        List<User> allUsers = new ArrayList<User>(userService.allUsers());
        // ToDo придумать как и где сортировать по другому по возрастанию ID юзеров
        Collections.sort(allUsers, new Comparator<User>() {
            public int compare(User o1, User o2) {
                Long i1 = o1.getId();
                Long i2 = o2.getId();
                return (i1 < i2 ? -1 : (i1.equals(i2) ? 0 : 1));
            }
        });
        modelAndView.addObject("users", allUsers);
        return modelAndView;
    }

    @RequestMapping(value = {"/addUser"}, method = RequestMethod.GET)
    public ModelAndView addUser(User user) {
        ModelAndView modelAndView = new ModelAndView(addUserView);
        return getUserModelAndView(modelAndView, user);
    }

    @RequestMapping(value = {"/addUser"}, method = RequestMethod.POST)
    public ModelAndView addUser(@Valid @ModelAttribute User user,
                                final BindingResult result,
                                ModelAndView modelAndView,
                                RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return getUserModelAndView(modelAndView, user);
        }
        // ToDO Реализовать. Попытка добавить пользователя с существующим логином
        if (userService.getUserByLogin(user.getLogin()) != null) {
            return getUserModelAndView(modelAndView, user);
        }
        userService.addUser(user);
        redirectAttrs.addFlashAttribute("success", "User added: " + user);
        return new ModelAndView("redirect:" + indexView);
    }

    @RequestMapping(value = {"/updateUser/{id}", "/updateUser"}, method = RequestMethod.GET)
    public ModelAndView updateUser(User user, RedirectAttributes redirectAttrs) {
        ModelAndView modelAndView = new ModelAndView(updateUserView);
        if (user.getId() == null) {
            redirectAttrs.addFlashAttribute("error", "Not the selected user");
            return new ModelAndView("redirect:" + indexView);
        }
        user = userService.getUserById(user.getId());
        modelAndView.addObject("user", user);
        return getUserModelAndView(modelAndView, user);
    }

    @RequestMapping(value = {"/updateUser"}, method = RequestMethod.POST)
    public ModelAndView updateUser(@Valid @ModelAttribute User user,
                                   final BindingResult result,
                                   final ModelAndView modelAndView,
                                   RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return getUserModelAndView(modelAndView, user);
        }
        userService.updateUser(user);
        redirectAttrs.addFlashAttribute("success", "User updated: " + user);
        return new ModelAndView("redirect:" + indexView);
    }

    @RequestMapping(value = {"/deleteUser/{id}"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        User user = userService.getUserById(id);
        userService.deleteUserById(id);
        redirectAttrs.addFlashAttribute("success", "Delete user: " + user);
        return "redirect:" + indexView;
    }

    private ModelAndView getUserModelAndView(ModelAndView modelAndView, User user) {
        modelAndView.addObject("currentRoles", user.getRoles());
        modelAndView.addObject("allRoles", roleService.allRoles());
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
