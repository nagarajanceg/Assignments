#ifndef _THREADPOOL_H_
#define _THREADPOOL_H_

#define sz 10;
typedef struct thread
{
	pthread_t id;
	int shouldBeJoined;
	int index;
	struct thread_pool * threadPool;
}Thread;

typedef struct thread_queue{
	// int size;
	// pthread_t thread;
	Thread *thread;
	void* (*routine) (void*);
	void * arg;
	struct thread_queue* next;
}Queue;


typedef struct thread_pool{
	int count;
	int tobeDestroyed;
	int queueSize;
	int state; /*0 - idle , 1-running*/
	int closed;
	pthread_mutex_t mutex;
    pthread_cond_t q_not_empty;
    pthread_cond_t q_empty;
    pthread_cond_t q_run;
    Queue *qhead;
    Queue *qtail;
    Thread **ids;
    pthread_t test;
}ThreadPool;

typedef struct pthread_arg
{
	ThreadPool * threadPool;
	Thread * thread;
} PthreadArg;

typedef void* (*dispatch_fn)(void *);
ThreadPool* ThreadPool_construct();
static void* workers(void* arguments);
void createThread(ThreadPool* threadPool);
void ThreadPool_run(ThreadPool* threads, Thread * thread, dispatch_fn dispatch_to_here, void *arg);
void ThreadPool_destruct(ThreadPool*);
void ThreadPool_join(Thread *,void**);
// void normal_pthread();
#endif