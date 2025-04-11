package dbConnect;

import com.google.gson.Gson;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some potentially useful utility methods.
 */
public class Utility {
    /**
     * Enums for time formatting, support digit time display with separator of length 1.
     * <div>
     * Supported time string formats:
     * <li>{@link #DD_MM_YYYY}</li>
     * <li>{@link #MM_DD_YYYY}</li>
     * <li>{@link #YYYY_MM_DD}</li>
     * </div>
     */
    public enum TimeFormat {
        DD_MM_YYYY("dd'%s'MM'%s'yyyy"),
        MM_DD_YYYY("MM'%s'dd'%s'yyyy"),
        YYYY_MM_DD("yyyy'%s'MM'%s'dd");

        private final String pattern;

        TimeFormat(String pattern) {
            this.pattern = pattern;
        }

        public String getFormattedPattern(String separator) {
            return String.format(pattern, separator, separator);
        }
    }

    /**
     * Convert date String that matched {@link TimeFormat} to {@code Date} Object.
     * @param dateString A string of date consists of day, month and year.
     * @param timeFormat The date order of the string following {@link TimeFormat} supported enums.
     * @param separator the separator used in the date string.
     * @return {@link java.util.Date} Object from the given date string.
     */
    public static java.util.Date parseDate(String dateString, TimeFormat timeFormat, String separator) {
        String pattern = timeFormat.getFormattedPattern(separator);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"));

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);

        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Convert {@code Date} object to String presentation.
     * @param date the date object to convert.
     * @param separator the string use for separation between date field.
     * @param timeFormat the date order of the string following {@link TimeFormat} supported enums.
     * @param includeTime true to add HH:MM in front of the date string.
     * @return A string presentation of the date object.
     * @throws IllegalArgumentException Invalid enum of {@link TimeFormat}.
     */
    public static String parseDate(java.util.Date date, String separator, TimeFormat timeFormat, boolean includeTime) throws IllegalArgumentException {
        Instant instant = date.toInstant();

        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));

        String pattern = timeFormat.getFormattedPattern(separator);

        if (includeTime) {
            pattern = "HH:mm " + pattern;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        String parsedDate = zonedDateTime.format(formatter);

        return parsedDate + " UTC";
    }

    // Internal parsing method.
    public static String appendPlaceholderValue(String filter, Object[] params, int argCount) {
        if (filter.isEmpty()) {
            filter = "{}";
        } else {
            if (!filter.startsWith("{")) {
                filter = "{" + filter;
            }

            if (!filter.endsWith("}")) {
                filter = filter + "}";
            }
        }

        for (int i = 0; i < argCount; i++) {
            Object param = params[i];

            String appending;

            if (param instanceof ObjectId) {
                appending = "ObjectId(\"" + param + "\")";
            } else if (param instanceof String strParam) {
                if (strParam.startsWith("/") && strParam.endsWith("/")) {
                    appending = strParam;
                } else {
                    appending = "\"" + strParam + "\"";
                }
            } else if (param instanceof Number || param instanceof Boolean) {
                appending = param.toString();
            } else {
                appending = new Gson().toJson(params);
            }

            filter = filter.replaceFirst("\\?", appending);
        }

        return filter;
    }

    // Internal params counter.
    public static int countFilterParams(String filter) {
        Matcher matcher = Pattern.compile("\\?").matcher(filter);
        int count = 0;

        while (matcher.find()){
            count++;
        }

        return count;
    }
}
