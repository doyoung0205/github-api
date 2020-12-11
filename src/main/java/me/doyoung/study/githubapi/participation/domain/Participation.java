package me.doyoung.study.githubapi.participation.domain;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    // 모든 참석자
    private Set<String> allUserNames;

    // 과제 이름, 참석자들
    private Map<Integer, Set<String>> taskInfoMap;
}
