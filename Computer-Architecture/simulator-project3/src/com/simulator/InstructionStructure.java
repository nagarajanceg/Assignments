package com.simulator;

import com.simulator.PhysicalRegHandler;
import com.simulator.helpers.ApexSimulatorHelper;

public class InstructionStructure implements Cloneable{
	private String src1;
	private String src2;
	private String dest;
	private String Stage;
	private int result;
	private int literal;
	private int inst_number;
	private String logicOpSign;
	private boolean currentValue;
	private String currentExecInst;
	public int decodedSrc1 = 0;
	public int decodedSrc2 = 0;
	public int decodedDest = 0;
	public int forwardSrc1 = 0;
	public int forwardSrc2 = 0;
	public boolean fwSrc1Status = false;
	public boolean fwSrc2Status = false;
	public boolean fwpswFlag = false;
	public boolean lsqFlag = false;
	public int PswFlagValue = -1;
	public boolean src1CanDispacted =false;
	public boolean src2CanDispacted =false;
	public String original; // contains original arch reg dest name
	public String src1Original;
	public String src2Original;
	public int address = 0 ;
	public int cfid=-1;
	
	public void resetInst(){
		this.dest = null;
		this.result = 0;
		this.literal = 0;
		this.currentValue = false;
		this.decodedSrc1 = 0;
		this.decodedSrc2 =0;
		this.decodedDest =0;
		this.forwardSrc1 =0;
		this.forwardSrc2 = 0;
		this.fwSrc1Status =false;
		this.fwSrc2Status = false;
		this.fwpswFlag = false;
		this.lsqFlag = false;
		this.PswFlagValue = -1;
		this.src1CanDispacted =false;
	}
	public String getSrc1Original() {
		return src1Original;
	}
	
	public void resetOriginal(){
		this.src1 = this.src1Original;
		this.src2 = this.src2Original;
		this.dest = this.original;
		this.setInstValues();
	}
	
	public void setSrc1Original(String src1Original) {
		this.src1Original = src1Original;
	}

	public String getSrc2Original() {
		return src2Original;
	}

	public void setSrc2Original(String src2Original) {
		this.src2Original = src2Original;
	}
	public int getCfid() {
		return cfid;
	}

	public void setCfid(int cfid) {
		this.cfid = cfid;
	}

	public boolean isLsqFlag() {
		return lsqFlag;
	}

	public void setLsqFlag(boolean lsqFlag) {
		this.lsqFlag = lsqFlag;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}
	
	
	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public boolean isSrc1CanDispacted() {
		return src1CanDispacted;
	}

	public void setSrc1CanDispacted(boolean src1CanDispacted) {
		this.src1CanDispacted = src1CanDispacted;
	}

	public boolean isSrc2CanDispacted() {
		return src2CanDispacted;
	}

	public void setSrc2CanDispacted(boolean src2CanDispacted) {
		this.src2CanDispacted = src2CanDispacted;
	}

	public int getPswFlagValue() {
		return PswFlagValue;
	}

	public void setPswFlagValue(int pswFlagValue) {
		PswFlagValue = pswFlagValue;
	}

	public boolean isFwpswFlag() {
		return fwpswFlag;
	}

	public void setFwpswFlag(boolean fwpswFlag) {
		this.fwpswFlag = fwpswFlag;
	}

	public int getForwardSrc1() {
		return forwardSrc1;
	}

	public void setForwardSrc1(int forwardSrc1) {
		this.forwardSrc1 = forwardSrc1;
	}

	public int getForwardSrc2() {
		return forwardSrc2;
	}

	public void setForwardSrc2(int forwardSrc2) {
		this.forwardSrc2 = forwardSrc2;
	}
	
	public boolean isFwSrc1Status() {
		return fwSrc1Status;
	}

	public void setFwSrc1Status(boolean fwSrc1Status) {
		this.fwSrc1Status = fwSrc1Status;
	}

	public boolean isFwSrc2Status() {
		return fwSrc2Status;
	}

	public void setFwSrc2Status(boolean fwSrc2Status) {
		this.fwSrc2Status = fwSrc2Status;
	}

	public InstructionStructure(){
		this.currentExecInst = "";
	}

	public String getSrc1() {
		return src1;
	}
	public void setcurrentInstr(String identity){
		String[] currentInst = this.currentExecInst.split(" ");
		if(identity.equals("src1")){
			if(this.logicOpSign.equals("JUMP")){
				currentInst[2] = this.src1;
			}else{
				currentInst[3] = this.src1;
			}
		}else if (identity.equals("src2")){
			currentInst[4] = this.src2; 
		}else if (identity.equals("dest")){
			currentInst[2] = this.dest;
		}
//		List<String> list = Arrays.asList(array);
		this.currentExecInst = String.join(" ", currentInst);
	}
	public void setInstValues(){
		if(this.src1 != null && (!logicOpSign.equals("BNZ") && !logicOpSign.equals("BZ"))){
			setcurrentInstr("src1");
		}
		if(this.src2 != null && (logicOpSign.equals("JUMP") == false)){
			setcurrentInstr("src2");
		}
		if( this.dest != null){
			setcurrentInstr("dest");
		}
	}
	public void setNewSrc1(String src1) {
		this.src1 = src1;
	}
	public void setSrc1(String src1) {
		this.src1 = src1;
		//set src to the current execution if it's not branching instructions
		if(!logicOpSign.equals("BNZ") && !logicOpSign.equals("BZ")){
			this.currentExecInst += " "+this.src1; 
		}
	}
	public boolean isBranching(){
		return logicOpSign.equals("BNZ") || logicOpSign.equals("BZ") || logicOpSign.equals("JUMP") || logicOpSign.equals("JAL");
	}
	public String getSrc2() {
		return src2;
	}
	public void setNewSrc2(String src2){
		this.src2 = src2;
	}
	public void setSrc2(String src2) {
		this.src2 = src2;
		this.currentExecInst += " "+ this.src2;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
		this.currentExecInst += " " + this.dest; 
	}
	
	public void setNewDest(String dest) {
		this.dest = dest;
	}

	public String getStage() {
		return Stage;
	}

	public void setStage(String stage) {
		Stage = stage;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getLiteral() {
		return literal;
	}

	public void setLiteral(int literal) {
		this.literal = literal;
		this.currentExecInst += " " + this.literal; 
	}

	public int getInst_number() {
		return inst_number;
	}
	public void setInst_number(int inst_number) {
		this.inst_number = inst_number;
		this.currentExecInst +=""+ this.inst_number+""+":";
	}
	public boolean is_lsq(){
		return logicOpSign.equals("LOAD") || logicOpSign.equals("STORE");
	}
	public boolean is_mul(){
		return logicOpSign.equals("MUL");
	}
	public boolean is_div(){
		return logicOpSign.equals("DIV");
	}
	public boolean is_halt(){
		return logicOpSign.equals("HALT");
	}
	public String getLogicOpSign() {
		return logicOpSign;
	}
	public boolean is_arthimetic(){
		return logicOpSign.equals("ADD")|| logicOpSign.equals("MUL")||logicOpSign.equals("SUB")||logicOpSign.equals("DIV");
	}
	public boolean is_int_unit(){
//		return logicOpSign.equals("MOVC") || logicOpSign.equals("ADD")|| logicOpSign.equals("SUB") 
//				|| logicOpSign.equals("BNZ")|| logicOpSign.equals("BZ") || logicOpSign.equals("JUMP") 
//				||logicOpSign.equals("JAL") || logicOpSign.equals("AND") || logicOpSign.equals("OR") ||logicOpSign.equals("EXOR");
		return (is_mul() == false && is_div() == false && is_halt() == false );
	}
	public String getFuncUnit(){
		if(is_mul()){
			return "MUL";
		}
		else if(is_int_unit()){
			return "IFU";
		}else if(is_div()){
			return "DIV";
		}
		return null;
	}
	public void setLogicOpSign(String logicOpSign) {
		this.logicOpSign = logicOpSign;
		this.currentExecInst +=" " + this.logicOpSign;
	}

	public boolean isCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(boolean currentValue) {
		this.currentValue = currentValue;
	}
	public void decode_source1(){
		
		if(ApexSimulatorHelper.INSTANCE.physicalTable.get(this.src1) != null){
			PhysicalRegHandler source = ApexSimulatorHelper.INSTANCE.physicalTable.get(this.src1);
			if(source.isBusy() == false){
				this.setSrc1CanDispacted(true);
			}
			this.decodedSrc1 = source.getValue();
		}else{
			if(ApexSimulatorHelper.INSTANCE.archTable.get(this.src1) != null){
				RegisterHandler source = ApexSimulatorHelper.INSTANCE.archTable.get(this.src1);
				this.decodedSrc1 = source.getValue();
				this.setSrc1CanDispacted(true);
			}
		}
	}
	public void decode_source2(){
		
		if(ApexSimulatorHelper.INSTANCE.physicalTable.get(this.src2) != null){
			PhysicalRegHandler source = ApexSimulatorHelper.INSTANCE.physicalTable.get(this.src2);
			if(source.isBusy() == false){
				this.setSrc2CanDispacted(true);
			}
			this.decodedSrc2 = source.getValue();
		}else{
			if(ApexSimulatorHelper.INSTANCE.archTable.get(this.src2) != null){
				RegisterHandler source = ApexSimulatorHelper.INSTANCE.archTable.get(this.src2);
				this.setSrc2CanDispacted(true);
				this.decodedSrc2 = source.getValue();
			}
		}
	}
	public void decode_dest(){
		if(ApexSimulatorHelper.INSTANCE.physicalTable.get(this.dest) != null){
			PhysicalRegHandler source = ApexSimulatorHelper.INSTANCE.physicalTable.get(this.dest);
			this.decodedDest = source.getValue();
		}
	}
	@Override
	public String toString(){
		return this.currentExecInst;
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
