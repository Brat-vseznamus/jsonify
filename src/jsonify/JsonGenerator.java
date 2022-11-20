package jsonify;

import jsonify.elements.JsonArrayDescription;
import jsonify.elements.JsonClassDescription;
import jsonify.elements.JsonDescription;
import jsonify.elements.JsonMapDescription;
import jsonify.elements.JsonPrimitiveDescription;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class JsonGenerator {
    public JsonGenerator() {}

    public JsonDescription parse(Class<?> clazz) {
        return parseType(clazz, new HashMap<>());
    }

    private static JsonDescription parse(Class<?> clazz, Map<TypeVariable<?>, JsonDescription> generics) {
        if (String.class.isAssignableFrom(clazz)
            || Number.class.isAssignableFrom(clazz)
            || clazz.isPrimitive()) {
            return new JsonPrimitiveDescription(clazz.getSimpleName());
        }

        if (clazz.isArray()) {
            return new JsonArrayDescription(parse(clazz.getComponentType(), generics));
        }

        JsonClassDescription classTree = new JsonClassDescription();

        for (Field field: clazz.getDeclaredFields()) {
            Type actualType = field.getGenericType();
            classTree.addProperty(field.getName(), parseType(actualType, generics));
        }

        return classTree;
    }

    private static JsonDescription parseType(Type type, Map<TypeVariable<?>, JsonDescription> generics) {
        // if type is generic inheritance of collection
        if (type instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> typeClass
                && Collection.class.isAssignableFrom(typeClass)) {

            if (typeClass.equals(Collection.class)) {
                return new JsonArrayDescription(parseType(parameterizedType.getActualTypeArguments()[0], generics));
            }

            JsonArrayDescription arrayDescription = getJsonArrayDescription(
                    generics,
                    parameterizedType.getActualTypeArguments(),
                    typeClass
            );

            if (arrayDescription != null) return arrayDescription;
        }

        // if type is generic inheritance of map
        if (type instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> typeClass
                && Map.class.isAssignableFrom(typeClass)) {

            if (typeClass.equals(Map.class)) {
                return new JsonMapDescription(
                        parseType(parameterizedType.getActualTypeArguments()[0], generics),
                        parseType(parameterizedType.getActualTypeArguments()[1], generics)
                );
            }

            JsonMapDescription mapDescription = getJsonMapDescription(
                    generics,
                    parameterizedType.getActualTypeArguments(),
                    typeClass
            );

            if (mapDescription != null) return mapDescription;
        }

        // if type is type variable
        if (type instanceof TypeVariable<?> typeVariable) {
            if (generics.containsKey(typeVariable)) {
                return generics.get(typeVariable);
            } else {
                return new JsonPrimitiveDescription(type.getTypeName());
            }
        }

        // if type is common class
        if (type instanceof Class<?> clazz) {
            // if type is inheritance of collection
            if (Collection.class.isAssignableFrom(clazz)) {
                Optional<ParameterizedType> parameterizedType = getInterfaceRoot(clazz, Collection.class);

                if (parameterizedType.isPresent()) {
                    return new JsonArrayDescription(
                            parseType(parameterizedType.get().getActualTypeArguments()[0], generics)
                    );
                }
            }

            // if type is inheritance of map
            if (Map.class.isAssignableFrom(clazz)) {
                Optional<ParameterizedType> parameterizedType = getInterfaceRoot(clazz, Map.class);

                if (parameterizedType.isPresent()) {
                    return new JsonMapDescription(
                            parseType(parameterizedType.get().getActualTypeArguments()[0], generics),
                            parseType(parameterizedType.get().getActualTypeArguments()[1], generics)
                    );
                }
            }
            return parse(clazz, Map.of());
        }

        // if type is generic type
        if (type instanceof ParameterizedType parameterizedType) {
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            var vars = parameterizedType.getActualTypeArguments();
            var trees = new HashMap<TypeVariable<?>, JsonDescription>();

            IntStream.range(0, vars.length)
                    .boxed()
                    .forEach(i -> {
                        trees.put(rawType.getTypeParameters()[i], parseType(vars[i], generics));
                    });

            return parse(rawType, trees);

        }

        // if type is generic array
        if (type instanceof GenericArrayType arrayType) {
            return new JsonArrayDescription(parseType(arrayType.getGenericComponentType(), generics));
        }

        // if it is wildcard
        if (type instanceof WildcardType wildcardType) {
            if (wildcardType.getUpperBounds().length == 0) {
                return parseType(Object.class, generics);
            }
            return parseType(wildcardType.getUpperBounds()[0], generics);
        }

        return null;
    }

    private static JsonMapDescription getJsonMapDescription(
            Map<TypeVariable<?>, JsonDescription> typeTrees,
            Type[] types,
            Class<?> typeClass) {
        Optional<ParameterizedType> collectionType = getInterfaceRoot(typeClass, Map.class);

        if (collectionType.isPresent()) {
            TypeVariable<?> searchingType0 = (TypeVariable<?>) collectionType.get().getActualTypeArguments()[0];
            TypeVariable<?> searchingType1 = (TypeVariable<?>) collectionType.get().getActualTypeArguments()[1];

            var indexOfType0 = Arrays.asList(typeClass.getTypeParameters()).indexOf(searchingType0);
            var indexOfType1 = Arrays.asList(typeClass.getTypeParameters()).indexOf(searchingType1);

            return new JsonMapDescription(
                    parseType(types[indexOfType0], typeTrees),
                    parseType(types[indexOfType1], typeTrees)
            );
        }
        return null;
    }

    private static JsonArrayDescription getJsonArrayDescription(
            Map<TypeVariable<?>, JsonDescription> typeTrees,
            Type[] types,
            Class<?> typeClass
    ) {
        Optional<ParameterizedType> collectionType = getInterfaceRoot(typeClass, Collection.class);

        if (collectionType.isPresent()) {
            TypeVariable<?> searchingType = (TypeVariable<?>) collectionType.get().getActualTypeArguments()[0];
            var indexOfType = Arrays.asList(typeClass.getTypeParameters()).indexOf(searchingType);

            return new JsonArrayDescription(parseType(types[indexOfType], typeTrees));
        }
        return null;
    }

    private static Optional<ParameterizedType> getInterfaceRoot(Class<?> clazz, Class<?> targetClass) {
        return Arrays.stream(clazz.getGenericInterfaces())
                .filter(type1 -> type1 instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(parameterizedType -> parameterizedType.getRawType().equals(targetClass))
                .findFirst();
    }
}
