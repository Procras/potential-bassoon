package com.fabrizi.giancarlo.takehometest.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
public class Commit{
    private Author author;
    private Committer committer;
    private String message;
    private Tree tree;
    private String url;
    private int comment_count;
    private Verification verification;
}
