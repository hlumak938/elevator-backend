package org.sytoss.bdd.configuration;

import io.cucumber.java.ParameterType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterTypes {

    @ParameterType("\\[([0-9, ]*)\\]")
    public List<Integer> listOfIntegers (String integers) {
        return Arrays.stream(integers.split(", ")).map(Integer::parseInt).collect(Collectors.toList());
    }
}
