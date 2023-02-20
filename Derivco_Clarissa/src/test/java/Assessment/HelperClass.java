package Assessment;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;

public class HelperClass {

	private static final String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1";
	private static final String SEARCH_PATH = "/search.php";

	public Response searchIngredientName(String ingredient) {
		String url = BASE_URL + SEARCH_PATH + "?i=" + ingredient; // ?i= for ingredient
		Response response = given().when().get(url);
		response.prettyPrint();
		return response;
	}

	public Response searchCocktailName(String cocktail) {
		String url = BASE_URL + SEARCH_PATH + "?s=" + cocktail; // ?s= for cocktail
		Response response = given().when().get(url);
		response.prettyPrint();
		return response;
	}

}
