package me.doyoung.study.githubapi.participation.infra;

import lombok.extern.java.Log;
import me.doyoung.study.githubapi.participation.domain.Participation;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Log
@Repository
public class TranslateGithubAPI {

    @Value("${oauthToken}")
    private String oauthToken;

    public Optional<Participation> translate() throws IOException {

        log.info("github api 접속 요청");
        final GitHub gitHub = new GitHubBuilder().withOAuthToken(oauthToken).build();
        log.info("github api 접속 완료");

        final String repositoryName = "whiteship/live-study";
        log.info("github repository 접속 요청");
        final GHRepository repository = gitHub.getRepository(repositoryName);
        log.info("github repository 접속 완료");


        log.info("github 이슈 조회 요청");
        final List<GHIssue> allIssues = repository.getIssues(GHIssueState.ALL);
        log.info("github 이슈 조회 완료");

        // 모든 참가자
        // 중복제거
        // 자동으로 정렬
        final Set<String> allUserIds = new TreeSet<>();

        // 과제 별 제출 정보
        final Map<Integer, Set<String>> taskInfoMap = new HashMap<>();
        final String TASK_MANGER_NAME = "Keesun Baik";
        final String COMMON_TITLE_WORD = "주";
        log.info("github translate 시작");
        for (GHIssue issue : allIssues) {

            // 백기선님 글만 담기
            final String issueWriterName = issue.getUser().getName();
            if (!issueWriterName.equals(TASK_MANGER_NAME)) {
                continue;
            }

            // 주차 앞의 숫자 가져오기
            String issueTitle = issue.getTitle();
            issueTitle = issueTitle.substring(0, issueTitle.indexOf(COMMON_TITLE_WORD));
            final int taskWeekNumber = Integer.parseInt(issueTitle);

            final Set<String> userIds = new HashSet<>();

            for (GHIssueComment comment : issue.getComments()) {
                final String commentUserId = comment.getUser().getLogin();
                allUserIds.add(commentUserId);
                userIds.add(commentUserId);
            }

            // 과제 별 제출자
            taskInfoMap.put(taskWeekNumber, userIds);
        }

        log.info("github translate 완료");
        return Optional.of(Participation.builder().
                allUserNames(allUserIds)
                .taskInfoMap(taskInfoMap)
                .build());
    }


}
