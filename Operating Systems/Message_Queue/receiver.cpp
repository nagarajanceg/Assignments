extern "C" {
#include "MQ.h"
}
#include <assert.h>
#include <stdio.h>
#include <iostream>

int main() {

    int rv;
    cs550_MQ mq;

    // rv = unlink("/tmp/mq");
    rv = cs550_MQOpen(&mq, "hellofile", 5); assert(rv == 0);
    char msg1[1024];
    int i=0;
    while (true) {
        char msg[1024];
        rv = cs550_MQRecv(&mq, msg, 1024); 
        assert(rv >= 0);
        msg[rv] = '\0';
        printf("%s\n",msg );
        if(i == 2){
            break;
        }
        i++;
    }
    while(true){
        sleep(2);
        if(i == 5){
            break;
        }
        ssize_t sz = cs550_MQPeek(&mq);
        if(sz == 0){
            // printf("%s\n",CS550_NOMSG);
        }else{
            printf("peek msgsize = %zu\n",sz);
        } 
        i++;
    }
  rv = cs550_MQClose(&mq); assert(rv == -1); 
}
