package com.wemojema;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseTest {

    protected static Faker faker = new Faker();

    @Test
    void should_compile() {
        Assertions.assertTrue(true);
    }

}
