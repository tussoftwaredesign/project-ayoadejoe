package record_pojos;

import java.util.List;

import enum_oops2.QuizCategory;

/**
 * Represents a single quiz question and its properties.
 * Used in quiz and scoring logic.
 */
public record QuizData(String question, List<String> options, String correctAnswer, QuizCategory category) { }
