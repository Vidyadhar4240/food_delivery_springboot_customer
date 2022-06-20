package com.loginregister.logincodejavanet;

import com.loginregister.logincodejavanet.RestaurantDishes.Menu;
import com.loginregister.logincodejavanet.RestaurantDishes.MenuRepository;
import com.loginregister.logincodejavanet.UserPackage.User;
import com.loginregister.logincodejavanet.UserPackage.UserRepository;
import com.loginregister.logincodejavanet.dto.OrderDTO;
import com.loginregister.logincodejavanet.dto.OrderItemDTO;
import com.loginregister.logincodejavanet.orderItems.OrderItem;
import com.loginregister.logincodejavanet.orderItems.OrderItemRepository;
import com.loginregister.logincodejavanet.orders.Order;
import com.loginregister.logincodejavanet.orders.OrderRepository;
import com.loginregister.logincodejavanet.restaurantdetails.Restaurant;
import com.loginregister.logincodejavanet.restaurantdetails.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private MenuRepository menuRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        return "register_success";
    }

    @RequestMapping(value = "/list_users", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView listAllUsers(Model model, Principal principal) {
//        List<User> listUsers = (List<User>) userRepo.findAll();
        String email = principal.getName();
        User newUser = userRepo.findByEmail(email);
        model.addAttribute("listUsers", newUser);
        ModelAndView modelAndView = new ModelAndView("users");
        ModelAndView mav = modelAndView;
        mav.addObject("user", newUser);
        return mav;
    }
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditUserPage(@PathVariable(name = "id") Long id) {
        ModelAndView mav = new ModelAndView("edit_user");
        Optional<User> user = userRepo.findById(id);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute("user") User user) {
        userRepo.save(user);
        return "index";
    }

    @GetMapping("/list_restaurants")
    public String listRestaurants(Model model) {
        List<Restaurant> restaurantList = restaurantRepo.findAll();
        model.addAttribute("restaurantList", restaurantList);
        return "restaurants_list";
    }

    @GetMapping("/list_menu/{id}")
    public String listMenu(Model model, @PathVariable(name = "id") Long restaurant_id) {
        System.out.println(restaurant_id);
        List<Menu> menuList = menuRepo.findAllByRestaurantId(restaurant_id);
        model.addAttribute("menuList", menuList);

        OrderDTO order = new OrderDTO();
        order.setOrderItems(new ArrayList<>());
        if(menuList != null){
            OrderItemDTO orderItem = null;
            for(Menu menu : menuList){
                orderItem = new OrderItemDTO();
                orderItem.setMenuId(menu.getId());
                orderItem.setAmount(menu.getPrice());
                order.getOrderItems().add(orderItem);
            }
        }
        model.addAttribute("order", order);
        return "menu";
    }

    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST)
    public String saveOrder(Model model, @ModelAttribute("order") OrderDTO orderDTO) {

        Order order = null;
        if(orderDTO != null && orderDTO.getOrderItems() != null){
            List<OrderItem> orderItems = new ArrayList<>();
            OrderItem orderItem = null;
            double totalAmount = 0;
            for(OrderItemDTO orderItemDTO : orderDTO.getOrderItems()){
               if(orderItemDTO.isSelected()){
                   orderItem = new OrderItem();
                   orderItem.setMenuId(orderItemDTO.getMenuId());
                   orderItems.add(orderItem);
                   totalAmount = getTotal(totalAmount, (double) orderItemDTO.getAmount());
//                   totalAmount += (Double) orderItemDTO.getAmount();
               }
            }
            model.addAttribute("totalAmount", totalAmount);
            if(!orderItems.isEmpty()){
                order = new Order();
                order.setUserId(orderDTO.getUserId());
                order.setTotalAmount(totalAmount);
                orderRepo.save(order);
                for(OrderItem item : orderItems){
                    item.setOrderId(order.getOrderId());
                    orderItemRepo.save(item);
                }
            }
        }
        return "order_successful";
    }
    private static double getTotal(double value1, double value2) {
        BigDecimal total = BigDecimal.valueOf(value1).add(BigDecimal.valueOf(value2));
        return total.doubleValue();
    }
}