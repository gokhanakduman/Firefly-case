package com.firefly.orderManagement.controller.validation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneNotNullFieldValidator implements ConstraintValidator<AtLeastOneNotNullFieldValidation, Object>
{
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
    	Field[] fields = object.getClass().getDeclaredFields();
    	for(Field field : fields) {
    		field.setAccessible(true);
    		try {
				if (field.get(object) != null) {
					return true;
				}
			} catch (Exception e) {
				continue;
			}
    	}
        return false;
    }
}