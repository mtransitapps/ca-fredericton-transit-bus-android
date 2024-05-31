package org.mtransit.parser.ca_fredericton_transit_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// http://data.fredericton.ca/en
// https://data-fredericton.opendata.arcgis.com/
// https://data-fredericton.opendata.arcgis.com/datasets/google-transit-gtfs
public class FrederictonTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new FrederictonTransitBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN_FR;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true; // use route short name as route ID
	}

	@Override
	public @Nullable Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		switch (routeShortName.toLowerCase(Locale.ROOT)) {
		case "para":
			return 10_000L;
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "-\\d+$";
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), routeLongName, getIgnoredWords());
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_ORANGE = "FD6604"; // ORANGE (from PNG logo)

	private static final String AGENCY_COLOR = AGENCY_COLOR_ORANGE;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		switch (gRoute.getRouteShortName()) {
		// @formatter:off
		case "10": return "75923C"; // Dark Green
		case "10N": return "75923C"; // Dark Green
		case "11": return "75923C"; // Dark Green
		case "11S": return "75923C"; // Dark Green
		case "12": return "4169E1"; // Blue
		case "12N": return "4169E1"; // Blue
		case "13": return "4169E1"; // Blue
		case "13S": return "4169E1"; // Blue
		case "14": return "E60000"; // Red
		case "14N": return "E60000"; // Red
		case "15": return "E60000"; // Red
		case "15S": return "E60000"; // Red
		case "16": return "32CD32"; // Green
		case "16N": return "32CD32"; // Green
		case "17": return "32CD32"; // Green
		case "17S": return "32CD32"; // Green
		case "18": return "996633"; // Purple
		case "20": return "996633"; // Purple
		case "116": return "4B0082"; // Brown
		case "216": return "4B0082"; // Brown
		// @formatter:on
		}
		throw new MTLog.Fatal("Unexpected route color for %s!", gRoute.toStringPlus());
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STARTS_W_VIA_ = Pattern.compile("(^via .*$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanDirectionHeadsign(int directionId, boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = CleanUtils.keepToAndRemoveVia(directionHeadSign);
		directionHeadSign = STARTS_W_VIA_.matcher(directionHeadSign).replaceAll(EMPTY); // remove trip only containing "via abc"
		directionHeadSign = super.cleanDirectionHeadsign(directionId, fromStopName, directionHeadSign);
		return directionHeadSign;
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepVia(tripHeadsign, true);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"DEC", "UNB",
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), gStopName, getIgnoredWords());
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanSlashes(gStopName);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
