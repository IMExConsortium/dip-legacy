package edu.ucla.mbi.dip;

import java.util.*;

 
public class EmptyList 
    extends AbstractList{
    
    public int size(){ 
	return 0;
    }

    public Object get(int i)
	throws IndexOutOfBoundsException{
	
	throw new IndexOutOfBoundsException();
    }

}



