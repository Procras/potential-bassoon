package com.fabrizi.giancarlo.takehometest.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Verification{
    private boolean verified;
    private String reason;
    private Object signature;
    private Object payload;
}
