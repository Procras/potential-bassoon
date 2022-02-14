package com.fabrizi.giancarlo.takehometest.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter @Setter @ToString
public class CommitRoot{
    private String sha;
    private String node_id;
    private Commit commit;
    private String url;
    private String html_url;
    private String comments_url;
    private Author author;
    private Committer committer;
    private ArrayList<Parent> parents;
    private int contributions;
    private String repository;


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommitRoot other = (CommitRoot) obj;
        if (!sha.equals(other.sha))
            return false;
        if (!node_id.equals(other.node_id))
            return false;
        return true;
    }
}
