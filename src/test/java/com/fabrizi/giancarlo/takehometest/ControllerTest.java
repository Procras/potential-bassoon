package com.fabrizi.giancarlo.takehometest;

import com.fabrizi.giancarlo.takehometest.controller.Controller;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerTest {

    @InjectMocks
    private Controller testClass;


//    @Test
//    public void getResellerCredit_test() {
//        ResponseEntity re = testClass.getDataFromOrganization("netflix");
//        assertTrue(re.getStatusCode().equals(HttpStatus.OK));
//    }
}
