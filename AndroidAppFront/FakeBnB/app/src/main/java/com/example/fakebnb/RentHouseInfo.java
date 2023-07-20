package com.example.fakebnb;

public class RentHouseInfo {

    int max_persons_allowed;
    int bathrooms;
    int bedrooms;
    int price;
    int extra_person_cost;
    String rental_type;
    String description;
    String rules;
    String address;
    String amenities;
    String hostImage;
    String hostName;
    boolean userStayedHere;


    public RentHouseInfo(int max_persons_allowed, int bathrooms, int bedrooms, int price,
                         int extra_person_cost, String rental_type, String description,
                         String rules, String address, String amenities, String hostImage,
                         String hostName, boolean userStayedHere) {
        this.max_persons_allowed = max_persons_allowed;
        this.bathrooms = bathrooms;
        this.bedrooms = bedrooms;
        this.price = price;
        this.extra_person_cost = extra_person_cost;
        this.rental_type = rental_type;
        this.description = description;
        this.rules = rules;
        this.address = address;
        this.amenities = amenities;
        this.hostImage = hostImage;
        this.hostName = hostName;
        this.userStayedHere = userStayedHere;
    }

    public int getMax_persons_allowed() {
        return max_persons_allowed;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getPrice() {
        return price;
    }

    public int getExtra_person_cost() {
        return extra_person_cost;
    }

    public String getRental_type() {
        return rental_type;
    }

    public String getDescription() {
        return description;
    }

    public String getRules() {
        return rules;
    }

    public String getAddress() {
        return address;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getHostImage() {
        return hostImage;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean getUserStayedHere() {
        return userStayedHere;
    }
}
