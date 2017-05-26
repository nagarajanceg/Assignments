#include "types.h"
#include "x86.h"
#include "defs.h"
#include "param.h"
#include "mmu.h"
#include "proc.h"
#include "date.h"
#include "cs550_syscall.h"
#define MAX 50


HT ** _hash_map_table;
void * current_offset;
int sys_date(void){
    struct rtcdate *st;
    if(argptr(0,(void  *) &st, sizeof(&st))<0)
            return -1;
    cmostime(st);
    return 0;
}
void _hash_data_table(){
	_hash_func("ajJ2rUS36Ds7gCvYhWvRMQ4cM");
	_hash_func("lcQQ6");
	_hash_func("tG0cd");
	_hash_func("BQaye");
	_hash_func("vRL61");
	_hash_func("BfPAS");
	_hash_func("VWqcU");
	_hash_func("rNV_x");
	_hash_func("m9TVP");
	_hash_func("dN3rB");
	_hash_func("NEorw");
	_hash_func("iE45d");
	_hash_func("VFGu_");
	_hash_func("zqf7e");
	_hash_func("Ayr5H");
	_hash_func("y7dVW");
	_hash_func("RhaR2");
	_hash_func("GUT_3");
	_hash_func("mtwGL");
	_hash_func("Rz1qA");
	_hash_func("BDslj");
	_hash_func("DYcHF");
	_hash_func("XVI4N");
	_hash_func("RknTi");
	_hash_func("eraAb");
	_hash_func("K7plu");
	_hash_func("UPjQy");
	_hash_func("uFjK1");
	_hash_func("E8Jj9");
	_hash_func("viJpE");
	_hash_func("y8hmu");
	_hash_func("GhPNY");
	_hash_func("mEymb");
	_hash_func("UAi7z");
	_hash_func("g3fke");
	_hash_func("spiXC");
	_hash_func("sCY6P");
	_hash_func("lQ5p2");
	_hash_func("yFLZV");
	_hash_func("y7NBZ");
	_hash_func("JS_81");
	_hash_func("pHGgA");
	_hash_func("D3BT2");
	_hash_func("LYSUb");
	_hash_func("UHDeS");
	_hash_func("zQQm5");
	_hash_func("T4IiC");
	_hash_func("ZnCtG");
	_hash_func("TBDYx");
	_hash_func("k_jNM");
}

void initialize(){
	//* allocating a page with memory block of 4096
	_hash_map_table = (HT**) kalloc();
	if(_hash_map_table == 0){
		cprintf("hash map table allocation problem ==\n");
		return;
	}
	memset(_hash_map_table, 0, 4096);
	current_offset = (void *) _hash_map_table + sizeof(HT *) * 100;
	_hash_data_table();
}
int
strcmp(const char *p, const char *q)
{
  while(*p && *p == *q){
    p++, q++;
  }
  return (uchar)*p - (uchar)*q;
}
int calculate_sum(char* value){
	int i,sum =0;
	for (i = 0; value[i] !='\0' ; ++i)
	{
		sum = sum + (value[i]- 0);
	}
	return sum;
}
void _hash_func(char* value){
	int key,sum;
	sum = calculate_sum(value);
	key = sum % MAX;
	put(value, key, sum);
}
int _hash_get(char* value){
	int key = calculate_sum(value) % MAX;
	return key;
}
void put(char* name, int key, int value){
 	HT * ht_ptr = _hash_map_table[key];
 	if( ht_ptr == 0){
 		ht_ptr = (HT*)current_offset;
 		ht_ptr -> name = name;
 		ht_ptr -> key = key;
 		ht_ptr -> sum = value;
 		ht_ptr->next = 0;
 		current_offset = (void * ) current_offset + sizeof(HT);
 		_hash_map_table[key] = ht_ptr;
 	}else{
 		HT * temp_ptr = ht_ptr;
 		while(ht_ptr->next != 0){
 			ht_ptr = ht_ptr->next;
 		}
 		temp_ptr = (HT*)current_offset;
 		temp_ptr -> name = name;
 		temp_ptr -> key = key;
 		temp_ptr -> sum = value;
 		temp_ptr -> next = 0;
 		current_offset = (void * ) current_offset + sizeof(HT);
 		ht_ptr->next = temp_ptr;
 	}
}

int get(char* name, int key){
	HT * ht_ptr = _hash_map_table[key];
	if(ht_ptr == 0){
		return -1;
	}
	while(ht_ptr != 0){
		if(strcmp(ht_ptr->name,name)== 0){
			return ht_ptr->sum;
		}
		ht_ptr = ht_ptr -> next;
	}
	return -1;
}

void 
syslinear(void)
{
int i;
char * arg1 = (char*)proc->tf->eax;
for(i=0; i< MAX;i++){
	if(strncmp(st[i].name,arg1,strlen(arg1)) == 0){
		proc->tf->eax = st[i].sum;
		return;
	}
}
if(i >= MAX ){
	cprintf("%d %s: unknown111 sys call\n",
            proc->pid, proc->name);
	proc->tf->eax = -1;
}
return;
}

void
syshash(void){
	// cprintf("in Syshash\n");
	char * arg1 = (char*)proc->tf->eax;
	// cprintf(arg1);
	int expectedSum = get(arg1, _hash_get(arg1));
	if(expectedSum > 0){
		// cprintf("string = %s sum found = %d\n",arg1, expectedSum);
		proc->tf->eax = expectedSum;
		return;		
	}
	proc->tf->eax = -1;
	return;
	// cprintf("hash val = %d",_hash_func(arg1));
}