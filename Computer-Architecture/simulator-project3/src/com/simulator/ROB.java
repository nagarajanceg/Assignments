package com.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ROB {
	LinkedHashMap<Integer, InstructionStructure> rob;
	int MAX_SIZE = 32;
	int size = 0;
	ArrayList<InstructionStructure > commitInst = new ArrayList<>();
	boolean state = true; // true - capacity is available , false - no capacity
	
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public ROB(){
		this.rob = new LinkedHashMap<>();
		this.initialize();
	}
	public void initialize(){
		for(int i=0; i<MAX_SIZE; i++){
			this.rob.put(i, null);
		}
	}
	public void set(int key, InstructionStructure inst){
		rob.put(key, inst);
	}
	public InstructionStructure getInstructions(int val){
		InstructionStructure inst = this.rob.get(val);
		return inst;
	}
	public void removeInstr(InstructionStructure instr){
		for(int i=0; i<MAX_SIZE; i++){
			InstructionStructure inst = rob.get(i);
			if(inst.getInst_number() == instr.getInst_number()){
				rob.put(i, null);
				this.pushInstruction();
				return;
			}
		}
	}
	public void setSize(int sz){
		this.size = sz;
	}
	public int getSize(){
		return this.size;
	}
	public boolean isFull(){
		if(this.size == MAX_SIZE){
			return true;
		}
		return false;
	}
	public void pushInstruction(){
		for(int i=0; i<MAX_SIZE-1;i++){
			if(rob.get(i) == null){
				InstructionStructure ins = rob.get(i+1);
				rob.put(i,ins);
				rob.put(i+1, null);
			}
		}
		this.size = this.size -1;
	}
	public void ClearAfterBranch(InstructionStructure inst){
		int pos = -1;
		for(int i=0; i<MAX_SIZE; i++){
			InstructionStructure InstrExec = getInst(i);
			if(InstrExec != null && InstrExec.getInst_number()==inst.getInst_number()){
//				rob.put(i, null);
				pos = i;
			}
		}
		pos = pos +1;
		if(pos >0){
			for(; pos < MAX_SIZE; pos++){
				if(rob.get(pos) != null){
					InstructionStructure InstrExec = getInst(pos);
//					InstrExec.resetOriginal();
					rob.put(pos, null);
					this.size = size -1;
				}
			}
			
		}
	}
	public InstructionStructure getHead(){
		InstructionStructure head = rob.get(0);
		return head;
	}

	public ArrayList<InstructionStructure> getCommitInst() {
		return commitInst;
	}
	public void setCommitInst(InstructionStructure commitInst) {
		this.commitInst.add(commitInst);
	}
	public InstructionStructure getInst(int key){
		InstructionStructure ins = this.rob.get(key);
		if(ins != null){
			return ins;
		}
		return null;
	}
	 public void displayAll(){
		 if(this.getSize() == 0){
				System.out.println("<ROB>: Empty");
				return;
	        }
		 System.out.println("<ROB>: ");
		 for(int i=0; i < MAX_SIZE; i++){
			 InstructionStructure currentInstr = this.getInst(i);
			 if(currentInstr != null){
				 System.out.println( "I"+currentInstr);
			 }
		 }
	 }
	 public void swapBranchTable(){
		 
	 }
	 public void displayCommit(){
		 
		 if(this.commitInst.size() == 0 ){
			 System.out.println("<Commit>:Empty");
			 return;
		 }
		 System.out.println("<Commit>:");
		 for(InstructionStructure element: this.commitInst){
			 System.out.println("I"+element);
		 }
		 this.commitInst.clear();
	 }
	 
}
