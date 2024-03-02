package com.practice.spring;

import com.practice.spring.learningtest.template.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {

    Calculator calculator;
    String numFilePath;

    @BeforeEach
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilePath = getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(this.calculator.calcSum(numFilePath)).isEqualTo(10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        assertThat(this.calculator.calcMultiply(numFilePath)).isEqualTo(24);
    }

    @Test
    public void concatenateOfNumbers() throws IOException {
        assertThat(this.calculator.concatenate(numFilePath)).isEqualTo("1234");
    }
}
