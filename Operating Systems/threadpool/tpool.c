#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <malloc.h>
#include <assert.h>
#include <sys/types.h>
#include <string.h>
#include "threadpool.h"

#define SIZE 50

static void* workers(void* arguments){
// void* workers(ThreadPool arguments){
	// ThreadPool* threadPool = (ThreadPool*) arguments;
	PthreadArg *pthreadarg = (PthreadArg *) arguments;
	ThreadPool* threadPool = pthreadarg->threadPool;
	Thread* currentThread = pthreadarg->thread;
	free(pthreadarg);
	Queue* qt;
	int rv, test;
	pthread_t   tid;
	// qt->thread = tid;
	// printf("Worker started => %d\n", currentThread->index);
			
	while(1){
		threadPool->queueSize = threadPool->queueSize;
		pthread_mutex_lock(&(threadPool->mutex));
		while( threadPool->queueSize ==0 && threadPool->tobeDestroyed != 1 && currentThread->shouldBeJoined == 0){
			// pthread_mutex_unlock(&(threadPool->mutex));
			// printf("Worker in condition wait %d\n", currentThread->index);
			pthread_cond_wait(&(threadPool->q_not_empty),&(threadPool->mutex));
			
			// printf("after cond wait== %d \n",threadPool->queueSize);
		}
		if(threadPool->tobeDestroyed == 1 || currentThread->shouldBeJoined == 1){
			pthread_mutex_unlock(&(threadPool->mutex));
			break;
		}
		threadPool->test = tid;
		qt = threadPool->qhead;
		threadPool->queueSize--;
		if(threadPool->queueSize == 0){
			threadPool->qhead = NULL;
			threadPool->qtail = NULL;
		}else{
			threadPool->qhead = qt->next;
		}

		// printf("Reach this place size == %d\n",threadPool->queueSize );
		if(threadPool->queueSize == 0){
			pthread_cond_signal(&(threadPool->q_empty));
		}
		qt->thread = currentThread;
		pthread_cond_broadcast(&(threadPool->q_run));

		pthread_mutex_unlock(&(threadPool->mutex));
		(qt->routine)(qt->arg);
		// free(qt);
	}
	// printf("Thread exiting %d\n", currentThread->index);
}
void createThread(ThreadPool* threadPool){
	int i,rv;
	// printf("Created thread id\n");
	Thread * tempThread;
	for(i=0; i < SIZE ; i++){
		tempThread = (Thread *) malloc(sizeof(Thread));
		tempThread->shouldBeJoined = 0;
		tempThread->index = i;
		tempThread->threadPool = threadPool;
		pthread_t tp;
		PthreadArg * pthreadarg = malloc(sizeof(PthreadArg));
		pthreadarg->threadPool = threadPool;
		pthreadarg->thread = tempThread;

		// rv = pthread_create(&tp, NULL, dummy,NULL); assert(rv==0);

		rv = pthread_create(&(tempThread->id), NULL, workers, (void*)pthreadarg); assert(rv==0);
		// printf(" %lu\n",tempThread->id );
		threadPool->ids[i] = tempThread;
	}
}
ThreadPool* ThreadPool_construct(){
	int rv;
	// Thread* th = (Thread *) malloc(sizeof(pthread_t)*SIZE);
	ThreadPool * threadPool = (ThreadPool *) malloc(sizeof(ThreadPool)*SIZE);
	threadPool->ids = (Thread **) malloc(sizeof(Thread* )*SIZE);
	threadPool->count = SIZE;
	threadPool->queueSize = 0;
	threadPool->qhead = NULL;
	threadPool->qtail = NULL;
	threadPool->tobeDestroyed = 0;
	threadPool->state = 0;
	rv = pthread_mutex_init(&threadPool->mutex,NULL);assert(rv==0);
	rv = pthread_cond_init(&threadPool->q_empty,NULL);assert(rv==0);
	rv = pthread_cond_init(&threadPool->q_not_empty,NULL);assert(rv==0);
	rv = pthread_cond_init(&threadPool->q_run,NULL);assert(rv==0);
	createThread(threadPool);
	printf("Thread creation completed\n");
	return threadPool;
}

void ThreadPool_run(ThreadPool* threads, Thread * thread, dispatch_fn dispatch_to_here, void *arg){
	int k;
	ThreadPool * threadPool = threads;
	Queue* pres;
	pres = (Queue*)malloc(sizeof(Queue));

	pres->routine = dispatch_to_here;
	pres->arg = arg;
	pres->next = NULL;
	pres->thread = NULL;
	k = threadPool->queueSize;
	// printf("Queue Size == %d\n",k );
	pthread_mutex_lock(&(threadPool->mutex));
	/*if(pool->dont_accept) { //Just incase someone is trying to queue more
		free(cur); //work structs.  
		return;
	}*/
	if(threadPool->queueSize == 0){
		threadPool->qhead = pres;
		threadPool->qtail = pres;
		pthread_cond_signal(&(threadPool->q_not_empty));

	}else{
		threadPool->qtail->next = pres;
		threadPool->qtail = pres;
	}
	threadPool->queueSize++;
	while(pres->thread == NULL){
		pthread_cond_wait(&(threadPool->q_run), &(threadPool->mutex));
	}

	pthread_mutex_unlock(&(threadPool->mutex));
	memcpy(thread,pres->thread, sizeof(Thread));
	free(pres);
}
void ThreadPool_join(Thread* thread, void** t){
	
	if(thread == NULL||thread->shouldBeJoined == 1){
		// printf("Thread already joined\n");
		return;
	}
	printf("Thread joining called %d\n", thread->index);
	thread->shouldBeJoined = 1;
	pthread_cond_broadcast(&(thread->threadPool->q_not_empty));
	pthread_join(thread->id, t);
	// printf("Thread joining is called %d\n", thread->index);

}
void ThreadPool_destruct(ThreadPool* threadPool){

	int i;
	for(i=0; i < SIZE; i++){
		ThreadPool_join(threadPool->ids[i],NULL);
	}
	// printf("inside ThreadPool wait == %d\n",threadPool->queueSize);
	// free(threadPool->ids);
	pthread_mutex_destroy(&(threadPool->mutex));
	pthread_cond_destroy(&(threadPool->q_empty));
	pthread_cond_destroy(&(threadPool->q_not_empty));	
	return;
}
