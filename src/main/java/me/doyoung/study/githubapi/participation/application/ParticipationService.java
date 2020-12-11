package me.doyoung.study.githubapi.participation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import me.doyoung.study.githubapi.participation.domain.Participation;
import me.doyoung.study.githubapi.participation.infra.TranslateGithubAPI;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Log
@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final TranslateGithubAPI translateGithub;

    public Optional<Participation> findParticipationAll() {
        try {
            final Participation participation = translateGithub.translate().orElseThrow(() -> new IllegalAccessError("번역을 잘못했어영"));
            log.info("github api 변역 완료");
            return Optional.of(participation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
