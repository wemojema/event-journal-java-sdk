package com.eventjournal.api.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wemojema.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ToolboxTest extends BaseTest {

    EventJournal.Toolbox uut;


    @Test
    void should_deserialize_a_list_of_objects() {
        List<Pojo> pojos = new ArrayList<>();
        pojos.add(new Pojo(faker.starTrek().character(), Arrays.asList(faker.starTrek().location(), faker.starTrek().location()),
                Map.of(faker.starTrek().location(), faker.random().nextInt(1, 10), faker.starTrek().character(), faker.random().nextInt(1, 10))));
        String testString = EventJournal.Toolbox.serialize(pojos);
        System.out.println(testString);
        List<Pojo> result = EventJournal.Toolbox.deserialize(testString, Pojo.class, List.class);
        Assertions.assertEquals(pojos.get(0).foo, result.get(0).foo);
    }


    public static class Pojo {
        String foo;
        List<String> bar;
        @JsonDeserialize(as = LinkedHashMap.class)
        Map<String, Integer> map;

        public Pojo() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pojo pojo = (Pojo) o;

            if (!Objects.equals(foo, pojo.foo)) return false;
            if (!(bar.containsAll(pojo.bar) && pojo.bar.containsAll(bar))) return false;
            return map.keySet().containsAll(pojo.map.keySet())
                    && pojo.map.keySet().containsAll(map.keySet())
                    && map.entrySet().stream().allMatch(entry -> pojo.map.get(entry.getKey()).equals(entry.getValue()));

        }

        @Override
        public int hashCode() {
            int result = foo != null ? foo.hashCode() : 0;
            result = 31 * result + (bar != null ? bar.hashCode() : 0);
            result = 31 * result + (map != null ? map.hashCode() : 0);
            return result;
        }

        public Pojo(String foo, List<String> bar, Map<String, Integer> map) {
            this.foo = foo;
            this.bar = bar;
            this.map = map;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public List<String> getBar() {
            return bar;
        }

        public void setBar(List<String> bar) {
            this.bar = bar;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return "Pojo{" +
                    "foo='" + foo + '\'' +
                    ", bar=" + bar +
                    ", map=" + map +
                    '}';
        }
    }
}