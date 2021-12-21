package com.closememo.command.interfaces.batch;

import com.closememo.command.config.openapi.apitags.BatchApiTag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@BatchApiTag
@PreAuthorize("hasRole('SYSTEM')")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RequestMapping("/command/batch")
@RestController
public @interface BatchCommandInterface {

}
