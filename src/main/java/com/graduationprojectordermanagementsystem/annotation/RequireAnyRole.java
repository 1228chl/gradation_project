package com.graduationprojectordermanagementsystem.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAnyRole {

    String[] value();//支持 @RequireAnyRole("A") 或 @RequireAnyRole("A", "B")
}
