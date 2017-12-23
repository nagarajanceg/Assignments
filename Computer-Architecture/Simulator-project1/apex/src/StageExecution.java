package apex;

import apex.helpers.ApexSimulatorHelper;

import java.util.List;
import java.util.Map;

public class StageExecution {
	public Map<String, InstructionStructure> pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();
	public static int pc = ApexSimulator.pc;
	protected static List<InstructionStructure> instructions = ParseInstructions.instructions;
	public static int max_size = 4000 + (instructions.size()*4);
	public void ExeInstructionInStages(){
		execWriteBack();
		execMemory();
		execMUL2();
		execMUL1();
		execIntFU();
		execDecode();
		execFetch();
	}
	
	private void execFetch() {
		if (pipelineStatus.get("FETCH") == null && ApexSimulatorHelper.INSTANCE.haltFlag == 0){
			if(pc < max_size){
				int addr = (pc-4000)/4; //fetch address calculation
				InstructionStructure InstrExec = instructions.get(addr);
				pipelineStatus.put("FETCH", InstrExec);
				InstrExec.setStage("FETCH");
				pc = pc+4;
			}
		}
	}


	private void execDecode() {
		if(pipelineStatus.get("DECODE") == null){
			InstructionStructure InstrExec = pipelineStatus.get("FETCH");
			if(InstrExec != null){
				pipelineStatus.put("FETCH", null); //Reset Fetch stagw
				InstrExec.setStage("DECODE");
				pipelineStatus.put("DECODE",InstrExec);
			}
		}
	
	}

	private void execIntFU() {
		if(pipelineStatus.get("IFU") == null){
			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			//Check the instruction going to execute is not null and not a multiply operation
			if(InstrExec != null && InstrExec.is_mul() != true){
				if(InstrExec.getStage() == "ready"){
					pipelineStatus.put("DECODE", null);
					InstrExec.setStage("IFU");
					pipelineStatus.put("IFU",InstrExec);
				}
			}
		}
		
	}

	private void execMUL1() {
		if(pipelineStatus.get("MUL1") == null){
			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			//Execute only for mul operation
			if(InstrExec != null && InstrExec.is_mul() == true){
				if(InstrExec.getStage() == "ready"){
					pipelineStatus.put("DECODE", null);
					InstrExec.setStage("MUL1");
					pipelineStatus.put("MUL1", InstrExec);
				}
			}
		}
		
	}

	private void execMUL2() {
		if(pipelineStatus.get("MUL2") == null){
			InstructionStructure InstrExec = pipelineStatus.get("MUL1");
			if(InstrExec != null){
				if(InstrExec.getStage() == "MUL1"){
					pipelineStatus.put("MUL1",null);
					InstrExec.setStage("MUL2");
					pipelineStatus.put("MUL2",InstrExec);
				}
			}
		}
	}

	private void execMemory() {
		if(pipelineStatus.get("MEM") == null){
			InstructionStructure InstrExec = null;
			// first check mul2 doesn't have any instruction because it arrives earlier in order
			if(pipelineStatus.get("MUL2") != null){
				InstrExec = pipelineStatus.get("MUL2");
				pipelineStatus.put("MUL2",null);
			}else if(pipelineStatus.get("IFU") != null){
				InstrExec = pipelineStatus.get("IFU");
				pipelineStatus.put("IFU",null);
			}
			if(InstrExec != null){
				pipelineStatus.put("MEM",InstrExec);
				InstrExec.setStage("MEM");
			}
		}
		
	}

	public void execWriteBack(){
		if(pipelineStatus.get("WB") != null){
			InstructionStructure InstrExec = pipelineStatus.get("WB");
			pipelineStatus.put("WB", null);
			InstrExec.setStage("completed");
		}
		
		if(pipelineStatus.get("WB") == null){
			InstructionStructure InstrExec = pipelineStatus.get("MEM");
			if(InstrExec != null){
				pipelineStatus.put("MEM", null);
				InstrExec.setStage("WB");
				pipelineStatus.put("WB",InstrExec);
			}
		}
	}
	//To check all the instructions getting executed
	public boolean allInstructionsExecuted(){
		if ( null==pipelineStatus.get("FETCH") && null ==pipelineStatus.get("DECODE") && null ==pipelineStatus.get("IFU")
				&& null == pipelineStatus.get("MUL1") && null == pipelineStatus.get("MUL2")
				&& null == pipelineStatus.get("WB")
				&& null == pipelineStatus.get("MEM")){
			return true;
		}
		return false;
	}
}
