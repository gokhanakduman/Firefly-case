package com.firefly.orderManagement.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class NoNullFieldsValidator implements ConstraintValidator<NoNullFieldsValidation, Object>
{
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
    	Field[] fields = object.getClass().getDeclaredFields();
    	for(Field field : fields) {
    		field.setAccessible(true);
    		try {
				if (field.get(object) == null) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
    	}
        return true;
    }
}
