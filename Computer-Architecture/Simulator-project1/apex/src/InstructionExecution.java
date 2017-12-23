package apex;

import java.util.Map;

import apex.helpers.ApexSimulatorHelper;

public class InstructionExecution {
	public Map<String, InstructionStructure> pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();

	public void ExecuteInstr(InstructionStructure instr){
		//Set the status of the executing instruction destination register
		if(instr.getDest() != null){
			RegisterHandler dest = ApexSimulatorHelper.INSTANCE.getRegister(instr.getDest());
			dest.setStatus(true);
		}
	}
	//Routine to execute at branching 
	public void BranchProcedure(InstructionStructure inst, int conditionValue){
		int address = inst.decodedSrc2;
		RegisterHandler PSW = ApexSimulatorHelper.INSTANCE.getRegister("PSW");
		if(PSW.getValue() == conditionValue){
			int inst_num = (inst.getInst_number() * 4) + 4000;
			StageExecution.pc = address + inst_num;
			System.out.println("PC Value after "+inst.getLogicOpSign()+" :" +StageExecution.pc);
			System.out.println("Branch taken. So flushing instructions in Fetch and Decode Stage");
			pipelineStatus.put("DECODE", null);
			pipelineStatus.put("FETCH", null);
			if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
				System.out.println("Newly Generated PC Value is out of range of instruction size. So exiting system....");
				ApexSimulatorHelper.INSTANCE.printArchState();
				System.exit(0);
			}
		}
	}
	//No operation
	public void Mul1Execution(InstructionStructure inst){
		return;
	}
	public void Mul2Execution(InstructionStructure inst){
		switch(inst.getLogicOpSign()){
			//Multiplication logic
			case "MUL":
				int result = inst.decodedSrc1 * inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			default:
				break;
		}
	}
	public void IntegerFUExecution(InstructionStructure inst){
		//match the operand to the corresponding operation
		switch(inst.getLogicOpSign()){
			case "ADD":{	
				int result = inst.decodedSrc1 + inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}	
			case "SUB":{
				int result = inst.decodedSrc1 - inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}
			case "MOVC":{
				int result = inst.decodedSrc1;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}
			case "LOAD":{
				break;
			}
			case "STORE":{
				break;
			}
			case "AND":{
				int result = inst.decodedSrc1 & inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}
			case "OR":{
				int result = inst.decodedSrc1 | inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}
			case "EXOR":{
				int result = inst.decodedSrc1 ^ inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				break;
			}
			case "HALT":{
				ApexSimulatorHelper.INSTANCE.haltFlag = 1;
				//clear fetch and decode stages
				pipelineStatus.put("FETCH", null);
				pipelineStatus.put("DECODE", null);
				System.out.println("Halt reached");
				break;
			}
			case "BZ":{
				//Brnach only if the psw flag is set
				BranchProcedure(inst,1);
				break;
			}
			case "BNZ":{
				BranchProcedure(inst,0);
				break;
			}
			case "JUMP":{
				StageExecution.pc = inst.decodedSrc1 + inst.decodedSrc2;				
				System.out.println("New Pc value : "+ StageExecution.pc);
				System.out.println("flush instructions in DEcode and fetch stage");
				pipelineStatus.put("DECODE", null);
				pipelineStatus.put("FETCH", null);
				if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
					System.out.println("New Pc value is out of range");
					ApexSimulatorHelper.INSTANCE.printArchState();
					System.exit(0);
				}
				break;
			}
			default:
				break;
		}
	}
}
