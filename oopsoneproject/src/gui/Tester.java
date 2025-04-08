

package gui;

public class Tester {
	
	float q = 0.9085f;
	
	public Tester(int q){
		
		System.out.println("Tester::Constructor> q="+this.q);
		
		
		
	}
	
	public static void main(String[] e) {
		Tester test = new Tester(2);
	}
	
}

