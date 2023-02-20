package Assessment;

import java.util.ArrayList;

public class CocktailAPIRoot {
	
	private ArrayList<CocktailAPIResponse> drinks;

	
	public ArrayList<CocktailAPIResponse> getDrinks() {
		return drinks;
	}

	public void setDrinks(ArrayList<CocktailAPIResponse> drinks) {
		this.drinks = drinks;
	}

}
