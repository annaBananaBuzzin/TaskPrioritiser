package com.example.taskprioritiser.internal.service;

import com.example.taskprioritiser.internal.service.model.ScoreType;
import com.example.taskprioritiser.internal.service.model.Task;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PrioritiserService {

    // These along with weights should be in config
    // and timeZone
    private static final int DAY_DEADLINE_CONSTANT = 3;
    private static final int MAX_SCORE = 10;

    private final TaskService taskService;

    public PrioritiserService(TaskService taskService) {
        this.taskService = taskService;
    }

    public List<Task> getPrioritisedTasks() {
        List<Task> tasks = taskService.getAllTasks();

        // set in config
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        // Separate tasks due today from others as they will need to be top focus
        Map<Boolean, List<Task>> partitionedTasksByToday = tasks.stream()
                .collect(Collectors.partitioningBy(task ->
                        task.getDeadline().atZone(zone).toLocalDate().equals(today)));

        Map<ScoreType, Integer> scoreWeights = getScoreWeightMap();

        // tasks are now prioritised
        partitionedTasksByToday.replaceAll((isToday, tasksSubList)->
               prioritiseTaskSubList(tasksSubList, isToday, scoreWeights));

        return Stream.concat(partitionedTasksByToday.get(true).stream(), partitionedTasksByToday.get(false).stream()).toList();
    }

    private List<Task> prioritiseTaskSubList(List<Task> taskSubList, boolean isToday, Map<ScoreType, Integer> scoreWeights) {
        return taskSubList.stream()
                .collect(Collectors.toMap(
                        task -> task,
                        task -> calculatePriorityScore(task, isToday, scoreWeights)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private double calculatePriorityScore(Task task, boolean isToday, Map<ScoreType, Integer> scoreWeights) {
        // Get priority equivalent scores
        int effortWeightScore = task.getEffortScore().getValue() * scoreWeights.get(ScoreType.EFFORT);
        int impactWeightedScore = task.getImpactScore().getValue() * scoreWeights.get(ScoreType.IMPACT);
        int urgencyWeightedScore = task.getUrgencyScore().getValue() * scoreWeights.get(ScoreType.URGENCY);

        int benefitScore = impactWeightedScore + urgencyWeightedScore;

        double reliefScore = calculateReliefScore(effortWeightScore, task.getDeadline(), isToday);

        return benefitScore + reliefScore;
    }

    private double getDeadlineVariable(Instant deadline, Function<Duration, Double> timeUnitCalculator) {
        Instant now = Instant.now();
        Duration duration = Duration.between(now, deadline);
        return timeUnitCalculator.apply(duration);
    }

    private double calculateReliefScore(int effortWeightScore, Instant deadline, boolean isToday) {
        if (deadline == null) {
            return MAX_SCORE - effortWeightScore + 1;
        }
        // is this bad practise? to create a function here not just pass it through
        Function<Duration, Double> deadlineVariable = isToday ? this::getTimeDueVariable : this::getDateDueVariable;
        return (double) effortWeightScore / getDeadlineVariable(deadline, deadlineVariable);
    }

    private double getTimeDueVariable(Duration duration) {
        long hours = duration.toHours();
        return (hours < 0 ? 0 : hours) + 1;
    }

    private double getDateDueVariable(Duration duration) {
        long days = duration.toDays();
        return days < 0 ? days + DAY_DEADLINE_CONSTANT : days * -0.5;
    }

    private Map<ScoreType, Integer> getScoreWeightMap() {
        // move to config
        Map<ScoreType, Integer> scoreWeightMap = new HashMap<>();
        scoreWeightMap.put(ScoreType.EFFORT, 3);
        scoreWeightMap.put(ScoreType.IMPACT, 3);
        scoreWeightMap.put(ScoreType.URGENCY, 2);
        return scoreWeightMap;
    }

}
