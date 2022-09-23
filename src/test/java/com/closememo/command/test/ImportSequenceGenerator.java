package com.closememo.command.test;

import com.closememo.command.infra.sequencegenerator.ObjectId;
import com.closememo.command.infra.sequencegenerator.SequenceGeneratorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ObjectId.class, SequenceGeneratorImpl.class})
public @interface ImportSequenceGenerator {

}
