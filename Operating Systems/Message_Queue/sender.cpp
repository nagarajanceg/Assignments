extern "C" {
#include "MQ.h"
}
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
struct stat st;

int main()
{
	int rv,i;
	cs550_MQ mq;
    rv = cs550_MQOpen(&mq, "hellofile", 10);assert(rv == 0);
	for(i=0;i<13; i++){
		// printf("from start in send\n");
		char buf[100];
		sprintf(buf, "Hello %d from process deatils listed with pid %d!", i,
        (int) getpid());
		rv = cs550_MQSend(&mq, buf, strlen(buf));
	 	printf("%s\n",buf );
	 	sleep(1);
	}
	 rv = cs550_MQClose(&mq); 

	 printf("close assert Value %d\n",rv );
	 assert(rv == -1);
	
	return 0;
}
