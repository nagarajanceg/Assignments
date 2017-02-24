#include <sys/types.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <unistd.h>
#include <limits.h>
#include <stdlib.h>
#include <stdint.h>
#include "malloc.h"
Mem_info *head = NULL;
Mem_info * tail = NULL;

/* --------best fit algorithm ----------*/
size_t BLOCK_DIFF = INT_MAX;
Mem_info * best_block = NULL;

int isHeadAvailable(){
	if(head == NULL){
		return 0;
	}
	return 1;
}
int round_of_power2(int x){
	x--;
	x |= x >> 1;
	x |= x >> 2;
	x |= x >> 4;
	x |= x >> 8;
	x |= x >> 16;
	x++;
	return x;
}
bool is_powerOf2(int num){
	if(num > MIN && !(num & (num-1))){
		return true;
	}else{
		return false;
	}
}
void info_msg (char * msg_content){
	char msg_buf[100];
	sprintf(msg_buf,"%s",msg_content);
	write(2, msg_buf, strlen(msg_buf));
}
/*Insert nodes in a list*/
void addNode(Mem_info* node ){
	if(!isHeadAvailable()){
		head = node;
		tail = head;
	}else{
		node -> prev = tail;
		tail -> next = node;
		tail = tail -> next;
	}
}
/* intialize the node with default values */
Mem_info * create_node(){
	Mem_info *element = sbrk(round_of_power2(sizeof(Mem_info)));
	if(element == (void*)-1){
		// info_msg("error:=> Memory unavailable at sbrk");
	}
	element -> size = 0;
	element -> state = 0;
	element -> start = NULL;
	element -> next = NULL;
	element -> prev = NULL;
	return element;
}
/* traverse to the node and return the node*/
Mem_info * getNode(void * ptr){
	Mem_info * current = head;
	while(current != NULL){
		if(current -> start == ptr){
			return current;
		}
		current = current -> next;
	}
	return NULL;
}

/*input will be power of 2 */
void splitup(Mem_info* current, size_t size){
	size_t temp = current -> size;
	while(temp > 0){
		if(temp == size){
			current->state = 1;
			if(current->next == NULL){
				tail = current;
			}
			best_block = current;
			break;
		}
		temp = current->size / 2;
		current -> size = temp;
		Mem_info* new_node = create_node();
		new_node -> size = temp;
		new_node -> next = current -> next;
		new_node -> prev = current;
		new_node -> start = current -> start + temp;
		current -> next = new_node;
		current = current -> next;
	}
}

/*Merge the adjacent free nodes*/
void merge_free_nodes(Mem_info * node){
	size_t cumulative_sum;
	Mem_info * temp;
	/* Merge the previous and current node with a total block size is of power of2 */
	if(node -> prev && node -> prev -> state == 0){
		cumulative_sum = node -> size + node -> prev -> size;
		int ptDiff = node - node->prev;
		if(is_powerOf2(cumulative_sum)){
			temp = node;
			node = node -> prev;
			node -> size = cumulative_sum;
			node -> next = temp -> next;
			if(temp -> next){
				temp -> next -> prev = node;
			}else{
				tail = node;
			}
		}
	}
	/* Merge the next and current node with a total block size is of power of2 */
	if(node -> next && node ->next ->state == 0){
		cumulative_sum = node -> size + node -> next -> size;
		if(is_powerOf2(cumulative_sum)){
			node -> size = cumulative_sum;
			if(node -> next -> next){
				node -> next = node -> next -> next;
				node -> next -> prev = node;
			}else{
				node -> next = NULL;
				tail = node;
			}
		}
	}
}
/* getting the memory from OS by Sbrk system call */
void *allocate_from_os(size_t sz){
	Mem_info *  node ;
	void * address;
	/* assigned size in terms of powers of 2 */
	node = create_node();
	size_t aligned_size =  sz < MIN ? MIN : round_of_power2(sz);
	address = sbrk(aligned_size); // assign size in bytes

	if(address == (void*)-1){
	 	// info_msg("error :=> Memory unavailable");
	 	return NULL;
	}
	node -> size = aligned_size;
	node -> state = 1;
	node -> start = address;
	node -> next = NULL;
	addNode(node);
	return address;
}
/* check the requested space available in maintained free blocks */
Mem_info* check_available_space(size_t sz){
	if(!is_powerOf2(sz)){
		sz = sz < MIN ? MIN : round_of_power2(sz);
	}
	
	Mem_info * iterate = head;
	while(iterate != NULL){
		// state 0 represent the free status
		if(iterate -> state == 0){
			size_t block_size = iterate -> size;
			if(sz == block_size){
				return iterate;
			}
			if(sz < block_size){
				size_t current_diff = block_size - sz;
				if(current_diff < BLOCK_DIFF){
					BLOCK_DIFF = current_diff;
				 	best_block = iterate;
				}
			}
		}
		iterate = iterate -> next;
	}
	if(best_block){
		if(is_powerOf2(best_block -> size)){
			splitup(best_block, sz);	
		}else{
			return NULL;
		}
	}
	return best_block;
}

void * malloc(size_t size){
	if( size <= 0){
		return NULL;
	}
	Mem_info * node = NULL;
	void *address = NULL;

	// For the very first time, head is ponting to NULL and the requested size memory is obtained from OS.
	if(!isHeadAvailable()){
		address = allocate_from_os(size);
	}else{
		/* check the available free blocks*/
		node = check_available_space(size);
		if(!node){
			address = allocate_from_os(size);
		}else{
			/* the values resetted to serve best fit method for the further requests */
			best_block = NULL;
			BLOCK_DIFF = INT_MAX;
			node -> state = 1;
			address = node ->start;
		}
	}
	return address;
}
void free(void *ptr){
	// char msg_buf[100];
	if(ptr == NULL){
		return ;
	}
	Mem_info * node = getNode(ptr);
	/*Node is available and the state 1 to show it's already occupied*/
	if(node){
		if(node -> state == 0){
			// info_msg("error_msg :=> Required pointer unavailable or already freed\n");
		}else{
			node -> state = 0;
			// sprintf(msg_buf, ">>> Freed %zu bytes at %p.\n", node->size, ptr);
    		// write(2, msg_buf, strlen(msg_buf));
			merge_free_nodes(node);	
		}
	}else{
		info_msg("error_msg :=> No pointer exist\n");
	}

}
void * realloc(void* ptr, size_t size){
	size = round_of_power2(size);
	if(ptr == NULL){
		// info_msg("desc :=> pointer is null, so creating new node during reallocation\n");
		return malloc(size);
	}else{
		Mem_info * node = getNode(ptr);
		if(node && (node -> size >= size)){
			// info_msg("desc:=> return the same size in memory");
			return node -> start;
		}else{
			void * newPtr = malloc(size);
			if(node){
				memcpy(newPtr, ptr, node -> size);
			}
			free(ptr);
			return newPtr;
		}
	}
	
}
void * calloc(size_t members, size_t size){
	size_t sz = members * size;
	void *nodes = malloc(sz);
	memset(nodes, 0 , sz);
	return nodes; 
}