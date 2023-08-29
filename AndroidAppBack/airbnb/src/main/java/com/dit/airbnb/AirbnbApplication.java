package com.dit.airbnb;

import com.dit.airbnb.service.PopulateDBService;
import com.dit.airbnb.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class AirbnbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbnbApplication.class, args);
    }

    @Component
    public static class CommandLineAppStartupRunner implements CommandLineRunner {

        @Autowired
        private PopulateDBService populateDBService;

        @Override
        public void run(String... args) throws Exception {
            populateDBService.populateStaticRoles();
            populateDBService.populateUsersReg();
            populateDBService.populateApartments();
            populateDBService.populateBooking();
            populateDBService.populateMessages();
        }
    }

}
