package heigit.ors.services.routing;

import static io.restassured.RestAssured.*;
//import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
//import java.util.Arrays;
import org.junit.Test;
import org.json.JSONObject;

import heigit.ors.services.common.EndPointAnnotation;
import heigit.ors.services.common.ServiceTest;

@EndPointAnnotation(name="routes")
public class ParamsTest extends ServiceTest {

	public ParamsTest() {
		addParameter("coordinatesShort", "8.85498,53.097323|8.78906,53.071752");
		addParameter("coordinatesShortFaulty", "8p.85498,53.097323|8.78906,53.071752");
		addParameter("coordinatesLong", "8.85498,53.097323|4.78906,53.071752");
		addParameter("elevation", true);
		addParameter("extra_info", "surface|suitability|steepness");
		addParameter("geometry", true);
		addParameter("geojson", "geojson");
		addParameter("polyline", "polyline");
		addParameter("encodedpolyline", "encodedpolyline");
		addParameter("instructions", true);
		addParameter("instructions_format_html", "html");
		addParameter("instructions_format_text", "text");
		addParameter("preference", "fastest");
		addParameter("profile", "cycling-regular");
		addParameter("units", "m");
		addParameter("languageUnknown", "huyd");
		System.out.println("calling..");

		// options for driving profiles
		JSONObject options = new JSONObject();
		options.put("avoid_features", "highways|tollways|ferries|tunnels|unpavedroads|tracks|fords");
		options.put("maximum_speed", "105");
		addParameter("drivingOptions", options.toString());

		// options for driving profiles with avoid polygon
		options = new JSONObject();
		options.put("avoid_features", "tunnels");
		options.put("maximum_speed", "75");
		JSONObject polygon = new JSONObject();
		polygon.put("type", "Polygon");
		String[][][] coords = new String[][][] { { { "8.91197", "53.07257" }, { "8.91883", "53.06081" },
				{ "8.86699", "53.07381" }, { "8.91197", "53.07257" } } };
		polygon.put("coordinates", coords);
		options.put("avoid_polygons", polygon);
		addParameter("drivingAvoidpolygonOptions", options.toString());

		// options for HGV profiles
		options = new JSONObject();
		options.put("avoid_features", "highways|tollways|ferries|tunnels|unpavedroads|tracks|fords");
		options.put("maximum_speed", "75");
		JSONObject profileParams = new JSONObject();
		profileParams.put("width", "5");
		profileParams.put("height", "3");
		profileParams.put("length", "10");
		profileParams.put("axleload", "2");
		profileParams.put("weight", "5");
		profileParams.put("hazmat", "true");
		options.put("profile_params", profileParams);
		addParameter("hgvOptions", options.toString());

		// options for cycling profiles
		options = new JSONObject();
		options.put("avoid_features", "ferries|pavedroads|fords");
		options.put("maximum_speed", "25");
		profileParams = new JSONObject();
		profileParams.put("maximum_gradient", "5");
		profileParams.put("difficulty_level", "1");
		options.put("profile_params", profileParams);
		addParameter("bicycleOptions", options.toString());

		// options for walking profiles
		options = new JSONObject();
		options.put("avoid_features", "steps|fords");
		options.put("maximum_speed", "5");
		addParameter("walkingOptions", options.toString());

	}

	/**
	 * @Test public void basicPingTest() {
	 *       given().when().get("/status").then().statusCode(200); }
	 *       .param("elevation", getParameter("elevation")) .param("geometry",
	 *       !((Boolean) getParameter("geometry")).booleanValue())
	 *       .param("geometry_format", getParameter("geometry_format"))
	 *       .param("instructions_format", getParameter("html"))
	 */

	@Test
	public void basicPingTest() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.body("any { it.key == 'routes' }", is(true))
				.statusCode(200);
	}

	@Test
	public void expectNoInstructions() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("instructions", !((Boolean) getParameter("instructions")).booleanValue())
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('segments')", is(false))
				.statusCode(200);
	}

	@Test
	public void expectInstructions() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("instructions", getParameter("instructions"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('segments')", is(true))
				.body("routes[0].segments.size()", is(greaterThan(0)))
				.statusCode(200);
	}

	@Test
	public void expectInstructionsAsText() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("instructions", getParameter("instructions"))
				.param("instructions_format", getParameter("instructions_format_text"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("info.query.instructions_format", is("text"))
				.statusCode(200);
	}

	@Test
	public void expectInstructionsAsHtml() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("instructions", getParameter("instructions"))
				.param("instructions_format", getParameter("instructions_format_html"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("info.query.instructions_format", is("html"))
				.statusCode(200);
	}

	@Test
	public void expectGeometry() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.statusCode(200);
	}

	@Test
	public void expectNoGeometry() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", !((Boolean) getParameter("geometry")).booleanValue())
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(false))
				.statusCode(200);
	}

	@Test
	public void expectGeometryGeojson() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("geometry_format", getParameter("geojson"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].geometry_format", is("geojson"))
				.body("routes[0].geometry.type", is("LineString"))
				.statusCode(200);
	}

	@Test
	public void expectGeometryPolyline() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("geometry_format", getParameter("polyline"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].geometry_format", is("polyline"))
				.body("routes[0].geometry", hasSize(243))
				.statusCode(200);
	}

	@Test
	public void expectGeometryEncodedPolyline() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("geometry_format", getParameter("encodedpolyline"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].geometry_format", is("encodedpolyline"))
				.body(
						"routes[0].geometry",
						is(
								"cqacIwn`u@p@lCN|@@B`@rAFG|AhEhApC`@x@r@xA`@t@Vf@|@`BVb@T`@bBbDf@`ADH^p@f@~@x@|AHNjAlBPRPRfAlAn@j@tAbArA|@VRVVdAhA`C`DrAtBz@tA`AnBDHNXDOBGDSP`@H\\DVTrAl@`DdBpKFZBJ@HPfATvA`@hC@FN`AN|@L`AjBxLLl@BPA\\?J?JTpAl@nDVvAHf@f@vCDX@Lx@jF~@|Fv@jFN~@BZ@P?F@j@?LBNDRPvABLA@CBCDEHADBNAJCP@VDRDJFHHD?LBV@L@FBV@lFLhF\\hGDf@\\|DZvCJlAD^@HJr@Hb@Nj@x@dCFN@DDHDHDFFJd@p@dClDLRj@~@FHBFPLLJDFZXDDVV@B@BJTDNd@rDFd@Rz@DLBDv@pANVPZNj@@FNx@Pz@DTBJ@F@FH^DNJVR\\Zb@LLFHBBPPt@v@JL^ZVTCJCJi@rCQbAIj@StAERSxAQlAI\\_@t@AJi@~D[`CCLE\\AJEZCPADCJAHCRXj@b@t@^r@Rf@Xn@~BlGDLFNTf@DJBBBH@BFHVb@p@lA^n@VXBFDDjDbG`@r@BDrA~BJPFJFJzFxJzA|CVh@D\\FHDHDFHJ`C|DJP~AjCHLv@nAPVd@r@RZLPDFFJz@xAL???PNt@pAJHFJAB_AjB"))
				.statusCode(200);
	}

	@Test
	public void expectElevation() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("geometry_format", getParameter("geojson"))
				.param("elevation", getParameter("elevation"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].geometry_format", is("geojson"))
				.body("routes[0].geometry.type", is("LineString"))
				.body("routes[0].geometry.coordinates[0]", hasSize(3))
				.statusCode(200);
	}

	@Test
	public void expectNoElevation() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("geometry_format", getParameter("geojson"))
				.param("elevation", !((Boolean) getParameter("elevation")).booleanValue())
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].geometry_format", is("geojson"))
				.body("routes[0].geometry.type", is("LineString"))
				.body("routes[0].geometry.coordinates[0]", hasSize(2))
				.statusCode(200);
	}

	@Test
	public void expectExtrainfo() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("profile", getParameter("profile"))
				.param("geometry", getParameter("geometry"))
				.param("extra_info", getParameter("extra_info"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("any { it.key == 'routes' }", is(true))
				.body("routes[0].containsKey('geometry')", is(true))
				.body("routes[0].containsKey('extras')", is(true))
				.body("routes[0].extras.containsKey('surface')", is(true))
				.body("routes[0].extras.containsKey('suitability')", is(true))
				.body("routes[0].extras.containsKey('steepness')", is(true))
				.statusCode(200);
	}

	@Test
	public void expect400201() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("geometry", getParameter("geometry"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("error.code", is(201))
				.statusCode(400);
	}

	@Test
	public void expect400202() {

		given()
				.param("coordinates", getParameter("coordinatesShortFaulty"))
				.param("preference", getParameter("preference"))
				.param("geometry", getParameter("geometry"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("error.code", is(202))
				.statusCode(400);
	}

	@Test
	public void expect400203() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("geometry", getParameter("geometry"))
				.param("profile", getParameter("profile"))
				.param("language", getParameter("languageUnknown"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("error.code", is(203))
				.statusCode(400);
	}

	@Test
	public void expect400204() {

		given()
				.param("coordinates", getParameter("coordinatesLong"))
				.param("preference", getParameter("preference"))
				.param("geometry", getParameter("geometry"))
				.param("profile", getParameter("profile"))
				.when()
				.get(getEndPointName())
				.then()
				.assertThat()
				.body("error.code", is(204))
				.statusCode(400);
	}

	@Test
	public void expectOptions() {

		given()
				.param("coordinates", getParameter("coordinatesShort"))
				.param("preference", getParameter("preference"))
				.param("geometry", getParameter("geometry"))
				.param("profile", getParameter("profile"))
				.param("options", getParameter("drivingOptions"))
				.when()
				.log()
				.all()
				.get(getServiceName())
				.then()
				.log()
				.all()
				.assertThat()
				.body("info.query.options.containsKey('avoid_features')", is(true))
				.statusCode(200);
	}

}
