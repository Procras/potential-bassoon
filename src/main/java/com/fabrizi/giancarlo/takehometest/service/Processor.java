package com.fabrizi.giancarlo.takehometest.service;


import com.fabrizi.giancarlo.takehometest.pojo.*;
import com.fabrizi.giancarlo.takehometest.utils.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class Processor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    @Value("${TOKEN}")
    private String token; //"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"

    @Value("${urlTopContributorsPerRepo}")
    private String urlTopContributorsPerRepo; //"https://api.github.com/repos/%s/%s/contributors?q=contributions&order=desc"

    @Value("${urlCommitsPerRepo}")
    private String urlCommitsPerRepo; //String urlCommitsPerRepo = "https://api.github.com/repos/%s/%s/commits";

    @Value("${userInfoBio}")
    private String userInfoBio; //String userInfoBio = "https://api.github.com/users/%s";

    @Value("${apiUrlTopContributorsPerRepo}")
    private String apiUrlTopContributorsPerRepo; //String apiUrlTopContributorsPerRepo = "https://api.github.com/repos/%s/%s/contributors?q=contributions&order=desc";

    @Value("${apiUserInfo}")
    private String apiUserInfo; //String apiUserInfo = "https://api.github.com/users/%s";

    @Value("${apiUrlCommitsPerRepo}")
    private String apiUrlCommitsPerRepo; //String apiUrlCommitsPerRepo = "https://api.github.com/repos/%s/%s/commits";

    public ResponseEntity processRequest(String organizationName, String apiUrlReposList) throws InvalidInputException {
        logger.info("processRequest() - IN - input: [{organizationName}, [{apiUrlReposList}]", organizationName, apiUrlReposList);
        List<TopUser> listToReturnSorted = new ArrayList<>();
        if (organizationName.isBlank()) {
            throw new InvalidInputException("Request param organizationName is empty");
        }
        //prepare restTemplate with Token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        //get name list of repos of organization
        ResponseEntity<Root[]> response = restTemplate.exchange(apiUrlReposList, HttpMethod.GET, request, Root[].class);
        Root[] rootResponse = response.getBody();
        List<Root> repoList = Arrays.asList(rootResponse);
        logger.info("processRequest() - NOTIFY - there are [{}] repos for the [{}] ", repoList.size(),organizationName);
        //for each root get name of repo, call api for specific repo to get contributors
        List<String> organizationRepos = new ArrayList<>();
        List<String> listOfAllRepoNames = new ArrayList<>();
        repoList.parallelStream().forEach((root) -> listOfAllRepoNames.add(root.getName()));
        // for each repo get the top committer
        List<String> listOfAllRepos = new ArrayList<>();
        repoList.parallelStream().forEach((root) -> listOfAllRepos.add(root.getName()));
        // get for each repo the top contributors
        List<User> topUsersList = new ArrayList<>();
        listOfAllRepos.parallelStream().forEach((repoName) -> {
            String getTopContributorPerRepoUrl = String.format(apiUrlTopContributorsPerRepo, organizationName, repoName);
            ResponseEntity<User[]> topContributorsResponse = restTemplate.exchange(getTopContributorPerRepoUrl, HttpMethod.GET, request, User[].class);
            User[] repoUserList = topContributorsResponse.getBody();
            if (repoUserList.length != 0) {
                User topUser = repoUserList[0];;
                topUser.setRepoName(repoName);
                topUsersList.add(topUser);
            }
        });
        logger.info("processRequest() - NOTIFY - there are [{}] top User committers", topUsersList.size());
        // get user bio info
        Set<UserInfo> userInfoBioList = new HashSet<>();
        topUsersList.parallelStream().forEach((topUser) -> {
            String userInfoUrl = String.format(apiUserInfo, topUser.getLogin());
            UserInfo userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, UserInfo.class).getBody();
            userInfoResponse.setRepoName(topUser.getRepoName());
            userInfoResponse.setContribution(topUser.getContributions());
            userInfoBioList.add(userInfoResponse);
        });
        logger.info("processRequest() - NOTIFY - there are [{}] bios in the userInfoBioList", userInfoBioList.size());
        // get commits for each repo
        Set<CommitRoot> topUserCommitterList = new HashSet<>();
        Map<String, List<CommitRoot>> repoMaps = new HashMap<>();
        listOfAllRepos.parallelStream().forEach((repository) -> {
            String totalRepoCommitsUrl = String.format(apiUrlCommitsPerRepo, organizationName, repository);
            ResponseEntity<CommitRoot[]> repoCommitListResponse = restTemplate.exchange(totalRepoCommitsUrl, HttpMethod.GET, request, CommitRoot[].class);
            CommitRoot[] repoCommitList = repoCommitListResponse.getBody();
            List<CommitRoot> listOfCommits = new ArrayList<>();
            listOfCommits.addAll(Arrays.asList(repoCommitList));
            repoMaps.put(repository, listOfCommits);
        });
        Set<TopUser> listToReturn = new HashSet<>();
        topUsersList.parallelStream().forEach((topUser) -> {
            List<CommitRoot> newCommitList = new ArrayList<>();
            List<CommitRoot> commitList = repoMaps.get(topUser.getRepoName());
            commitList.stream().forEach(commit -> {
                if((commit.getCommitter()!=null) && (commit.getCommitter().getAvatar_url()!=null) && (commit.getCommitter().getAvatar_url().equals(topUser.getAvatar_url()))) {
                    newCommitList.add(commit);
                }
            });
            CommitRoot commit = new CommitRoot();
            if(newCommitList.size()!=0) {
                commit = newCommitList.get(0);
            }
            TopUser top = new TopUser();
            top.setUsername(topUser.getLogin());
            top.setRepoName(topUser.getRepoName());
            top.setImageUrl(topUser.getAvatar_url());
            top.setCommits(topUser.getContributions());
            top.setRepoName(topUser.getRepoName());
            if(commit.getCommitter()!=null && commit.getCommitter().getAvatar_url()!=null){
                top.setMessage(commit.getCommit().getMessage() != null ? commit.getCommit().getMessage() : "N/A");
                top.setEmail(commit.getCommit().getCommitter().getEmail() != null ? commit.getCommit().getCommitter().getEmail() : topUser.getEmail());

            }
            listToReturn.add(top);
        });
        logger.info("processRequest() - OUT - output: listToReturn size is [{}]", listToReturn.size());
        listToReturnSorted = listToReturn.parallelStream().collect(Collectors.toList());
        Collections.sort(listToReturnSorted, (o1, o2) -> o2.getCommits().compareTo(o1.getCommits()));
        return new ResponseEntity<>(
                listToReturnSorted,
                HttpStatus.OK);
    }
}
