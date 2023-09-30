package com.dit.airbnb;

import com.dit.airbnb.service.PopulateDBService;
import com.dit.airbnb.service.RecommendationService;
import com.dit.airbnb.service.RoleService;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
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

        @Autowired
        private RecommendationService recommendationService;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("Load Static Roles [START]");
            populateDBService.populateStaticRoles();
            System.out.println("Load Static Roles [END]");

            System.out.println("Load Static Users [START]");
            populateDBService.populateUsersReg();
            System.out.println("Load Static Users [END]");

            System.out.println("Load Static Apartments [START]");
            populateDBService.populateApartments();
            System.out.println("Load Static Apartments [END]");

            System.out.println("Load Static Bookings [START]");
            populateDBService.populateBooking();
            System.out.println("Load Static Bookings [END]");

            System.out.println("Load Static Booking Reviews [START]");
            populateDBService.populateBookingReview();
            System.out.println("Load Static Booking Reviews [END]");

            System.out.println("Load Static Messages [START]");
            populateDBService.populateMessages();
            System.out.println("Load Static Messages [END]");

            // recommendation
            System.out.println("Load Recommendations Apartments [START]");
            populateDBService.populateRecApartments();
            System.out.println("Load Recommendations Apartments [END]");

            System.out.println("Load Recommendations Bookings/Reviews [START]");
            populateDBService.populateRecBookingAndReviews();
            System.out.println("Load Recommendations Bookings/Reviews [END]");


            //recommendationService.recommend(130L);

        }
    }

    // In case we want to redirect http to https
    /*
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(true);
        connector.setRedirectPort(8443);
        return connector;
    }
     */
}
