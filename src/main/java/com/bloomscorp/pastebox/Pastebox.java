package com.bloomscorp.pastebox;

import com.bloomscorp.pastebox.support.Constant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public final class Pastebox {

    /*
     * ATTRIBUTION:
     *
     * > https://stackoverflow.com/a/1128728/3640307
     * > https://stackoverflow.com/a/112542/3640307
     * > https://stackoverflow.com/a/2932439/3640307
     * > https://stackoverflow.com/a/11049108/3640307
     */

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Pastebox() {}

    public static boolean isEmptyString(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Contract(pure = true)
    public static boolean minLength(@NotNull String value, int length) {
        return value.length() >= length;
    }

    @Contract(pure = true)
    public static boolean maxLength(@NotNull String value, int length) {
        return value.length() <= length;
    }

    public static boolean pattern(String value,String pattern) {

        Pattern regex = Pattern.compile(pattern);
        if (value == null) {
            return false;
        }
        Matcher m = regex.matcher(value);
        return m.matches();
    }

    public static boolean equalStrings(@NotNull final String s1, final String s2) {
        return s1.equals(s2);
    }

    public static <E extends Enum<E>> boolean isInEnum(final String value, @NotNull Class<E> enumClass) {
        for(E e : enumClass.getEnumConstants())
            if(e.toString().equals(value))
                return true;
        return false;
    }

    public static <E extends Enum<E>> boolean includesInEnum(final String value, @NotNull Class<E> enumClass) {
        for(E e : enumClass.getEnumConstants())
            if(e.toString().toLowerCase().contains(value.toLowerCase()))
                return true;
        return false;
    }

    public static <E extends Enum<E>> boolean isInEnum(final int value, @NotNull Class<E> enumClass) {
        for(E e : enumClass.getEnumConstants())
            if(e.toString().equals(Integer.toString(value)))
                return true;
        return false;
    }

    public static <E extends Enum<E>> boolean isInEnum(E enumValue, @NotNull Class<E> enumClass) {
        for(E e : enumClass.getEnumConstants())
            if(e == enumValue)
                return true;
        return false;
    }

    public static long getCurrentTimeInMillis() { return System.currentTimeMillis(); }

    public static String @NotNull [] splitCSVtoStringArray(String csv) {
        if(!Pastebox.isEmptyString(csv)) return csv.split(","); return new String[0];
    }

    public static boolean stringArrayContainsValue(String[] array, String value) {
        return Arrays.asList(array).contains(value);
    }

    public static boolean intArrayContainsValue(int[] array, int value) {
        return IntStream.of(array).anyMatch(x -> x == value);
    }

    public static boolean doubleArrayContainsValue(double[] array, double value) {
        return DoubleStream.of(array).anyMatch(x -> x == value);
    }

    public static boolean longArrayContainsValue(long[] array, long value) {
        return LongStream.of(array).anyMatch(x -> x == value);
    }

    public static long getLongFromStringValue(String value) {
        return Long.parseLong(value);
    }

    public static boolean isEmptyArray(Object[] array) {
        return (array == null) || (array.length == 0);
    }

    public static @NotNull String removeSpaces(@NotNull String value) {
        return value.trim().replaceAll(" +", "");
    }

    public static @NotNull String removeExtraSpaces(@NotNull String value) {
        return value.trim().replaceAll(" +", " ");
    }

    @Contract(pure = true)
    public static @NotNull String removeNewLineReturnTabWithSpace(@NotNull String value) {
        return value.replaceAll("[\\t\\n\\r]+", " ");
    }

    public static String @NotNull [] removeElementFromStringArray(String[] array, String value) {

        List<String> list = new ArrayList<>(Arrays.asList(array));
        list.removeAll(Collections.singletonList(value));

        return list.toArray(Pastebox.EMPTY_STRING_ARRAY);
    }

    public static long @NotNull [] splitCSVtoLongArray(String csv) {

        try {
            long[] longValues;
            String[] stringValues = Pastebox.splitCSVtoStringArray(csv);

            if(stringValues.length < 1) return new long[0];

            longValues = new long[stringValues.length];

            for(int i = 0; i < stringValues.length; i++) longValues[i] = Long.parseLong(stringValues[i]);

            return longValues;
        } catch(NumberFormatException e) { return new long[0]; }
    }

    public static @NotNull String concatStringArrayToCSV(String[] array) {

        int i;
        StringBuilder csv = new StringBuilder();

        if(Pastebox.isEmptyArray(array))
            return csv.toString();

        for(i = 0; i < array.length - 1; i++)
            csv.append(array[i]).append(",");

        return csv.append(array[i]).toString();
    }

    public static @NotNull String getStackTraceAsString(@NotNull Exception exception, String separator) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        StringBuilder stackTrace = new StringBuilder(Constant.BLANK_STRING_VALUE);

        exception.printStackTrace(pw);
        stackTrace.append(sw.toString());

        if(exception.getCause() != null) {
            exception.getCause().printStackTrace(pw);
            stackTrace.append(separator).append(sw.toString());
        }

        return stackTrace.toString();
    }

    public static @NotNull String prepareGetterMethodName(@NotNull String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static @NotNull String prepareSetterMethodName(@NotNull String field) {
        return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static List<String> getClassFields(@NotNull Object object) {
        return Arrays
                .stream(object.getClass().getDeclaredFields())
                .toList()
                .stream()
                .parallel()
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    public static @NotNull Method getFieldGetterMethod(String field, @NotNull Object object) throws NoSuchMethodException, SecurityException {
        return object.getClass().getMethod(
                Pastebox.prepareGetterMethodName(field),
                (Class<?>[]) null
        );
    }

    public static @NotNull Method getFieldSetterMethod(String field, Class<?> param, @NotNull Object object) throws NoSuchMethodException, SecurityException {
        return object.getClass().getMethod(
                Pastebox.prepareSetterMethodName(field),
                param
        );
    }

    public static String getFieldNameFromGetterMethod(@NotNull Method method) {

        String methodName = method.getName();

        if(!methodName.startsWith("get")) return Constant.BLANK_STRING_VALUE;

        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }

    @Contract("_, null, _ -> false")
    public static boolean fieldHasType(String fieldName, Class<?> type, @NotNull Object object) throws NoSuchFieldException {
        return object.getClass().getDeclaredField(fieldName).getType().equals(type);
    }

    @Contract("_, null -> false")
    public static boolean methodHasReturnType(@NotNull Method method, Class<?> returnType) {
        return method.getReturnType().equals(returnType);
    }

    @Contract(pure = true)
    public static @NotNull String keepAlphanumericAndSpace(@NotNull String value) {
        return value.replaceAll("[^A-Za-z0-9\\s]", "");
    }

    @Contract(pure = true)
    public static @NotNull String replaceMultipleSpaceWithSingleSpace(@NotNull String value) {
        return value.replaceAll(" +", " ");
    }

    public static boolean containsOnlyAlphanumeric(String value) {
        return !Pastebox.isEmptyString(value) && value.matches("[A-Za-z0-9]+");
    }

    public static boolean replaceStringWithBoolean(String value) {
        return !Pastebox.isEmptyString(value) && value.equalsIgnoreCase("yes");
    }

    public static String getNullSafeString(String value) {
        return Pastebox.isEmptyString(value) ? Constant.BLANK_STRING_VALUE : value;
    }
}
