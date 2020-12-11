package me.doyoung.study.githubapi.participation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import me.doyoung.study.githubapi.participation.domain.Participation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final ParticipationService participationService;

    @EventListener(ApplicationReadyEvent.class)
    public void createViewFromParticipation() {
        final Participation participation = participationService.findParticipationAll().orElseThrow(() -> new IllegalArgumentException("findParticipationAll error"));
        final String contents = getContents(participation);

        try {
            log.info("README 작성 시작");

            final String path = System.getProperty("user.dir");
            File file = new File(path + "/README.md");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(contents.getBytes());
            log.info("README 작성 완료");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getContents(Participation participation) {

        final Map<Integer, Set<String>> taskInfoMap = participation.getTaskInfoMap();

        final List<Map.Entry<Integer, Set<String>>> sortedTaskInfoMap = taskInfoMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());


        final Set<String> allUserNames = participation.getAllUserNames();

        // 총 과제 수
        final double totalTaskCount = taskInfoMap.keySet().size();

        // 제목
        String subject = "### 스터디 참여 현황 \n";

        log.info("테이블 제목 작성 시작");
        StringBuilder tableHeading = new StringBuilder("| 참여자 ");
        StringBuilder tableLine = new StringBuilder("|---|:");
        for (Map.Entry<Integer, Set<String>> entry : sortedTaskInfoMap) {
            String ISSUE_TITLE_SUFFIX = "주차";
            final String issueTitle = String.format("| %s ", entry.getKey() + ISSUE_TITLE_SUFFIX);
            tableHeading.append(issueTitle);
            tableLine.append("---:|");
        }

        tableHeading.append(" | 참여율 | \n");
        tableLine.append("---:| \n");
        log.info("테이블 제목 작성 완료");


        log.info("테이블 컨텐츠 작성 시작");
        StringBuilder tableRows = new StringBuilder();
        for (String userName : allUserNames) {

            tableRows.append("| ").append(userName);
            double submittedCount = 0;

            for (Map.Entry<Integer, Set<String>> entry : sortedTaskInfoMap) {
                if (entry.getValue().contains(userName)) {
                    tableRows.append("| ✅ ");
                    submittedCount += 1;
                    continue;
                }
                tableRows.append("|  ");
            }


            double taskRate = (submittedCount * 100) / totalTaskCount;
            tableRows.append(" | ` ").append(String.format("%.2f", taskRate)).append("% ` | \n");
        }

        log.info("테이블 컨텐츠 작성 종료");
        return subject +
                tableHeading +
                tableLine +
                tableRows;

    }
}
