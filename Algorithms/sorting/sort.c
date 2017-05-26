#include <stdio.h>
#include <stdlib.h>
#include <time.h>


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

void visualizeOutput(char* s,int* arr, int n){
	printf("%s\n",s );
	if(n <=20){
		stepWisePrint(arr,n);
	}
	printArray(arr,n);
}

void exchangeSort(int* unsortedArray, int length){
	int i, j;
	for(i=0; i< length-1;i++){
		for(j=i+1; j<length; j++){
			if(unsortedArray[i] > unsortedArray[j]){
				swap(&unsortedArray[i], &unsortedArray[j]);
			}
		}
		if(length <= 20){
			stepWisePrint(unsortedArray, length);
			printArray(unsortedArray, length);
		}
	}
}

void insertionSort(int* unsortedArray, int length){
	int i,j;
	int key;
	for(i=1; i< length; i++){
		key = unsortedArray[i];
		j = i-1;
		while(j >=0 && unsortedArray[j] > key){
			unsortedArray[j+1] = unsortedArray[j];
			j--;
		}
		unsortedArray[j+1] = key;
		if(length <= 20){
			stepWisePrint(unsortedArray, length);
			printArray(unsortedArray, length );
		}
	}
}

void mergeConquer(int* unsortedArray, int low, int mid, int high){
	int i,j,k;
	int leftSize = mid - low +1;
	int rightSize = high - mid;
	int leftSubArray[leftSize] , rightSubArray[rightSize];
	/*split the array based on median and copy that in two sub arrays*/
	for(i=0; i< leftSize; i++){
		leftSubArray[i]=unsortedArray[low+i];
	}
	for(j=0; j<rightSize; j++){
		rightSubArray[j] = unsortedArray[mid+j+1]; 
	}
	/*k index is used to maintain the sorted values*/
	for(k =low,i=0,j=0;(i < leftSize && j <rightSize); k++ ){
		if(leftSubArray[i] <= rightSubArray[j]){
			unsortedArray[k] = leftSubArray[i];
			i++;
		}else{
			unsortedArray[k] = rightSubArray[j];
			j++;
		}
	}
	/*copy the rest of the values using k-index from left and right subarray*/
	while(i < leftSize){
		unsortedArray[k] = leftSubArray[i];
		k++;i++;
	}
	while(j < rightSize){
		unsortedArray[k] = rightSubArray[j];
		k++;j++;
	}
}

void mergeDivide(int* unsortedArray, int low, int high, int length){
	if(low < high){
		int mid = (low + high)/2;	
		mergeDivide(unsortedArray, low, mid, length);
		mergeDivide(unsortedArray, mid+1, high, length);
		mergeConquer(unsortedArray,low, mid, high);
		if(length <= 20){
			stepWisePrint(unsortedArray, length);
			printArray(unsortedArray, length);
		}
	}
}

void mergeSort(int* unsortedArray, int length){
	mergeDivide(unsortedArray, 0, length-1, length);
}

int partition(int* unsortedArray, int low, int high, int length){
	/* initialize random seed*/
	srand(time(NULL));

	int i = low-1, j;
	/*choose the random index as pivot*/
	int randomIndex = low+(rand()%(high -low +1));
	int pivot = unsortedArray[randomIndex];
	/*Always move the pivot to last element of the current array*/
	swap(&unsortedArray[randomIndex], &unsortedArray[high]);
	for(j =low;j < high; j++){
		if(unsortedArray[j] < pivot){
			i = i+1;
			swap(&unsortedArray[i],&unsortedArray[j]);
		}
	}
	/*switch back the pivot to the proper position*/
	swap(&unsortedArray[i+1], &unsortedArray[high]);
	if(length <= 20){
		stepWisePrint(unsortedArray, length);
		printArray(unsortedArray, length);
	}
	return (i+1);
}

void quickSort(int* unsortedArray, int low, int high, int length){
	if(low < high){
		/*get the pivot index and sort the array recursively*/
		int pivotElement = partition(unsortedArray, low, high, length);
		quickSort(unsortedArray, low, pivotElement-1,length);
		quickSort(unsortedArray, pivotElement+1, high,length);
	}
}

/* function to generate random numbers*/
void randomizeInput(int* inputArray, int n){
	/* initialize random seed*/
	srand(time(NULL));
	int i;
	if(n < 20){
		for(i =0; i< n;i++){
			inputArray[i] = rand() %15;
		}
	}else{
		for( i =0; i < n; i++){
			inputArray[i] = rand() % 32768;
		}
	}
}

int main()
{
	int sortType, n;
	printf("Select Sort Type\n1 -> Exchange Sort\n2 -> Insertion Sort \n3 -> Merge Sort \n4 -> Randomized Quick Sort\n");
	scanf("%d",&sortType);
	/*sort algorthim selection check*/
	if(sortType < 1 || sortType > 4){
		printf("Please choose the sortType listed\n");
		return 0;
	}
	printf("Enter a number in range of 1 to 1000\n");
	scanf("%d",&n);
	int arr[n];
	/*out of range check*/
	if(n<1 || n>1000){
		printf("The given number is out of range. Please choose a number from 1 to 1000\n");
		return 0;
	}else{
		randomizeInput(arr, n);
		visualizeOutput("Before sorting",arr,n);
	}
	switch(sortType){
		case 1: exchangeSort(arr,n);
				visualizeOutput("After sorting",arr,n);
				break;
		case 2: insertionSort(arr,n);
				visualizeOutput("After sorting",arr,n);
				break;
		case 3: mergeSort(arr,n);
				visualizeOutput("After sorting",arr,n);
				break;
		case 4: quickSort(arr,0,n-1,n);
				visualizeOutput("After sorting",arr,n);
				break;
		default: 
				printf("Please choose the sortType listed\n");

	}
	return 0;
}