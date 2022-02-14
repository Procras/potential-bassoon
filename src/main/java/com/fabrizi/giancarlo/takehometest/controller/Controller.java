package com.fabrizi.giancarlo.takehometest.controller;


import com.fabrizi.giancarlo.takehometest.pojo.*;
import com.fabrizi.giancarlo.takehometest.pojo.Error;
import com.fabrizi.giancarlo.takehometest.service.Processor;
import com.fabrizi.giancarlo.takehometest.utils.InvalidInputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;


import java.util.*;
import java.util.stream.Collectors;

/**
 * The Class Controller.
 */
@RestController
@RequestMapping("/organization/")
public class Controller {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    Processor processor;

    @GetMapping(value = "/v1/home-exercise/info")
    public ResponseEntity getDataForOrganization(
            @RequestParam(value = "organizationName") String organizationName,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) throws JsonProcessingException {
        String apiUrlReposList = "https://api.github.com/orgs/" + organizationName + "/repos";
        ResponseEntity<Object> replyToClient = null;
        try {
            replyToClient = processor.processRequest(organizationName, apiUrlReposList);
            return replyToClient;

        } catch (InvalidInputException iie) {
            logger.error("", iie);
            Error errorResponse = new Error();
            errorResponse.setTimestamp(new Date());
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Invalid Input Error");
            errorResponse.setMessage("https://api.github.com/orgs/MISSING/repos  <- You forgot to enter the organization name");
            errorResponse.setPath(apiUrlReposList);
            replyToClient = new ResponseEntity<>(
                    errorResponse,
                    HttpStatus.BAD_REQUEST);
            return replyToClient;
        } catch (RestClientException rce) {
            logger.error("", rce);
            Error errorResponse = new Error();
            errorResponse.setTimestamp(new Date());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
            errorResponse.setMessage("Organization does not exist!");
            errorResponse.setPath(apiUrlReposList);
            replyToClient = new ResponseEntity<>(
                    errorResponse,
                    HttpStatus.NOT_FOUND);
            return replyToClient;
        } catch (Exception e) {
            logger.error("", e);
            Error errorResponse = new Error();
            errorResponse.setTimestamp(new Date());
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage("oh oh something went wrong, please contact the administrator!");
            errorResponse.setPath(apiUrlReposList);
            replyToClient = new ResponseEntity<>(
                    errorResponse,
                    HttpStatus.INTERNAL_SERVER_ERROR);
            return replyToClient;
        }

//        List<TopUser> listToReturnSorted = listToReturn.stream().collect(Collectors.toList());
//        Collections.sort(listToReturnSorted, (o1, o2) -> o2.getCommits().compareTo(o1.getCommits()));
//
//        Pageable paging = PageRequest.of(pageNo, pageSize);
//
//        Page<EmployeeEntity> pagedResult = repository.findAll(paging);


    }

}

