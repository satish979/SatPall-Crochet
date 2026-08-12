package com.satpall.crochet.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FeedbackForm {

    @Min(value = 1, message = "Please select a rating")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 500, message = "Feedback must be less than 500 characters")
    private String comment;
}
