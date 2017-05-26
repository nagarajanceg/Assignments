.PHONY: a.out
a.out:
	rm -rf "hellofile"
	gcc  -O0 -ggdb -c -pthread MQ.c

	g++  -O0 -ggdb -pthread  sender.cpp MQ.o -o sender
	g++  -O0 -ggdb -pthread  receiver.cpp MQ.o -o receiver
	gcc -O0 -ggdb -pthread test1.c MQ.o -o test1
clean:
	rm sender receiver test1
