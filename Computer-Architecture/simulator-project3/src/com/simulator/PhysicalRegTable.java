package com.simulator;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PhysicalRegTable {
	LinkedHashMap<String, PhysicalRegHandler> physicalOp;
	PhysicalRegHandler physicalRegister;
	public PhysicalRegTable(){
		this.physicalOp = new LinkedHashMap<String, PhysicalRegHandler>();
		this.reset();
	}
	
	public void reset(){
		for(int i=0; i<32; i++){
			this.physicalOp.put("P"+i, new PhysicalRegHandler("P"+i, 0, true, false) );
		}
	}
	public void display(){
		for(String str: this.physicalOp.keySet()){
			System.out.println(this.physicalOp.get(str));
		}
	}
	
	public void set(String registerName, PhysicalRegHandler register){
		// Add register to the list
		physicalOp.put(registerName, register);
	}
	public PhysicalRegHandler get(String registerName){
		return physicalOp.get(registerName);
	}
	
//	public boolean isStatus(String registerName){
//		physicalRegister = physicalOp.get(registerName);
//		return physicalRegister.isStatus();
//	}
//	public void setStatus(String registerName){
//		physicalRegister = physicalOp.get(registerName);
//		physicalRegister.setStatus(true);
//	}
	public void setValue(String registerName, Integer value){
		physicalRegister = physicalOp.get(registerName);
		physicalRegister.setValue(value);
	}
	public Integer getValue(String registerName){
		physicalRegister = physicalOp.get(registerName);
		return physicalRegister.getValue();
	}
	
	public boolean isAvailable(String registerName){
		physicalRegister = physicalOp.get(registerName);
		return physicalRegister.isSlot();
	}
	public void setAvailable(String registerName, boolean available){
		physicalRegister = physicalOp.get(registerName);
		physicalRegister.setSlot(available);
	}
	public PhysicalRegHandler getFreeRegister(){
//		Iterator it = physicalOp.entrySet().iterator();
		for(PhysicalRegHandler element: physicalOp.values()){
			if(element.isSlot() == true){
				return element;
			}
		}
		return null;
		
	}
	public PhysicalRegTable  deepClone() {
		   try {
		    PhysicalRegTable phyTable = new PhysicalRegTable();
		    for(Entry<String, PhysicalRegHandler> entry : this.physicalOp.entrySet()){
//		    	System.out.println("Inside == > "+ entry.getKey() +" val =="+ PhysicalRegHandler.clone());
		    	phyTable.set(entry.getKey(),(PhysicalRegHandler) entry.getValue().clone());
		    }
		    return phyTable;
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
		 }
	
}
