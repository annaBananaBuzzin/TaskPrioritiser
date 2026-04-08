package com.example.taskprioritiser.internal.service;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.ServiceConfig;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import com.example.taskprioritiser.internal.service.model.Task;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PrioritiserService {

    private final ServiceConfig serviceConfig;

    private final TaskService taskService;
    private final PrioritiserConfig prioritiserConfig;

    public PrioritiserService(TaskService taskService, PrioritiserConfig prioritiserConfig, ServiceConfig config) {
        this.taskService = taskService;
        this.prioritiserConfig = prioritiserConfig;
        this.serviceConfig = config;
    }

    public List<Task> getPrioritisedTasks() {
        List<Task> tasks = taskService.getAllTasks();

        // Don't like this
        ZoneId zoneId = serviceConfig.getZoneId();

        LocalDate today = LocalDate.now(zoneId);

        // Separate tasks due today from others as they will need to be top focus
        Map<Boolean, List<Task>> partitionedTasksByToday = tasks.stream()
                .collect(Collectors.partitioningBy(task ->
                        task.getDeadline().atZone(zoneId).toLocalDate().equals(today)));

        // tasks are now prioritised
        partitionedTasksByToday.replaceAll((isToday, tasksSubList)->
               prioritiseTaskSubList(tasksSubList, isToday));

        return Stream.concat(partitionedTasksByToday.get(true).stream(), partitionedTasksByToday.get(false).stream()).toList();
    }

    private List<Task> prioritiseTaskSubList(List<Task> taskSubList, boolean isToday) {
        return taskSubList.stream()
                .collect(Collectors.toMap(
                        task -> task,
                        task -> calculatePriorityScore(task, isToday)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
    }

    // allows specific testing - should this be in a separate class?
    public double calculatePriorityScore(Task task, boolean isToday) {
        Map<ScoreType, Integer> scoreWeights = prioritiserConfig.getScoreWeightMap();

        int impactWeightedScore = task.getImpactScore().getValue() * scoreWeights.get(ScoreType.IMPACT);
        int urgencyWeightedScore = task.getUrgencyScore().getValue() * scoreWeights.get(ScoreType.URGENCY);

        int benefitScore = impactWeightedScore + urgencyWeightedScore;

        double reliefScore = calculateReliefScore(task.getEffortScore().getValue(), task.getDeadline(), isToday, scoreWeights.get(ScoreType.EFFORT));

        return benefitScore + reliefScore;
    }

    private double getDeadlineVariable(Instant deadline, Function<Duration, Double> timeUnitCalculator) {
        Instant now = Instant.now();
        Duration duration = Duration.between(now, deadline);
        return timeUnitCalculator.apply(duration);
    }

    private double calculateReliefScore(int effortScore, Instant deadline, boolean isToday, int effortScoreWeight) {
        if (deadline == null) {
            return (prioritiserConfig.getScoreMaxValue() - effortScore + 1) * effortScoreWeight;
        }

        // if today
        // hour

        // if negatve a if postive b
        // is this bad practise? to create a function here not just pass it through
        Function<Duration, Double> deadlineVariable = isToday ? this::getTimeDueVariable : this::getDateDueVariable;
        // y = a / x < for positive
        // but foe negative i want y = a^x

        return (double) (effortScore * effortScoreWeight) / getDeadlineVariable(deadline, deadlineVariable);
    }

    private double getTimeDueVariable(Duration duration) {
        long hours = duration.toHours();
        return (hours < 0 ? 0 : hours) + 1;
    }

    private double getDateDueVariable(Duration duration) {
        long days = duration.toDays();
        // if due tomorrow will be 0 days so the deadline constant helps without offsets and impossible equations
        return days < 0 ? days * -0.5 : days + prioritiserConfig.getDayDeadlineConstant();
    }

}
