package com.fabrizi.giancarlo.takehometest.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Error{
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
