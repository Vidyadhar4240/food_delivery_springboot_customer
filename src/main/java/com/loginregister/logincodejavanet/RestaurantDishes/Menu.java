package com.loginregister.logincodejavanet.RestaurantDishes;

import com.loginregister.logincodejavanet.restaurantdetails.Restaurant;

import javax.persistence.*;

@Entity
@Table(name = "menu")
public class Menu {

    private static final long serialVersionUID = 546436l;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private int id;

    @Column(length = 256)
    private String name;

    @Column(length = 254)
    private int price;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}

