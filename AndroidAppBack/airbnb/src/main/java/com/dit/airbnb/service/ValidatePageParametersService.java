package com.dit.airbnb.service;

import com.dit.airbnb.exception.BadRequestException;
import com.dit.airbnb.util.PaginationConstants;
import org.springframework.stereotype.Service;

@Service
public class ValidatePageParametersService {

    public void validate(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > PaginationConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + PaginationConstants.MAX_PAGE_SIZE);
        }
    }
}
