import jsonify.JsonGenerator;

public class Main {
    public static void main(String[] args) {
        Class<?> clazz = String.class;
        System.out.println(new JsonGenerator().parse(clazz).toJson(2));
    }
}