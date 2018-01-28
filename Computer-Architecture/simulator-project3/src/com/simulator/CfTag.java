package com.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CfTag {
	int MAX_SIZE = 8;
	int size =0;
	
	 LinkedHashMap<Integer, InstructionStructure>tag;
	 Queue<Integer> order = new LinkedList<>();
//	 LinkedHashMap<Integer, PhysicalRegHandler> 
	 LinkedHashMap<Integer, TagClone> tagTable;
	 
	public int getSize() {
			return size;
	}
	public boolean isFull(){
		if(this.order.isEmpty()){
			return true;
		}
		return false;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public List<Integer> getDeleteIds(InstructionStructure inst){
			int id = inst.cfid;
			List<Integer> elements = new ArrayList<>();
			int index = 0;
		
			for(Map.Entry<Integer, InstructionStructure> entry: tag.entrySet()){
				if(entry.getKey() == id){
					index =1;
				}
				if(index ==1){
					elements.add(entry.getKey());
				}
			}
			for(Integer ids: elements){
				this.tag.remove(ids);
				this.tagTable.remove(ids);
				this.order.add(ids);
			}
			
			
		return elements;
	}
	public CfTag(){
		this.tag = new LinkedHashMap<Integer,InstructionStructure>();
		this.tagTable = new LinkedHashMap<>();
		this.initialize();
	}
	public void initialize(){
		for(int i=1; i<=MAX_SIZE; i++){
			order.add(i);
//			this.tag.put(i, null);
		}
	}
	public InstructionStructure getFront(){
		if(tag.get(0) != null){
			return tag.get(0);
		}
		return null;
	}
	public TagClone getTagClone(Integer pos){
		return this.tagTable.get(pos);
	}
	public Integer setTag( InstructionStructure inst, TagClone tagTab) {
		int pos = order.poll();
		if(pos>0){
			this.tag.put(pos, inst);
			this.tagTable.put(pos, tagTab);
		}
		return pos;
	}
	public void displayQ(){
		for(Integer s: this.order){
			System.out.println(s);
		}
	}
	public void removeTag(Integer id){
		tag.remove(id);
		order.add(id);
		System.out.println("display queue");
		displayQ();
	}
	public void moveInstruction(){
		for(int i=0; i< MAX_SIZE -1 ;i++){
			if(tag.get(i) == null){
				InstructionStructure next = tag.get(i+1);
				tag.put(i, next);
				tag.put(i+1, null);
			}
		}
		this.size = this.size -1;
	}
	
	public LinkedHashMap<Integer, InstructionStructure> getTag() {
		return tag;
	}
	public void setTag(LinkedHashMap<Integer, InstructionStructure> tag) {
		this.tag = tag;
	}
	
}
