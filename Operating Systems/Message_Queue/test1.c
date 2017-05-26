// extern "C" {
// #include "MQ.h"
// }
#include "MQ.h"
#include <stdio.h>
#include <assert.h>

void notify_callback(const char *msg, size_t sz)
{
    printf("inside my_callback %s == %zu\n",msg,sz);
}
 
int main(int argc, char const *argv[])
{
	struct cs550_MQ mq;
	int rv = cs550_MQOpen(&mq, "hellofile", 5);assert(rv == 0);
	char msg[1000];
	
	cs550_MQNotify(&mq, &notify_callback);
	

	while(1){
		printf("its working\n");
		sleep(10);
		rv = cs550_MQRecv(&mq, msg, 1024); assert(rv == CS550_CBREGISTERED);
		if(rv == CS550_CBREGISTERED){
			printf("Already callback Registered\n");
		}
		rv = cs550_MQPeek(&mq);assert(rv == CS550_NOMSG);
		ssize_t sz = cs550_MQPeek(&mq);
		if(sz == CS550_NOMSG){
			printf("No messages available\n");
		}else{
			printf("peek msgsize = %zu\n",sz);
		}
	}
	rv = cs550_MQClose(&mq);assert(rv == 0);

	return 0;
}
