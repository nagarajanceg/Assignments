

I have implemented message received to multiple receivers. Notify callback can be registered via cs550Notify function. This callback will execute whenever message added to the queue after the callback register.  Receive called after notify by same process will get notification like already registered. Peek will return the size for a process having a pending message otherwise will return CS550_NOMSG.  Message queue of length 100 is implemented. In API
int cs550_MQOpen(struct cs550_MQ *, const char *path, int queue_length);Queue length can be 100. Maximum size assigned for the message queue as 100.

Test file:
I have attached three files apart from MQ.c ,MQ.h along with Makefile. I have tested my code with those files. test1.c having the notify callback by one process. Sender.cpp having message sending code act as one process. Receiver.cpp used to receive as another process.
