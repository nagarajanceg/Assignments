package com.simulator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.RepaintManager;

import java.util.Map.Entry;

public class RenameTable {
	public int size;
	public HashMap<String, String> regMap;
	public HashMap<String, Boolean> regCommited;
	public String z;
	
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	public RenameTable(){
		this.regMap = new HashMap<>();
		this.regCommited = new HashMap<>();
	}
	public void clearUnused(){
		this.regMap.values().removeAll(Collections.singleton(null));
		this.regCommited.values().removeAll(Collections.singleton(null));
	}
	public HashMap<String, String> getRegMap() {
		return regMap;
	}

	public void setRegMap(HashMap<String, String> regMap) {
		this.regMap = regMap;
	}

	public HashMap<String, Boolean> getRegCommited() {
		return regCommited;
	}

	public void setRegCommited(HashMap<String, Boolean> regCommited) {
		this.regCommited = regCommited;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	public void display(){
		System.out.println("<RENAME TABLE> ");
		for(Map.Entry<String, String> rnTable: regMap.entrySet()){
//			String val = rnTable.getValue();
//			if(val != null){
				System.out.println(rnTable.getKey()+ " : "+ rnTable.getValue());
//			}
		}
//		System.out.println("Commited  ");
//		for(Map.Entry<String, Boolean> rnTable: regCommited.entrySet()){
////			String val = rnTable.getValue();
////			if(val != null){
//				System.out.println(rnTable.getKey()+ " : "+ rnTable.getValue());
////			}
//		}
	}
//	public void display(){
//		
//	}
	public RenameTable  deepClone() {
		   try {
			   RenameTable renameTable = new RenameTable();
			   for(Entry<String, String> entry : this.regMap.entrySet()){
				   renameTable.regMap.put(entry.getKey(), entry.getValue());
			   }
			   for(Entry<String, Boolean> entry: this.regCommited.entrySet()){
				   renameTable.regCommited.put(entry.getKey(), entry.getValue());
			   }

		    return renameTable;
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
		 }
	
}
