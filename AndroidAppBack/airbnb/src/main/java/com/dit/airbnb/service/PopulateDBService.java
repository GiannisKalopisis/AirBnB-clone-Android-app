package com.dit.airbnb.service;

import com.dit.airbnb.csv_dto.ApartmentCSV;
import com.dit.airbnb.csv_dto.UserRegCSV;
import com.dit.airbnb.dto.Role;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.repository.*;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PopulateDBService {

    private static final String USER_REG_DATA_FILE_PATH = "src/main/resources/static/csv_data/user_reg_data.csv";
    private static final String APARTMENT_DATA_FILE_PATH = "src/main/resources/static/csv_data/apartment_data.csv";
    private static final String MESSAGE_DATA_FILE_PATH = "src/main/resources/static/csv_data/message_data.csv";
    private static final String BOOKING_DATA_FILE_PATH = "src/main/resources/static/csv_data/booking_data.csv";
    private static final String BOOKING_REVIEW_DATA_FILE_PATH = "src/main/resources/static/csv_data/booking_review_data.csv";

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingReviewRepository bookingReviewRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long FIRST_ID = 1;

    private static final long ROLE_TYPE_NUMBER_USER = 1;
    private static final long ROLE_TYPE_NUMBER_HOST = 2;
    private static final long ROLE_TYPE_NUMBER_USER_AND_HOST = 3;

    public void populateStaticRoles() {
        if (roleRepository.findById(FIRST_ID).orElse(null) != null) return;
        roleRepository.save(new Role(RoleName.ROLE_USER));
        roleRepository.save(new Role(RoleName.ROLE_HOST));
    }

    @Transactional
    public void populateUsersReg() throws IOException, AppException {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_REG_DATA_FILE_PATH))) {
            CsvToBean<UserRegCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(UserRegCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            for (UserRegCSV userRegCSV : csvToBean) {

                if (userRegRepository.findByUsername(userRegCSV.getUsername()).orElse(null) != null) continue;

                // Insert in db
                UserReg userReg = new UserReg(userRegCSV);

                // Encode password
                userReg.setPassword(passwordEncoder.encode(userReg.getPassword()));

                // NOTE(geo): can be written better
                if (userRegCSV.getRoleTypeNumber() == ROLE_TYPE_NUMBER_USER) {
                    Role userRoleUser = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new AppException("Role User not set"));
                    userReg.addRole(userRoleUser);
                } else if ((userRegCSV.getRoleTypeNumber() == ROLE_TYPE_NUMBER_HOST)) {
                    Role userRoleHost = roleRepository.findByName(RoleName.ROLE_HOST)
                            .orElseThrow(() -> new AppException("Role Host not set"));
                    userReg.addRole(userRoleHost);
                } else {
                    Role userRoleUser = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new AppException("Role User not set"));
                    Role userRoleHost = roleRepository.findByName(RoleName.ROLE_HOST)
                            .orElseThrow(() -> new AppException("Role Host not set"));
                    userReg.addRole(userRoleUser);
                    userReg.addRole(userRoleHost);
                }

                userRegRepository.save(userReg);
            }
        }
    }

    @Transactional
    public void populateApartments() throws IOException, AppException {

        try (Reader reader = Files.newBufferedReader(Paths.get(APARTMENT_DATA_FILE_PATH))) {
            CsvToBean<ApartmentCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ApartmentCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            for (ApartmentCSV apartmentCSV : csvToBean) {
                System.out.println(apartmentCSV.getDescription() + apartmentCSV.getDistrict() + apartmentCSV.getHostId());
            }
        }
    }
}
