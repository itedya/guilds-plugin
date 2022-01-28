package com.itedya.itedyaguilds.middlewares;

import com.itedya.itedyaguilds.dtos.Dto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class CommandArgumentsAreValid extends AbstractHandler {
    private Dto dto;

    public CommandArgumentsAreValid(Dto dto) {
    }

    @Override
    public String handle() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Dto>> violations = validator.validate(dto);

        for (var violation : violations) {
            return violation.getMessage();
        }

        return super.handle();
    }
}
