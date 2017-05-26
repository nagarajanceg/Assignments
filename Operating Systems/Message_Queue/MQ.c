#include "MQ.h"
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <assert.h>
#include <stdio.h>
#include <pthread.h>
#include <string.h>
#include <assert.h>
#include <sys/mman.h>
#include <signal.h>
#include <stdlib.h>
int preventNotify =0;
int maxQueue;
int
cs550_MQOpen(struct cs550_MQ *mqp, const char *path, int qlen) {

    int rv;
    maxQueue = qlen;
    if (access(path, F_OK) != 0) {
        int en = errno;
        assert(en == ENOENT);

        int fd = open(path, O_RDWR|O_CREAT|O_EXCL, 0600);
        if (fd < 0) {
            return -1;
        }

        rv = ftruncate(fd, sizeof(struct Info)); assert(rv == 0);

        void *vp = mmap(NULL, sizeof(struct Info), PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
        assert(vp != MAP_FAILED);

        rv = close(fd); assert(rv == 0);

        mqp->infop = (struct Info * ) vp;
        
        {
            pthread_mutexattr_t attr;
            rv = pthread_mutexattr_init(&attr); assert(rv == 0);
            rv = pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_ERRORCHECK); assert(rv == 0);
            rv = pthread_mutexattr_setpshared(&attr, PTHREAD_PROCESS_SHARED); assert(rv == 0);
            rv = pthread_mutex_init(&mqp->infop->mutex, &attr); assert(rv == 0);
            rv = pthread_mutexattr_destroy(&attr); assert(rv == 0);
        }

        {
            pthread_condattr_t attr;
            rv = pthread_condattr_init(&attr); assert(rv == 0);
            rv = pthread_condattr_setpshared(&attr, PTHREAD_PROCESS_SHARED); assert(rv == 0);
            rv = pthread_cond_init(&mqp->infop->condvar, &attr); assert(rv == 0);
            rv = pthread_condattr_destroy(&attr); assert(rv == 0);
        }

        mqp->infop->n = 0;
        mqp->infop->in = 0;
        mqp->infop->out = 0;
        mqp->infop->cnt = 0;
        mqp->infop->notifierSize =0;
        // mqp->infop->msgs = malloc(qlen* sizeof(struct Msg));
    } else {

        int fd = open(path, O_RDWR);
        if (fd < 0) {
            return -1;
        }

        void *vp = mmap(NULL, 3048, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
        assert(vp != MAP_FAILED);

        rv = close(fd); assert(rv == 0);

        mqp->infop = (struct Info * ) vp;
    }
    struct Info *ip = mqp->infop;
    rv = pthread_mutex_lock(&ip->mutex); assert( rv == 0);
    ip->p[ip->cnt] = getpid();
    ip->cnt = ip->cnt +1;
    rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
    return 0;
}
int
cs550_MQSend(struct cs550_MQ *mq, const char *msg, size_t msg_size) {
    int rv,i;
    if(msg_size > 128*1024){
        fprintf(stderr, "the size of the message over the limit\n" );
        return 0;
    }
    struct Info *ip = mq->infop;
    rv = pthread_mutex_lock(&ip->mutex); assert(rv == 0);
    int notifierSize = ip->notifierSize;
    int *notifier = ip->notifier;

    // printf(" no of messages %zu notifierSize = %d\n",ip->n , notifierSize);
    if(ip->cnt == 1){
        fprintf(stderr, "No listeners in the message queue:\n"); 
        rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
        rv = pthread_cond_broadcast(&ip->condvar); assert(rv == 0);
        return 0;
    }
    fprintf(stderr, "Send: On queue: %zu\n", ip->n);
    while (ip->n == maxQueue) {
        rv = pthread_cond_wait(&ip->condvar, &ip->mutex); assert(rv == 0);
    }

    memcpy(ip->msgs[ip->in].buf, msg, msg_size);
    ip->msgs[ip->in].size = msg_size;
    ip->msgs[ip->in].procCnt = 0;
    
    for(i = 0; i< ip->cnt; i++){
        if(ip->p[i] != getpid() ){
            ip->msgs[ip->in].processId[i] = ip->p[i];
            printf("ip->in = %zu i val = %d pid = %d\n", ip->n, i, (int)ip->msgs[ip->in].processId[i] ); 
            ip->msgs[ip->in].procCnt ++;
        }
    }
   
    ip->in = (ip->in + 1)%5;
    ip->n++;
    fprintf(stderr, "Send: On queue: %zu\n", ip->n);
    rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
    rv = pthread_cond_broadcast(&ip->condvar); assert(rv == 0);
    return 0;
}
ssize_t
cs550_MQRecv(struct cs550_MQ *mq, char *msg, size_t buf_size) {
    ssize_t  sz;
    if(preventNotify){
        // printf("already called by notify\n");
        return 2;
    }else{
        sz = cs550_CustomMQRecv(mq, msg, buf_size);
    }
    
    return sz;
}
ssize_t
cs550_CustomMQRecv(struct cs550_MQ *mq, char *msg, size_t buf_size) {
    
    int rv,i,k;
    ssize_t sz;
    struct Info *ip = mq->infop;
    
    rv = pthread_mutex_lock(&ip->mutex); assert(rv == 0);

    fprintf(stderr, "Before Recv: On queue: %zu \n", ip->n);

    while (ip->n == 0) {
        rv = pthread_cond_wait(&ip->condvar, &ip->mutex); 
        assert(rv == 0);
    }

    int queue = 0;
    int remainingproc = 0;
    int processExist = 0;
    for(i = 0; i < ip->msgs[ip->out].procCnt; i++){
        if(ip->msgs[ip->out].processId[i] != -1) {
            remainingproc ++;
            if(ip->msgs[ip->out].processId[i] == getpid()){
                processExist = 1;
                ip->msgs[ip->out].processId[i] = -1;
            }
        }
    }
    if(processExist == 1){
        memcpy(msg, ip->msgs[ip->out].buf, ip->msgs[ip->out].size);
         sz = ip->msgs[ip->out].size;
    }
    if(remainingproc == 1){
        ip->out = (ip->out + 1)%5;
        ip->n--;
        rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
        rv = pthread_cond_broadcast(&ip->condvar);assert(rv == 0);
    }else{
        rv = pthread_cond_wait(&ip->condvar, &ip->mutex);assert(rv == 0);
        rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
    }
    fprintf(stderr, "After Recv: On queue: %zu\n", ip->n);
    return sz;
}
ssize_t
cs550_MQPeek(struct cs550_MQ *mq) {
    int rv,i,k=0;
    size_t msgSize=0;
    struct Info *ip = mq->infop;
    
    rv = pthread_mutex_lock(&ip->mutex); assert(rv == 0);
    size_t sz = ip -> cnt;
    if(ip->n == 0){
        
        msgSize = 0;
        rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
        // printf("from here , %zu\n",msgSize);
        return msgSize;
    }
    else{
        int temp;
        for (i = ip->n -1,temp = ip->out ; i >=0; i--, temp = (temp + 1) % 5)
        {
            // printf("msg listeners count == %d \n",ip->msgs[temp].procCnt);
            for(k=ip->msgs[temp].procCnt - 1; k >=0; k--){
                // printf("iterating process Id == %d getpid ==%d\n",ip->msgs[temp].processId[k], getpid() ); 
                if(ip->msgs[temp].processId[k] == (int)getpid()){
                    msgSize = ip->msgs[temp].size;
                    rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
                    return msgSize;
                } 
            }
         }   
    }
    
    rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
    return msgSize;
}
int
cs550_MQClose(struct cs550_MQ *mq) {
    struct Info *ip = mq->infop;
    int j=0,i, rv;
    rv = pthread_mutex_lock(&ip->mutex); assert(rv == 0);
     for(i = 0; i< ip->cnt; i++){
        if(ip->p[i] == getpid() ){
            for(j=i; j < ip->cnt-1; j++){
                ip->p[j] = ip->p[j+1];
            } 
            ip->cnt = ip->cnt -1;
            break;
        }
      }
      rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
    return -1;
}  
void* notify(void* arguments){
    struct argMQ *args =(struct argMQ*) arguments;
    struct cs550_MQ* mq = args->st;
    struct Info *ip = mq->infop;

    while(1){
        char msg[1024];
        size_t sz = cs550_CustomMQRecv(mq, msg, 1024);
        msg[sz] = '\0';
        (*args->ptr_reg_callback)(msg,sz);
        // sleep(1);
    }
    pthread_exit(NULL);
    return NULL;
}
int cs550_MQNotify( struct cs550_MQ *mq, void(*callback)(const char* x, size_t sz)){
    struct Info *ip = mq->infop;
    int *notifier = ip->notifier;
    int rv = pthread_mutex_lock(&ip->mutex); assert(rv == 0);
    preventNotify = 1;
    pthread_t notify_thread;
    struct argMQ *args;

    args = malloc(sizeof(struct argMQ));
    args->st = mq;
    // printf("ma == %p == infop ==%p\n",mq, mq->infop );
    args->ptr_reg_callback = callback;
    rv = pthread_create(&notify_thread, NULL, notify ,(void *)args); assert(rv ==0);
    pthread_detach(notify_thread);
    rv = pthread_mutex_unlock(&ip->mutex); assert(rv == 0);
}