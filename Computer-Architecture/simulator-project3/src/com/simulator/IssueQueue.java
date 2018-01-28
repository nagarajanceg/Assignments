package com.simulator;

import java.util.LinkedHashMap;
import java.util.List;

import com.simulator.InstructionStructure;

public class IssueQueue {
	LinkedHashMap<Integer, InstructionStructure> issueQ;
	LinkedHashMap<Integer, Boolean> validInstr;
	
	int size = 0 ;
	int MAX_SIZE = 16;
	
	public int getSize() {
		return size;
	}
	public boolean isFull(){
		if(size < MAX_SIZE){
			return false;
		}
		return true;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public IssueQueue() {
		this.issueQ = new LinkedHashMap<Integer, InstructionStructure>();
		this.validInstr = new LinkedHashMap<Integer, Boolean>(); // False - Invalid, True - Valid
		this.initialize();
	}
	private void initialize(){
//		int size = ApexSimulatorHelper.INSTANCE.ISSUEQ_SIZE;
		for(int i=0; i< MAX_SIZE; i++){
			this.issueQ.put(i, null);
			this.validInstr.put(i, false);
		}
	}
	
	public LinkedHashMap<Integer, InstructionStructure> getIssueQ() {
		return this.issueQ;
	}
	
	public void setIssueQ(int key, InstructionStructure inst) {
		this.issueQ.put(key, inst);
	}
	public void setValidInstr(int key, boolean status){
		this.validInstr.put(key, status);
	}
	public InstructionStructure getIQinstruction(String type){
		InstructionStructure inst = null;
//		boolean src1Valid, src2Valid;
		for(int i=0; i< MAX_SIZE; i++){
			inst = getInstruction(i);
//			this.validInstr.get(i) == true
			if(inst != null && inst.getFuncUnit().equals(type)){
				if((inst.isSrc1CanDispacted() == true && inst.isSrc2CanDispacted() == true)){
					//set null to the instructions dispatched
					issueQ.put(i, null);
					return inst;
				}
			}
		}
		return null;
	}
	public void changeInstr(List<Integer> cfids){
		InstructionStructure instExec = null;
		for(int i=0; i<MAX_SIZE; i++){
			instExec = getInstruction(i);
			for(Integer id : cfids ){
				if(instExec != null && (instExec.getCfid() == id)){
//					instExec.resetOriginal();
					issueQ.put(i, null);
					this.size = this.size -1;
				}
			}
		}
	}
	public InstructionStructure getInstruction(int key){
		InstructionStructure inst = this.issueQ.get(key);
		if(inst != null){
			return inst;
		}
		// No instructions available in issueQ return null 
		return null;
	}
	public void clearIQ(){
		for(int i=0; i< MAX_SIZE; i++){
			this.issueQ.put(i, null);
		}
		this.size = 0 ;
	}
	public void updateInstructions(InstructionStructure inst){
		InstructionStructure temp;
		for(int i=0; i < MAX_SIZE; i++ ){
			temp = this.issueQ.get(i);
			if(temp != null){
				String s1 = temp.getSrc1();
				String s2 = temp.getSrc2();
				if(s1 != null && s1.equals(inst.getDest())){
					temp.decodedSrc1 = inst.getResult();
					temp.setSrc1CanDispacted(true);
//					temp.setFwSrc1Status(true);
//					temp.setForwardSrc1(result);
				}
				
				if(s2 != null && s2.equals(inst.getDest())){
					temp.decodedSrc2 = inst.getResult();
					temp.setSrc2CanDispacted(true);
//					temp.setFwSrc2Status(true);
//					InstrDecode.setForwardSrc2(result);
				}
//				if((source1 == null || source1.isStatus() == false || inst.isFwSrc1Status() ) 
//						&& (source2 == null || source2.isStatus() == false || inst.isFwSrc2Status())){
//						}
			}
		}
	}
	public void moveInstruction(){
		for(int i=0; i< MAX_SIZE -1 ;i++){
			if(issueQ.get(i) == null){
				InstructionStructure next = issueQ.get(i+1);
				issueQ.put(i, next);
				issueQ.put(i+1, null);
			}
		}
		this.size = this.size -1;
	}
	public void displayIQ(){
		if(this.getSize() == 0){
			System.out.println("<IQ>: Empty");
			return;
        }
		System.out.println("<IQ>:");
		for( int i=0; i < MAX_SIZE; i++ ){
			InstructionStructure currentInst = this.getInstruction(i);
			if(currentInst != null){
				System.out.println("I"+ currentInst);
			}
		}
    }
}
