package Assessment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class TestIngredientsAPITest {

	@Test // 1. Test Case Name: Test that a valid search returns a success status
			// code(200):
	public void testSearchByName_ValidSearch() {
		HelperClass searchByName = new HelperClass(); // showing the required reusable method.
		Response response = searchByName.searchIngredientName("Vodka");
		// * Validate response status code is 200
		assertEquals(200, response.getStatusCode());
		// * Validate response data is not null
		assertNotNull(response);
	}

	@Test // 2. Test case: searching valid ingredient - Schema Validation:
	public void testSearchCocktailByName_ResponseSchema() {
		// Send request to search for cocktail by name
		HelperClass searchByName = new HelperClass(); // showing the required reusable method.
		Response response = searchByName.searchIngredientName("Vodka");

		// Validate response schema
		JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchemaInClasspath("ingredients-schema.json");
		response.then().assertThat().body(validator);
	}
	
	
	@Test // 3. Test case: searching valid ingredient - returns required fields:
	public void testSearchCocktailByName_RequiredFields() {
		// Send request to search for cocktail by name
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchIngredientName("Vodka");
		
		// Validate response schema
		JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchemaInClasspath("ingredients-schema.json");
		response.then().assertThat().body(validator);

		// Verify that the response contains at least one cocktail
		response.then().body("ingredients.size()", greaterThan(0));

		// Verify that each cocktail has the required fields
		response.then().body("ingredients.idIngredient", hasItem(notNullValue()));
		response.then().body("ingredients.strIngredient", hasItem(notNullValue()));
		response.then().body("ingredients.strDescription", hasItem(notNullValue()));
		response.then().body("ingredients.strType", hasItem(notNullValue()));
		response.then().body("ingredients.strAlcohol", hasItem(notNullValue()));
		response.then().body("ingredients.strABV", hasItem(notNullValue()));
	}
	

	@Test // 4. Test that an API invalid search - returns a null (empty) ingredient array. 
	public void testSearchByName_InvalidSearch() {
		// Send request to search for cocktail by name
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchIngredientName("Invalid");
		assertEquals(200, response.getStatusCode()); // * Validate response status code is 200
		assertNotNull(response); // * Validate response data is not null

		Integer arrayActualSize = response.then().extract().path("ingredients");
		assertEquals("Array size for invalid search should be null", null, arrayActualSize);
	}

	@Test // 5. Alcoholic ingredient: strABV field cannot be null.
	public void testSearchAlcoholicIngredient() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchIngredientName("Vodka");

		String actualAlcohol = response.then().extract().path("ingredients[0].strAlcohol");
		String actualABV = response.then().extract().path("ingredients[0].strABV");

		if (actualAlcohol.equals("Yes")) {
			assertNotNull(actualABV);
		} else if (actualAlcohol.equals("No")) {
			assertEquals(null, actualABV);
		}
	}

	@Test // 6. Non-Alcoholic ingredient: strABV field must be null. WATER*
	public void testSearchNonAlcoholicIngredient() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchIngredientName("water");
		
		assertEquals(200, response.getStatusCode()); // * Validate response status code is 200
		assertNotNull(response); // * Validate response data is not null

		String actualAlcohol = response.then().extract().path("ingredients[0].strAlcohol");
		String actualABV = response.then().extract().path("ingredients[0].strABV");

		if (actualAlcohol.equals("Yes")) {
			assertNotNull(actualABV);
		} else if (actualAlcohol.equals("No")) {
			assertEquals(null, actualABV);
		}
	}

	@Test // 7. Test Case Name: Search for an ingredient with mixed case ‘vOdKa’
	public void testSearchNoAlcoholicIngredient() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchIngredientName("vOdKa");
		
		assertEquals(200, response.getStatusCode()); // * Validate response status code is 200
		assertNotNull(response); // * Validate response data is not null

		String expectedId = "1";
		String expectedName = "Vodka";
		String expectedType = "Vodka";
		String expectedAlcohol = "Yes";
		String expectedABV = "40";
		// Retrieve fields with Path:
		String actualId = response.then().extract().path("ingredients[0].idIngredient");
		String actualName = response.then().extract().path("ingredients[0].strIngredient");
		String actualType = response.then().extract().path("ingredients[0].strType");
		String actualAlcohol = response.then().extract().path("ingredients[0].strAlcohol");
		String actualABV = response.then().extract().path("ingredients[0].strABV");
		assertEquals("Ingredient ID is not as expected", expectedId, actualId);
		assertEquals("Ingredient name is not as expected", expectedName, actualName);
		assertEquals("Ingredient type is not as expected", expectedType, actualType);
		assertEquals("Ingredient alcohol flag is not as expected", expectedAlcohol, actualAlcohol);
		assertEquals("Ingredient ABV is not as expected", expectedABV, actualABV);
	}

}
