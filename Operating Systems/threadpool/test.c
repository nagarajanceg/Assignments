#include <stdio.h>
#include <pthread.h>
#include <assert.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include "threadpool.h"

#define SIZE 100
void* sampleTask(void *arg){
	long seconds = (long) arg;
	fprintf(stdout,"The Required tasks is running %lu\n",seconds);
}
double getTime(clock_t t){
	double time_taken = ((double)t)/CLOCKS_PER_SEC;
	return time_taken;
}
int main(int argc, char const *argv[])
{
	clock_t t1,t2,t3,t4;
	t1 = clock();
	printf("Initial time ==== %.2f\n",getTime(t1) );
	ThreadPool *t = ThreadPool_construct();
	t2 = clock();
	t3 = t2 - t1;
	printf("pool construct time ==== %.2f\n",getTime(t3) );
	long i;
	
    Thread * thr = malloc(sizeof(Thread));
    
	for(i=0;i<SIZE;i++){
		ThreadPool_run(t,thr,sampleTask,(void *)i);
	}
	t1 = clock();
	t3 = t1 - t2;
	// time_taken = ((double)t3)/CLOCKS_PER_SEC;
	printf("Total task execution time ==== %.2f\n",getTime(t3) );

	for(int i =0;i < SIZE; i ++){
    	ThreadPool_join(t->ids[i], NULL);
    	// printf("Thread joined\n");
    }
    t2 = clock();
    t4 = t2/sz;
	t3 =  t2 - t1;
	printf("Thread Join time == ==== %.2f\n",getTime(t3) );
	ThreadPool_destruct(t);

	t1 = clock();
	t3 = t1 -t2;
	printf("Thread destruct time == ==== %.2f\n",getTime(t3) );
	pthread_t testId[SIZE];
	t3 = clock();

	for(i=0;i<SIZE;i++){
		pthread_create(&testId[i],NULL,sampleTask,(void *)i);
	}
	t2 = clock();
	t3 = t2 -t1;
	printf("\n\nNormal Pthread time == ==== %.2f\n",getTime(t3) );
	for(i=0;i<SIZE;i++){
		pthread_join(testId[i],NULL);
	}	
	
	t1 = clock();
	t3 = t1 - t2;
	printf("Pthread join time == ==== %.2f\n",getTime(t3) );
	// time_taken = ((double)t4)/CLOCKS_PER_SEC;
	// printf("time ==== %.2f\n",time_taken);
	return 0;
}