package com.anzaiyun.common.valid.Anno;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class IntListValueConstraintValidator implements ConstraintValidator<IntListValue,Integer> {
    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(IntListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for(int val:vals){
            set.add(val);
        }
    }

    /**
     * value需要校验的值
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
