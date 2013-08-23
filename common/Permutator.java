package fiu.kdrg.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Permutator {


    /**
     * stands for Steinhaus-Johnson-Trotter Algorithm, 
     * wihtout thinking about least change between adjacent permutation
     * which needs compute its parity
     * @param n
     * @return
     */
    static public List<LinkedList<Integer>> recursiveSJT(int n){
	
	if( n == 1){
	    LinkedList<Integer> one = new LinkedList<Integer>();
	    one.add(1);
	    List<LinkedList<Integer>> basePerms = new ArrayList<LinkedList<Integer>>();
	    basePerms.add(one);
	    return basePerms;
	}
	
	List<LinkedList<Integer>> basePerms = recursiveSJT(n-1);
	List<LinkedList<Integer>> perms = new ArrayList<LinkedList<Integer>>();
	for(List<Integer> perm : basePerms){
	    for(int i = 0; i <= perm.size(); i++){
		LinkedList<Integer> tmp = new LinkedList<Integer>(perm);
		tmp.add(i, n);
		perms.add(tmp);
	    }
	}
	
	return perms;
    }
    
    
    
    public static void main(String[] args) {
	
	List<LinkedList<Integer>> perms = recursiveSJT(5);
	System.out.println(perms.size());
	for(LinkedList<Integer> perm : perms){
	    for(Integer ele : perm){
		System.out.print(ele + "\t");
	    }
	    System.out.println();
	}
	
    }
}
