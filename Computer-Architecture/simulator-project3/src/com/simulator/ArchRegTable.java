package com.simulator;

import java.util.HashMap;

public class ArchRegTable {
	HashMap<String, RegisterHandler> registerOp;
	RegisterHandler archRegister;
	public ArchRegTable(){
		this.registerOp = new HashMap<>();
		this.reset();
	}
	private void reset() {
		for(int i=0; i<16; i++){
			this.registerOp.put("R"+i, new RegisterHandler("R"+i, 0, false) );
		}
		this.registerOp.put("PSW", new RegisterHandler("PSW", 0, false));
	}
	
	public void set(String registerName, RegisterHandler register){
		// Add register to the list
		registerOp.put(registerName, register);
	}
	public RegisterHandler get(String registerName){
		return registerOp.get(registerName);
	}
	public void setValue(String registerName, Integer value){
		archRegister = registerOp.get(registerName);
		archRegister.setValue(value);
	}
	public Integer getValue(String registerName){
		archRegister = registerOp.get(registerName);
		return archRegister.getValue();
	}
	public boolean isStatus(String registerName){
		archRegister = registerOp.get(registerName);
		return archRegister.isStatus();
	}
	public void setStatus(String registerName){
		archRegister = registerOp.get(registerName);
		archRegister.setStatus(true);
	}
}
