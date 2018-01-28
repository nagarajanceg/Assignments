package com.simulator;

public class PhysicalRegHandler implements Cloneable {
	private String name;
	private int value;
//	private boolean status; //working status of register
	private boolean slot; // true - register free , false- register slot unavailable
	private boolean busy;//true- renamed, value can be forwarded , false-no more forwarded
//	private List<>	
	public PhysicalRegHandler(String name, int value,  boolean slot, boolean valid) {
		this.name = name;
		this.value = value;
//		this.status = status;
		this.slot = slot;
		this.busy = valid;
	}
	public void reset(){
		this.value = 0;
//		this.status = false;
		this.slot = true;
		this.busy = false;
	}
	public boolean isSlot() {
		return slot;
	}

	public void setSlot(boolean slot) {
		this.slot = slot;
	}
	
	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
//	public boolean isStatus() {
//		return status;
//	}
//	public void setStatus(boolean status) {
//		this.status = status;
//	}
	@Override
	public String toString() {
		return "RegisterHandler{" +
				"name='" + name + '\'' +
				", value=" + value +
//				", status=" + status +
				", slot=" + slot+
				", busy="+busy+
				'}';
	}
	
	@Override
    public Object clone() {
		  //shallow copy
		  try {
			  return super.clone();
		
		  } catch (CloneNotSupportedException e) {
		
		  
			  return null;
		  }
	}
}
