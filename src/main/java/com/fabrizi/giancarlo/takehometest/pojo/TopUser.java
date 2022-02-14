package com.fabrizi.giancarlo.takehometest.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class TopUser {

    private String username;
    private String imageUrl;
    private String email;
    private Integer commits;
    private String message;
    private String repoName;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + commits;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopUser other = (TopUser) obj;
        if (repoName != other.repoName)
            return false;
        return true;
    }

}
