package com.example.taskprioritiser.internal.service.repsoitory;

import com.example.taskprioritiser.internal.service.repsoitory.entity.ScoreEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Deprecated
public class ScorePersistenceService {
    
    private final ScoreRepository scoreRepository;
    
    public ScorePersistenceService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Transactional
    public void updateWeight(ScoreEntity scoreEntity) {
        validateWeight(scoreEntity.getWeight());
//        scoreRepository.updateScoreWeight(scoreEntity.getScore(), scoreEntity.getWeight());
    }

    @Transactional
    public ScoreEntity getScore(String score) {
        return scoreRepository.fetchScore(score);
    }

    // does this need to be a list? A map would be better, but should that be returned here or service layer?
    @Transactional
    public List<ScoreEntity> getAllScores() {
        return scoreRepository.fetchAllScores();
    }

    private void validateWeight(int weight) {
        if (weight < 0 || weight > 10) {
            throw new IllegalArgumentException("Weight must be between 0 and 10");
        }
    }
}
