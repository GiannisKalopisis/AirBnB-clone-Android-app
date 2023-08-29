package com.dit.airbnb.service;

import com.dit.airbnb.csv_dto.ApartmentCSV;
import com.dit.airbnb.csv_dto.BookingCSV;
import com.dit.airbnb.csv_dto.MessageCSV;
import com.dit.airbnb.csv_dto.UserRegCSV;
import com.dit.airbnb.dto.*;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.exception.ResourceNotFoundException;
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
import java.util.Optional;
import java.util.Set;

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

                UserReg hostUserReg = userRegRepository.findById(apartmentCSV.getHostId())
                        .orElseThrow(() -> new ResourceNotFoundException("HostUserReg", "id", apartmentCSV.getHostId()));

                Set<Role> roles = hostUserReg.getRoles();
                boolean foundFlag = false;
                for (Role role: roles) {
                    if (role.getName().equals(RoleName.ROLE_HOST)) {
                        foundFlag = true;
                        break;
                    }
                }
                if (!foundFlag) {
                    continue;
                }

                Apartment apartment = new Apartment(apartmentCSV);
                apartment.setUserRegHost(hostUserReg);
                apartmentRepository.save(apartment);
            }
        }
    }

    @Transactional
    public void populateBooking() throws IOException, AppException {

        try (Reader reader = Files.newBufferedReader(Paths.get(BOOKING_DATA_FILE_PATH))) {
            CsvToBean<BookingCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(BookingCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            for (BookingCSV bookingCSV : csvToBean) {

                UserReg userReg = userRegRepository.findById(bookingCSV.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", bookingCSV.getUserId()));

                Apartment apartment = apartmentRepository.findById(bookingCSV.getApartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", bookingCSV.getApartmentId()));

                Set<Role> roles = userReg.getRoles();
                boolean foundFlag = false;
                for (Role role : roles) {
                    if (role.getName().equals(RoleName.ROLE_USER)) {
                        foundFlag = true;
                        break;
                    }
                }
                if (!foundFlag) {
                    continue;
                }

                Booking booking = new Booking(bookingCSV);
                booking.setUserReg(userReg);
                booking.setApartment(apartment);
                bookingRepository.save(booking);
            }
        }
    }

    @Transactional
    public void populateMessages() throws IOException, AppException {

        try (Reader reader = Files.newBufferedReader(Paths.get(MESSAGE_DATA_FILE_PATH))) {
            CsvToBean<MessageCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(MessageCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            for (MessageCSV messageCSV : csvToBean) {

                UserReg sender = userRegRepository.findById(messageCSV.getSenderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Sender", "id", messageCSV.getSenderId()));

                UserReg receiver = userRegRepository.findById(messageCSV.getReceiverId())
                        .orElseThrow(() -> new ResourceNotFoundException("Receiver", "id", messageCSV.getReceiverId()));

                boolean isUser = false;
                for (Role role: sender.getRoles()) {
                    if (role.getName().equals(RoleName.ROLE_USER)) {
                        isUser = true;
                        break;
                    }
                }
                Long chatFirstSenderUserRegId = isUser ? sender.getId() : receiver.getId();
                Long chatFirstReceiverUserRegId = isUser ? receiver.getId() : sender.getId();
                Optional<Chat> chat = chatRepository.findByFirstSenderUserRegIdAndFirstReceiverUserRegId(chatFirstSenderUserRegId, chatFirstReceiverUserRegId);
                if (chat.isEmpty()) {
                    chat = Optional.of(new Chat(sender, receiver));
                    chatRepository.save(chat.get());
                }

                Optional<Message> optionalLastMessage = messageRepository.findLastMessageWithSendUserIdAndReceiverUserId(chatFirstSenderUserRegId,chatFirstReceiverUserRegId);
                if (optionalLastMessage.isPresent()) {
                    Message lastMessage = optionalLastMessage.get();
                    lastMessage.setIsLastMessage(false);
                    lastMessage.setSeen(true);
                    messageRepository.save(lastMessage);
                }

                Message message = new Message(messageCSV);
                message.setSenderUserReg(sender);
                message.setChat(chat.get());
                messageRepository.save(message);

                System.out.println(message);
            }
        }
    }
}
