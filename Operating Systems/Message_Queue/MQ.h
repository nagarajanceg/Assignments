#ifndef CS550_MQ_H
#define CS550_MQ_H



#include <unistd.h>
#include <stddef.h>
#include <pthread.h>

// #define CS550_NOMSG "No messages available"
// #define CS550_CBREGISTERED "Already callback Registered"
#define CS550_NOMSG 0
#define CS550_CBREGISTERED 2
#define CS550_TOOBIG 3
#define MAX 100


struct Msg {
    size_t size;
    char buf[100];
    pid_t processId[10];
    int procCnt; /*listener count to specific message*/
};

struct Info {
    pthread_mutex_t mutex;
    pthread_cond_t condvar;
    size_t n, in, out;
    pid_t p[10];
    int cnt; /*No of process count*/
    int notifierSize;
    pid_t notifier[10];
    struct Msg msgs[MAX];
};
struct cs550_MQ {
    struct Info *infop;
};
struct argMQ
{
	struct cs550_MQ* st;
	void(*ptr_reg_callback)(const char* , size_t s);
};
// typedef void(*ptr_reg_callback)(const char* , size_t s);
void(*ptr_reg_callback)(const char* , size_t s);
int cs550_MQOpen(struct cs550_MQ *, const char *path, int queue_length);
int cs550_MQClose(struct cs550_MQ *);
int cs550_MQSend(struct cs550_MQ *, const char *msg, size_t size);
ssize_t cs550_MQRecv(struct cs550_MQ *, char *msg, size_t size);
ssize_t cs550_CustomMQRecv(struct cs550_MQ *, char *msg, size_t size);
ssize_t cs550_MQPeek(struct cs550_MQ *);
int cs550_MQNotify( struct cs550_MQ *, void(*ptr_reg_callback)(const char*, size_t ));
// int cs550_MQNotify(struct cs550_MQ *, void (*cb)(const char *msg, size_t sz));
#endif
