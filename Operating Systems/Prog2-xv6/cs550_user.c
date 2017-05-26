#include "types.h"
#include "stat.h"
#include "user.h"
#include "date.h"

int main(){
	int it =100000,i,time, ticks=100,sum;
	int nanoFactor = 1000000000/(it*ticks);
	int start = uptime() * nanoFactor;
	printf(1," starting uptime %d \n",start );
	for(i=0;i<it;i++){
		getpid();
	}
	time = uptime() * nanoFactor - start;
	printf(1,"time after printing getpid for 100000 iterations = %d\n", time);
	
	for(i=0;i<it;i++){
		sum = aaICO();
	}
	time = uptime() * nanoFactor - start -time;
	printf(1,"first call in linear search for 100000 iterations time = %d, sum = %d\n", time, sum);

	for(i=0;i<it;i++){
		sum = xMznMiMbd2ASBAopjUBD();
	}
	time = uptime() * nanoFactor - start - time;
	printf(1,"last call in linear search for 100000 iterations time= %d, sum =%d\n", time, sum);

	for(i=0;i<it;i++){
		sum =ajJ2rUS36Ds7gCvYhWvRMQ4cM();
	}
	time = uptime() * nanoFactor - start -time;
	printf(1,"first call in hashing for 100000 iterations time= %d, sum=%d\n", time, sum);

	for(i=0;i<it;i++){
		sum = k_jNM();
	}
	time = uptime() * nanoFactor - start -time;
	printf(1,"last call in hashing for 100000 iterations time= %d, sum=%d \n", time, sum);

	struct rtcdate st;
        if(date(&st)){
                printf(2,"getdate failed\n");
                exit();
        }
        printf(1,"%d-%d-%d:%d:%d:%d\n", st.year, st.month, st.day, st.hour, st.minute, st.second);
	exit();
}