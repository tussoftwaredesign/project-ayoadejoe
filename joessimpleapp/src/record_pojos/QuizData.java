package record_pojos;

import java.util.List;

import enum_oops2.QuizCategory;

public record QuizData(String question, List<String> options, String correctAnswer, QuizCategory category) { }
