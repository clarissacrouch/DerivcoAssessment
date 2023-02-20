package Assessment;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.module.jsv.JsonSchemaValidator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.response.Response;
import junit.framework.TestCase;

public class TestCocktailAPITest extends TestCase {

	@Test // 1. Test case: searching existing cocktail name – verify status code 200,
			// response not null.
	public void testSearchByNameValidSearch() {
		HelperClass searchByName = new HelperClass(); // showing the required reusable method.
		Response response = searchByName.searchCocktailName("Margarita");
		assertEquals(200, response.getStatusCode()); // * Validate response status code is 200
		assertNotNull(response); // * Validate response data is not null
	}

	@Test // 2. Test case: searching existing cocktail name - verify drink array is not
			// null.
	public void testSearchCocktailByName_ResponseNotNull() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchCocktailName("Margarita");

		assertNotNull(response.then().extract().path("drinks"));
		
	}

	@Test // 3. Test case: searching invalid cocktail name - verify drink array is null.
	public void testInvalidCocktailName_ReturnsNullDrinksArray() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchCocktailName("invalid");

		String responseString = response.getBody().asString();
		ObjectMapper objectMapper = new ObjectMapper();
		CocktailAPIRoot root = null;
		try {
			root = objectMapper.readValue(responseString, CocktailAPIRoot.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull("Response should not be null", response);

		List<CocktailAPIResponse> drinks = root.getDrinks();
		Assert.assertNull("Drinks should be null", drinks);
	}

	@Test // 4. Test case: searching valid cocktail - verify request data is case
			// sensitive
	public void testSearchCocktailByName_CaseSensitive() {
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchCocktailName("TiPperArY");

		try {
			// Verify that the response status code is 200 OK
			assertEquals(200, response.getStatusCode());

			String responseString = response.getBody().asString();
			ObjectMapper objectMapper = new ObjectMapper();
			CocktailAPIRoot root = objectMapper.readValue(responseString, CocktailAPIRoot.class);

			boolean found = false;

			for (CocktailAPIResponse drink : root.getDrinks()) {
				if (drink.getStrDrink().equals("TiPperArY")) {
					// Verify that the response data contains "TiPperArY" and is case sensitive
					assertEquals("TiPperArY", drink.getStrDrink());
					found = true;
					break;
				}
			}
			assertFalse("Drink TiPperArY not found, means is case sensitive", found);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test // 5. Test case: searching valid cocktail - Schema Validation:
	public void testSearchCocktailByName_ResponseSchema() {
		// Send request to search for cocktail by name
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchCocktailName("Margarita");

		// Validate response schema
		JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchemaInClasspath("cocktail-schema.json");
		response.then().assertThat().body(validator);
	}

	@Test // 6. Test case: searching valid cocktail - returns required fields:
	public void testSearchCocktailByName_RequiredFields() {
		// Send request to search for cocktail by name
		HelperClass searchByName = new HelperClass();
		Response response = searchByName.searchCocktailName("Margarita");

		// Validate response schema
		JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchemaInClasspath("cocktail-schema.json");
		response.then().assertThat().body(validator);

		// Verify that the response contains at least one cocktail
		response.then().body("drinks.size()", greaterThan(0));

		// Verify that each cocktail has the required fields
		response.then().body("drinks.strDrink", hasItem(notNullValue()));
		response.then().body("drinks.strTags", hasItem(notNullValue()));
		response.then().body("drinks.strCategory", hasItem(notNullValue()));
		response.then().body("drinks.strAlcoholic", hasItem(notNullValue()));
		response.then().body("drinks.strGlass", hasItem(notNullValue()));
		response.then().body("drinks.strInstructions", hasItem(notNullValue()));
		response.then().body("drinks.strIngredient1", hasItem(notNullValue()));
		response.then().body("drinks.strMeasure1", hasItem(notNullValue()));
		response.then().body("drinks.strCreativeCommonsConfirmed", hasItem(notNullValue()));
		response.then().body("drinks.dateModified", hasItem(notNullValue()));

	}

}
