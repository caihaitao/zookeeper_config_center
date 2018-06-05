package com.cc.architecture;

import com.cc.architecture.service.MyAnnotationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-28 18:23:26
 */
public class MyAnnotationTest extends ArchitectureApplicationTests {
    @Autowired
    private MyAnnotationService myAnnotationService;

    @Test
    public void testDosth() {
        myAnnotationService.deSth();
    }
}
