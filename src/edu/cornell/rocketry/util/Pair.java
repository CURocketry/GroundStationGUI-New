package edu.cornell.rocketry.util;

/** a class representing a two-tuple */
public class Pair<L,R> {
	private L left;
	private R right;

	public Pair (L l, R r) {
		left = l;
		right = r;
	}

	public L left () {
		return left;
	}

	public R right () {
		return right;
	}
	
	public void setleft(L newleft){
		left = newleft;
	}
	
	public void setright(R newright){
		right = newright;
	}
	
	@Override
	public String toString(){
		return "Pair (" + left.toString() + ", " + right.toString() + ")";
	}

	public static void main (String[] args) {
		Pair<Integer, String> example = new Pair<Integer, String>(1, "I'm in a Pair with the Integer 1!");
		System.out.println(example);
	}	
}