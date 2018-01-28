package com.simulator;

import java.util.Map.Entry;

public class TagClone {
	public PhysicalRegTable physicalTabCopy ;
	public RenameTable renameTabCopy;
	
	public void  display(){
		System.out.println("Physical table renameddd");
		for(Entry<String, PhysicalRegHandler> entry : this.physicalTabCopy.physicalOp.entrySet()){
			System.out.println(entry.getKey()+ " vaa ="+ entry.getValue());
		}
		
		System.out.println("rename table renameddd");
		for(Entry<String, String> entry : this.renameTabCopy.regMap.entrySet()){
			System.out.println(entry.getKey()+ " vaa ="+ entry.getValue());
		}
		
		for(Entry<String, Boolean> entry : this.renameTabCopy.regCommited.entrySet()){
			System.out.println(entry.getKey()+ " vaa ="+ entry.getValue());
		}
	}
}

