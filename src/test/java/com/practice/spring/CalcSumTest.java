package com.practice.spring;

import com.practice.spring.learningtest.template.Calculator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {

    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(getClass().getResource(
                "numbers.txt"
        ).getPath());

        assertThat(sum).isEqualTo(10);
    }
}
