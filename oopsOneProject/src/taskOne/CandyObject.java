package taskOne;

public class CandyObject {

	public CandyObject() {
		System.out.println("No candies given");
	}
	
	public CandyObject(int noOfCandies) {
		System.out.println("No of candies to present:"+noOfCandies);
	}
	
	public CandyObject(int[] candySet, String[] winners) {
		System.out.println("An array of candies: first winner>>>"+winners[0]);
	}
}
