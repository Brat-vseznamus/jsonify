package jsonify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

class JsonGeneratorTest {

    @Test
    void parsePrimitive() {
        var clazz = int.class;

        Assertions.assertEquals(
                "\"" + clazz.getSimpleName() + "\"",
                new JsonGenerator().parse(clazz).toJson());
    }

    @Test
    void parseNumber() {
        var clazz = Double.class;

        Assertions.assertEquals(
                "\"" + clazz.getSimpleName() + "\"",
                new JsonGenerator().parse(clazz).toJson());
    }

    @Test
    void parseString() {
        var clazz = String.class;

        Assertions.assertEquals(
                "\"" + clazz.getSimpleName() + "\"",
                new JsonGenerator().parse(clazz).toJson());
    }

    private static class TestClass1 {}

    @Test
    void parseEmptyClass() {
        var clazz = TestClass1.class;

        Assertions.assertEquals(
                "{}",
                new JsonGenerator().parse(clazz).toJson());
    }

    private static class TestClass2 {
        private int a;
        private double b;
    }

    @Test
    void parseSimpleClass() {
        var clazz = TestClass2.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"a\": \"int\",\n" +
                        "\"b\": \"double\"\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass3{
        private List<Integer> l;
    }

    @Test
    void parseClassWithCollection() {
        var clazz = TestClass3.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"l\": [\n" +
                        "\"Integer\"\n" +
                        "]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass4{
        private Map<Integer, Double> m;
    }


    @Test
    void parseClassWithMap() {
        var clazz = TestClass4.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"m\": [{\n" +
                        "\"key\": \"Integer\",\n" +
                        "\"value\": \"Double\"\n" +
                        "}]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass5{
        private Map<Integer, Set<Double>> m;
    }

    @Test
    void parseClassWithMapOfCollection() {
        var clazz = TestClass5.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"m\": [{\n" +
                        "\"key\": \"Integer\",\n" +
                        "\"value\": [\n" +
                        "\"Double\"\n" +
                        "]\n" +
                        "}]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass6{
        private Map<Integer, Set<Double>> f1;
        private InnerGenericClass<Long> f2;

        private static class InnerGenericClass<E> {
            private E value;
        }
    }

    @Test
    void parseClassWithGenerics() {
        var clazz = TestClass6.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"f1\": [{\n" +
                        "\"key\": \"Integer\",\n" +
                        "\"value\": [\n" +
                        "\"Double\"\n" +
                        "]\n" +
                        "}],\n" +
                        "\"f2\": {\n" +
                        "\"value\": \"Long\"\n" +
                        "}\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass7<T>{
        private Map<Integer, Set<Double>> f1;
        private InnerGenericClass<T> f2;

        private static class InnerGenericClass<E> {
            private E value;
        }
    }

    @Test
    void parseClassWithUnknownGenerics() {
        var clazz = TestClass7.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"f1\": [{\n" +
                        "\"key\": \"Integer\",\n" +
                        "\"value\": [\n" +
                        "\"Double\"\n" +
                        "]\n" +
                        "}],\n" +
                        "\"f2\": {\n" +
                        "\"value\": \"T\"\n" +
                        "}\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass8{
        private Integer[] l;
    }

    @Test
    void parseClassWithArray() {
        var clazz = TestClass8.class;

        Assertions.assertEquals(
                "{\n" +
                        "\"l\": [\n" +
                        "\"Integer\"\n" +
                        "]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }


    private static class TestClass9{
        private List<?> l;
    }

    @Test
    void parseClassUnboundedWildcard() {
        var clazz = TestClass9.class;

        // bounds by Object
        Assertions.assertEquals(
                "{\n" +
                        "\"l\": [\n" +
                        "{}\n" +
                        "]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }

    private static class TestClass10{
        private List<? extends Number> l;
    }

    @Test
    void parseClassBoundedWildcard() {
        var clazz = TestClass10.class;

        // bounds by Number
        Assertions.assertEquals(
                "{\n" +
                        "\"l\": [\n" +
                        "\"Number\"\n" +
                        "]\n" +
                        "}",
                new JsonGenerator().parse(clazz).toJson(0));
    }
}