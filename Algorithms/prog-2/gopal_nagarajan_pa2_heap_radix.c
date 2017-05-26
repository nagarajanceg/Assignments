#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

void swap(int* a, int* b){
	int temp;
	temp = *a;
	*a = *b;
	*b =temp;
}

void printArray(int* outputArray, int length){
	int i,j;
	if(length >20){
		for(i=0; i<length; i++){
			printf("%d ", outputArray[i]);
		}
		printf("\n");
	}else{
		for (i = 0; i < length; i++){
			for(j=0; j < outputArray[i]; j++){
				printf("*");
			}
			printf("\n");
		}
	}
}
void stepWisePrint(int* arr, int length){
	int i;
	for(i =0; i< length; i++){
		printf("%d ",arr[i]);
	}
	printf("\n");
}

void visualizeOutput(char* s, int* arr, int n){
	printf("%s\n",s );
	if(n <=20){
		stepWisePrint(arr,n);
	}
	printArray(arr,n);
}
/* function to generate random numbers*/
void randomizeInput(int* inputArray, int n, int flag){
	/* initialize random seed*/
	// srand(time(NULL));
	int i;
	if(n < 20){
		for(i =0; i< n;i++){
			inputArray[i] = rand() % 15;
		}
	}else{
		if(flag){
			for( i =0; i < n; i++){
				inputArray[i] = rand() % 999;
			}
		}else{
			for( i =0; i < n; i++){
				inputArray[i] = rand() % 32768;
			}
		}
	}
}
void heapify(int *a, int start, int end){
	int largest = start;
	int left = 2*start + 1;
	int right = 2*start + 2;

	if(left < end && a[left] > a[largest]){
		largest = left;
	}
	if(right < end && a[right] >a[largest]){
		largest = right;
	}
	
	if(largest != start){
		swap(&a[largest], &a[start]);
		heapify(a, largest, end);
	}
}
void sortHeap(int *a, int n){
	int i;
	for(i = (n/2-1);i>=0; i--){
		heapify(a, i, n);
	}
	for(i = n-1; i>=0; i--){
		swap(&a[0], &a[i]);
		if(n <=20){
			stepWisePrint(a,n);
			printArray(a,n);
		}
		heapify(a, 0, i);
	}
}
void stableCountSort(int n, int digit, int* a){
	int countArr[10] ={0}; /* The digits ranges from 0 to 9*/
	int b[n];
	int i;
	for(i=0; i<n; i++){
		countArr[(a[i]/digit)%10] += 1;
	}
	for(i=1; i<10; i++){

		countArr[i] += countArr[i-1];
	}
	for(i =n-1; i>=0;i--){
		int offset = (a[i]/digit)%10; 
		b[countArr[offset]-1] =a[i];
		countArr[offset] -= 1;
	}
	for(i=0;i<n;i++){
		a[i] = b[i];
	}
	if(n <=20){
		stepWisePrint(a,n);
		printArray(a,n);
	}
	
}
int obtainMax(int* a,int n){
	int max = a[0];
	int i;
	for(i=1;i<n;i++){
		if(max < a[i]){
			max = a[i];
		}
	}
	return max;
}
/*The range is from 0 to 999*/
void radixSort(int* a, int n){
	int max = obtainMax(a,n);
	int digit;
	for(digit =1; max/digit > 0; digit *= 10){
		stableCountSort(n,digit,a);
	}
}
int main(){
	int sortType, n;
	printf("Select Sort Type\n1 -> Heap Sort\n2 -> Radix Sort \n");
	scanf("%d",&sortType);
	/*sort algorthim selection check*/
	if(sortType < 1 || sortType > 2){
		printf("Please choose the sortType listed\n");
		return 0;
	}
	printf("Enter a number in range of 1 to 1000\n");
	scanf("%d",&n);
	int a[n];

	/*out of range check*/
	if(n<1 || n>1000){
		printf("The given number is out of range. Please choose a number from 1 to 1000\n");
		return 0;
	}else{
		switch(sortType){
			case 1: 
					randomizeInput(a,n,0);
					visualizeOutput("Before sorting",a,n);
					sortHeap(a,n);
					visualizeOutput("After sorting",a,n);
					break;
			case 2: randomizeInput(a,n,1);
					visualizeOutput("Before sorting",a,n);
					radixSort(a,n);
					visualizeOutput("After sorting",a,n);
					break;
			default: 
					printf("Please choose the sortType listed\n");

		}
	}
}