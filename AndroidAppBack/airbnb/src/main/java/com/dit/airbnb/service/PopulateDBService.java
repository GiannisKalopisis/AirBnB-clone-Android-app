package com.dit.airbnb.service;

import com.dit.airbnb.csv_dto.custom.*;
import com.dit.airbnb.csv_dto.recommendation.ApartmentRecCSV;
import com.dit.airbnb.csv_dto.recommendation.BookingReviewRecCSV;
import com.dit.airbnb.dto.*;
import com.dit.airbnb.dto.enums.RentalType;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.*;
import com.dit.airbnb.util.RecommendationParameters;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class PopulateDBService {

    @Autowired
    private RecommendationParameters recommendationParameters;

    private static final String USER_REG_DATA_FILE_PATH = "src/main/resources/static/csv_data/user_reg_data.csv";
    private static final String APARTMENT_DATA_FILE_PATH = "src/main/resources/static/csv_data/apartment_data.csv";
    private static final String MESSAGE_DATA_FILE_PATH = "src/main/resources/static/csv_data/message_data.csv";
    private static final String BOOKING_DATA_FILE_PATH = "src/main/resources/static/csv_data/booking_data.csv";
    private static final String BOOKING_REVIEW_DATA_FILE_PATH = "src/main/resources/static/csv_data/booking_review_data.csv";

    private static final String REC_APARTMENT_DATA_FILE_PATH = "src/main/resources/static/rec_csv_data/rec_apartment_data.csv";
    private static final String REC_BOOKING_DATA_FILE_PATH = "src/main/resources/static/rec_csv_data/rec_booking_data.csv";
    private static final String REC_BOOKING_REVIEW_DATA_FILE_PATH = "src/main/resources/static/rec_csv_data/rec_booking_review_data.csv";

    public static final String IMAGE_DEFAULT_PATH = "static_image.png";

    public static final String userDefaultPassword = "123456";

    public Map<Long, Long> hostRecFileIdToDatabaseIdMap = new HashMap<>();
    public Map<Long, Long> userRecFileIdToDatabaseIdMap = new HashMap<>();
    public Map<Long, Long> apartmentRecFileIdToDatabaseIdMap = new HashMap<>();

    public List<String> apartmentImagesFilePath = List.of(
            "static_apart_image_1.jpeg",
            "static_apart_image_2.jpeg",
            "static_apart_image_3.jpeg",
            "static_apart_image_4.jpeg",
            "static_apart_image_5.jpeg",
            "static_apart_image_6.jpeg",
            "static_apart_image_7.jpeg",
            "static_apart_image_8.jpeg",
            "static_apart_image_9.jpeg",
            "static_apart_image_10.jpeg"
    );

    public static final String defaultCountry = "Greece";
    public static final String defaultCity = "Athina";
    public static final String defaultDistrict = "Athina";
    // (lat, long)
    public static final List<Pair<String, Pair<BigDecimal, BigDecimal>>> defaultAddressList = List.of(
                    Pair.create("Dionysiou Areopagitou 1", Pair.create(new BigDecimal("37.9693083"), new BigDecimal("23.728975"))),
                    Pair.create("Makrigianni 2", Pair.create(new BigDecimal("37.9689215"), new BigDecimal("23.7288717"))),
                    Pair.create("Panormou 22", Pair.create(new BigDecimal("37.98994140000001"), new BigDecimal("23.7588621"))),
                    Pair.create("Leof. Vasilisis Amalias 56", Pair.create(new BigDecimal("37.9698911"), new BigDecimal("23.7312446"))),
                    Pair.create("Sarri 9", Pair.create(new BigDecimal("37.9795089"), new BigDecimal("23.7237")))
    );

    public static final Random random = new Random();

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


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

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

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

                // Store image
                Image imageIn = new Image(IMAGE_DEFAULT_PATH);
                imageIn.setUserReg(userReg);
                imageRepository.save(imageIn);

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

                for (int i = 0; i < random.nextInt(3) + 1; i++) {
                    Image imageIn = new Image(apartmentImagesFilePath.get(random.nextInt(apartmentImagesFilePath.size())));
                    imageIn.setApartment(apartment);
                    imageRepository.save(imageIn);
                }

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
    public void populateBookingReview() throws IOException, AppException {

        try (Reader reader = Files.newBufferedReader(Paths.get(BOOKING_REVIEW_DATA_FILE_PATH))) {
            CsvToBean<BookingReviewCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(BookingReviewCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

                for (BookingReviewCSV bookingReviewCSV: csvToBean) {

                    UserReg reviewer = userRegRepository.findById(bookingReviewCSV.getReviewerId())
                            .orElseThrow(() -> new ResourceNotFoundException("Reviewer", "id", bookingReviewCSV.getReviewerId()));

                    Booking booking = bookingRepository.findById(bookingReviewCSV.getBookingId())
                            .orElseThrow(() -> new ResourceNotFoundException("BookingReview", "id", bookingReviewCSV.getBookingId()));


                    BookingReview bookingReview = new BookingReview(bookingReviewCSV);
                    bookingReview.setCreatorUserReg(reviewer);
                    bookingReview.setBooking(booking);

                    bookingReviewRepository.save(bookingReview);
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
            }
        }
    }

    @Transactional
    public void populateRecApartments() throws Exception {

        try (Reader reader = Files.newBufferedReader(Paths.get(REC_APARTMENT_DATA_FILE_PATH))) {
            CsvToBean<ApartmentRecCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ApartmentRecCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            int count = 0;
            for (ApartmentRecCSV apartmentRecCSV : csvToBean) {

                // NOTE(geo): load a small portion of the apartments
                if (count > recommendationParameters.getMaxApartmentsToLoad()) break;

                // if (apartmentRecCSV.getHostName().contains("&")) continue;
                UserReg host;
                if (!hostRecFileIdToDatabaseIdMap.containsKey(apartmentRecCSV.getHostId())) {
                    String codeName = apartmentRecCSV.getHostName() + apartmentRecCSV.getHostId();
                    UserReg userReg = UserReg.builder().
                            firstName(apartmentRecCSV.getHostName()).
                            lastName("empty").
                            email(codeName + "@gmail.com").
                            password(passwordEncoder.encode(userDefaultPassword)).
                            phone("999999").username(codeName).build();
                    Role userRoleUser = roleRepository.findByName(RoleName.ROLE_HOST)
                            .orElseThrow(() -> new AppException("Role User not set"));
                    userReg.addRole(userRoleUser);
                    host = userRegRepository.save(userReg);
                    hostRecFileIdToDatabaseIdMap.put(apartmentRecCSV.getHostId(), host.getId());
                    // Store image
                    Image imageIn = new Image(IMAGE_DEFAULT_PATH);
                    imageIn.setUserReg(userReg);
                    imageRepository.save(imageIn);
                } else {
                    Long realId = hostRecFileIdToDatabaseIdMap.get(apartmentRecCSV.getHostId());
                    host = userRegRepository.findById(realId).orElseThrow(() -> new ResourceNotFoundException("HostUserReg", "id", realId));
                }

                /*
                BigDecimal latitude = apartmentRecCSV.getGeoLat();
                BigDecimal longitude = apartmentRecCSV.getGeoLong();
                String apiKey = "AIzaSyDrdKGBv02spF8QpPFJaK26m1ZynbnXNOg";
                String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + apiKey;
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader2.readLine()) != null) {
                    response.append(line);
                }
                reader2.close();
                // Parse the JSON response to get the formatted address
                String jsonResponse = response.toString();
                int start = jsonResponse.indexOf("formatted_address") + 20;
                int end = jsonResponse.indexOf("\"", start);
                String address = jsonResponse.substring(start, end);
                System.out.println(jsonResponse);
                System.out.println(address);
                if (count == 11) break;
                */

                Apartment apartment = new Apartment(
                        apartmentRecCSV,
                        defaultCountry,
                        defaultCity,
                        defaultDistrict,
                        defaultAddressList.get(count % defaultAddressList.size()),
                        (count & 2) == 0 ? RentalType.RENTAL_ROOM : RentalType.RENTAL_HOUSE,
                        dateFormat.parse("2014-01-01"),
                        dateFormat.parse("2024-01-01")
                );

                apartment.setUserRegHost(host);
                Apartment savedApartment = apartmentRepository.save(apartment);
                apartmentRecFileIdToDatabaseIdMap.put(apartmentRecCSV.getApartmentId(), savedApartment.getId());
                for (int i = 0; i < random.nextInt(3) + 1; i++) {
                    Image imageIn = new Image(apartmentImagesFilePath.get(random.nextInt(apartmentImagesFilePath.size())));
                    imageIn.setApartment(savedApartment);
                    imageRepository.save(imageIn);
                }
                count++;
            }
        }
    }

    private Date getTargetPreviousDate(Date date, long targetBeforeNumber) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate prevDate = localDate.minusDays(targetBeforeNumber);
        return Date.from(prevDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Transactional
    public void populateRecBookingAndReviews() throws Exception {

        try (Reader reader = Files.newBufferedReader(Paths.get(REC_BOOKING_REVIEW_DATA_FILE_PATH))) {
            CsvToBean<BookingReviewRecCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(BookingReviewRecCSV.class)
                    .withIgnoreLeadingWhiteSpace(true).build();

            int count = 0;
            for (BookingReviewRecCSV bookingReviewRecCSV : csvToBean) {
                if (count > recommendationParameters.getMaxReviewsToLoad()) break;
                if (apartmentRecFileIdToDatabaseIdMap.containsKey(bookingReviewRecCSV.getApartmentId())) {
                    Long realId = apartmentRecFileIdToDatabaseIdMap.get(bookingReviewRecCSV.getApartmentId());
                    Apartment apartment = apartmentRepository.findById(realId).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id",realId));

                    UserReg userReg;
                    if (userRecFileIdToDatabaseIdMap.containsKey(bookingReviewRecCSV.getReviewerId())) {
                        Long userRealId = userRecFileIdToDatabaseIdMap.get(bookingReviewRecCSV.getReviewerId());
                        userReg = userRegRepository.findById(userRealId).orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userRealId));
                    } else {
                        String codeName = bookingReviewRecCSV.getReviewerName() + bookingReviewRecCSV.getReviewerId();
                        UserReg userRegToStore = UserReg.builder().
                                firstName(bookingReviewRecCSV.getReviewerName()).
                                lastName("empty").
                                email(codeName + "@gmail.com").
                                password(passwordEncoder.encode(userDefaultPassword)).
                                phone("999999").username(codeName).build();
                        Role userRoleUser = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new AppException("Role User not set"));
                        userRegToStore.addRole(userRoleUser);
                        userReg = userRegRepository.save(userRegToStore);
                        userRecFileIdToDatabaseIdMap.put(bookingReviewRecCSV.getReviewerId(),  userReg.getId());
                        // Store image
                        Image imageIn = new Image(IMAGE_DEFAULT_PATH);
                        imageIn.setUserReg(userReg);
                        imageRepository.save(imageIn);
                    }

                    Date startDate = getTargetPreviousDate(bookingReviewRecCSV.getReviewDate(), 2);
                    Date endDate = getTargetPreviousDate(bookingReviewRecCSV.getReviewDate(), 1);

                    Booking booking = new Booking(startDate, endDate);
                    booking.setIsReviewed(true);
                    booking.setUserReg(userReg);
                    booking.setApartment(apartment);
                    booking = bookingRepository.save(booking);

                    BookingReview bookingReview = BookingReview.builder().
                            rating((short) (random.nextInt(5 - 1 + 1) + 1)).
                            description(bookingReviewRecCSV.getComment()).booking(booking).creatorUserReg(userReg).build();

                    bookingReviewRepository.save(bookingReview);
                    count++;
                }
            }

        }
    }

}
