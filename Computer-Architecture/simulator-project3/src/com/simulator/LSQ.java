package com.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.simulator.helpers.ApexSimulatorHelper;

public class LSQ {
	private ROB rob ;
	LinkedHashMap<Integer, InstructionStructure> lsq;
	int MAX_SIZE = 32;
	int size = 0;
	PhysicalRegTable phyTable;
//	boolean status;
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
	public LSQ(ROB rob, PhysicalRegTable phy){
		this.lsq = new LinkedHashMap<>();
		this.rob = rob;
		this.phyTable = phy;
		this.initialize();
	}
	private void initialize(){
//		int size = ApexSimulatorHelper.INSTANCE.ISSUEQ_SIZE;
		for(int i=0; i< MAX_SIZE; i++){
			this.lsq.put(i, null);
		}
	}
	public LinkedHashMap<Integer, InstructionStructure> getIssueQ() {
		return this.lsq;
	}
	public void updateLsq(InstructionStructure inst){
//		List<Integer> keyList = new ArrayList<Integer>(lsq.keySet());
		InstructionStructure InstrExec = null;
//		int idx = keyList.size();
		int pos = -1;
		boolean flag = false;
		for(int i = size-1; i>=0; i--){
			InstrExec = lsq.get(i);
			if(InstrExec.getInst_number() == inst.getInst_number()){
				flag = true;
			}
			if(flag == true){
				if(InstrExec.getLogicOpSign().equals("STORE") && InstrExec.getAddress() == inst.getAddress()){
					phyTable.setValue(inst.getDest(), InstrExec.decodedSrc1);
					rob.removeInstr(inst);
					rob.setCommitInst(inst);
					pos = i;
					break;
				}
			}
		}
		if(pos >=0){
			lsq.put(pos, null);
			pushInstruction();
		}
	}
	public void pushInstruction(){
		for(int i=0; i<MAX_SIZE-1;i++){
			if(lsq.get(i) == null){
				InstructionStructure ins = lsq.get(i+1);
				lsq.put(i,ins);
				lsq.put(i+1, null);
			}
		}
		this.size = this.size -1;
	}
	public InstructionStructure getLsqinstr(){
		InstructionStructure head = lsq.get(0);
		if(head != null && head.isLsqFlag() == true ){
			if(head.getLogicOpSign().equals("LOAD")){
				lsq.put(0, null);
				return head;
			}else{
				if(rob != null && rob.getHead().getInst_number() == head.getInst_number()){
					String dest = head.getSrc1();
					if(dest.contains("R")){
						RegisterHandler destReg = ApexSimulatorHelper.INSTANCE.archTable.get(dest);
						head.setResult(destReg.getValue());
					}else{
						PhysicalRegHandler destReg = ApexSimulatorHelper.INSTANCE.physicalTable.get(dest);
						head.setResult(destReg.getValue());
					}
					lsq.put(0, null);
					return head;
				}
			}
		}
		return null;
	}
	public void set(int key, InstructionStructure inst) {
		this.lsq.put(key, inst);
	}
	public void clearLSQ(){
		for(int i=0; i< MAX_SIZE; i++){
			this.lsq.put(i, null);
		}
		this.size = 0 ;
	}
	public void updateAddress(InstructionStructure inst){
//		for
	}
	public InstructionStructure getInstruction(int key){
		InstructionStructure inst = this.lsq.get(key);
		if(inst != null){
			return inst;
		}
		// No instructions available in issueQ return null 
		return null;
	}
	public void moveInstruction(){
		for(int i=0; i< MAX_SIZE -1 ;i++){
			if(lsq.get(i) == null){
				InstructionStructure next = lsq.get(i+1);
				lsq.put(i, next);
				lsq.put(i+1, null);
			}
		}
		this.size = this.size -1;
	}
	public void display(){
		if(this.getSize() == 0){
			System.out.println("<LSQ>: Empty");
			return;
        }
		System.out.println("<LSQ>: ");
		for( int i=0; i < MAX_SIZE; i++ ){
			InstructionStructure currentInst = this.getInstruction(i);
			if(currentInst != null){
				System.out.println("I"+currentInst);
			}
		}
    }
}
