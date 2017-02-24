#ifndef _MALLOC_H_   /* Include guard */
#define _MALLOC_H_


typedef enum { false,true } bool;
#define MIN 8  /* Minimum number of blocks allowed to split */
  

/*structure to manipulate the free and allocated blocks*/
typedef struct Memory {
	size_t size;
	void * start; //
	struct Memory * next;
	struct Memory * prev;
	bool state; // 1 - occupied , 0 - available 
}Mem_info;
int isHeadAvailable();
void addNode(Mem_info*);
Mem_info* create_node();
int round_of_power2(int);
void *allocate_from_os(size_t);
void splitup(Mem_info*, size_t);
Mem_info* check_available_space(size_t);
void * malloc(size_t);
void info_msg (char *);
Mem_info * getNode(void *);
void merge_free_nodes(Mem_info *);
void free(void *);
void * realloc(void*, size_t);
void * calloc(size_t, size_t);
void print_details(size_t, int, void *);
bool is_powerOf2(int);

#endif // FOO_H_