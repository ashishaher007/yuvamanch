package com.ymanch.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {

	// Utility method to convert LocalDateTime to a "time ago" format
	public static String convertToTimeAgo(LocalDateTime pastTime) {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(pastTime, now);

		if (duration.isNegative()) {
			return "In the future"; // Handle future dates
		}

		long seconds = duration.getSeconds();
		if (seconds == 0) {
			return "Just now";
		}
		if (seconds < 60) {
			return seconds + " seconds ago";
		}

		long minutes = duration.toMinutes();
		
		if (minutes < 60) {
			return minutes + " minutes ago";
		}

		long hours = duration.toHours();
		if (hours < 24) {
			return hours + " hours ago";
		}

		long days = duration.toDays();
		if (days < 7) {
			return days + " days ago";
		}

		long weeks = days / 7;
		if (weeks < 4) {
			return weeks + " weeks ago";
		}

		long months = ChronoUnit.MONTHS.between(pastTime, now);
		if (months == 1) { // If exactly 1 month ago
			return "1 month ago";
		} else if (months > 1) { // If more than 1 month ago
			return months + " months ago";
		}

		long years = ChronoUnit.YEARS.between(pastTime, now);
		if (years == 1) { // If exactly 1 year ago
			return "1 year ago";
		} else if (years > 1) { // If more than 1 year ago
			return years + " years ago";
		}

		// If all else fails, just return the months
		return months + " months ago"; // Fallback case
	}

	public static String convertUtcToLocalTimeAgo(LocalDateTime utcDateTime) {
		// Convert UTC LocalDateTime to ZonedDateTime in UTC
		ZonedDateTime utcZoned = utcDateTime.atZone(ZoneOffset.UTC);

		// Convert to the system's default time zone (or you can specify a time zone)
		ZonedDateTime localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault());

		// Convert ZonedDateTime to LocalDateTime for further "time ago" calculation
		return convertToTimeAgo(localZoned.toLocalDateTime());
	}
}
