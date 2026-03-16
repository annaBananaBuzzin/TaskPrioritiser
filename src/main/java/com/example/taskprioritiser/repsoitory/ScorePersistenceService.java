package com.example.taskprioritiser.repsoitory;

import java.util.List;

public class ScorePersistenceService {

    // adding @Transacational right?
    // add persistence context
    
    private final ScoreRepository scoreRepository;
    
    public ScorePersistenceService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public void updateWeight(ScoreEntity scoreEntity) {
        validateWeight(scoreEntity.getWeight());
        scoreRepository.updateScoreWeight(scoreEntity.getScore(), scoreEntity.getWeight());
    }

    public ScoreEntity getScore(String score) {
        return scoreRepository.fetchScore(score);
    }

    public List<ScoreEntity> getAllScores() {
        return scoreRepository.fetchAllScores();
    }

    private void validateWeight(int weight) {
        if (weight < 0 || weight > 10) {
            throw new IllegalArgumentException("Weight must be between 0 and 10");
        }
    }
}
