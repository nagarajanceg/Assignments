#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <limits.h>
#include <math.h>

#define NMAX 8
#define NMIN 4
#define PROFITMAX 30
#define PROFITMIN 10
#define WEIGHTMAX 20
#define	WEIGHTMIN 5

typedef struct products
{
	int profit;
	int weight;
}Product;

/*structure to hold Brute force knapsack values*/
typedef struct result
{
	int *profit;
	int *weight;
	int pos;
}Result;

/*x - Max , y- Min*/
int generateRandom(x,y){
	return ((rand() %(x+1 - y)) +y);
}
void printProduct(int n, Product pt[n]){
	int i;
	for(i=0;i<n;i++){
		printf("\tprofit = %d weight = %d\n",pt[i].profit, pt[i].weight );
	}
}
/*Generate random values*/
void fillValues(int n, Product pt[n]){
	int i;
	for(i=0;i<n;i++){
		pt[i].profit = generateRandom(PROFITMAX, PROFITMIN);
		pt[i].weight = generateRandom(WEIGHTMAX, WEIGHTMIN);
	}	
}
/*calculating the capacity with factor 0.6*/ 
int calculateCapacity(int n, Product pt[n]){
	int i,weight=0;
	for(i=0; i<n;i++){
		weight += pt[i].weight;
	}
	weight = floor(0.6 * weight);
	return weight;
}
void printTotal(int n, Product pt[n], int capacity){
	int i,weight=0,profit=0;
	for(i=0; i<n;i++){
		weight += pt[i].weight;
		profit += pt[i].profit;
	}
	printf("Total\n\tprofit = %d weight=%d\n",profit, weight );
}
void initializeStruct(int n, Result* elements){
	int i,k;
	for(i=0; i<n; i++){
		elements[i].profit = (int *)malloc(n*sizeof(int));
		elements[i].weight = (int *)malloc(n*sizeof(int));
		for(k=0; k<n; k++){
			elements[i].profit[k] =0;
			elements[i].weight[k] =0;
		}
		elements[i].pos = 0;
	}
}
void freeStruct(int n, Result* elements){
	int i;
	for(i=0; i<n; i++){
		free(elements[i].profit);
		free(elements[i].weight);
	}
}
/*choose suitable weights based on profit*/
void selectItems(int n, Result* rt, int capacity){
	int i,j, maxProfit =0, pos, maxpos;
	int profitSum, weightSum;
	for(i=0;i<n;i++){
		profitSum = 0;
		weightSum = 0;
		for(j=0;j<rt[i].pos;j++){
				profitSum += rt[i].profit[j];
				weightSum += rt[i].weight[j];
		}
		if(maxProfit  <= profitSum && weightSum <= capacity ){
			maxProfit = profitSum;
			maxpos = i;
			pos = rt[i].pos;
		}
	}
	printf("\n******************** Brute Force Knapsack ******************** \n");
	printf("selected Items \n");
	int totalProfit = 0;
	int totalWeight = 0;
	for(i =0; i< pos; i++){
		printf("\tProfit = %d Weight = %d\n",rt[maxpos].profit[i], rt[maxpos].weight[i] );
		totalProfit += rt[maxpos].profit[i];
		totalWeight += rt[maxpos].weight[i];
	}
	printf("Total \n");
	printf("\tProfit == %d Weight=%d, \n",totalProfit, totalWeight);
}
void bruteForceKnapsack(int n, Product pt[n], int cap){
	int i,j,k;
	k = pow(2,n);
	Result result[k];
	initializeStruct(k,result);
	/*Calculate 2^n set combination*/
	for(i=0; i<(1<<n); i++){
		for(j=0;j<n; j++){
			if(i &(1 <<j)){
				result[i].profit[result[i].pos] += pt[j].profit;
				result[i].weight[result[i].pos] += pt[j].weight;
				result[i].pos += 1;
			}
		}
	}
	selectItems(k,result,cap);
	freeStruct(k,result);
}

void printValues(int n, int cap, int b[n][cap] ){
	int i,j;
	for(i=0; i<n; i++){
		for(j=0; j<cap; j++){
			printf(" %d",b[i][j]);
		}
		printf("\n");
	}	
}

/******* Refined Dynamic Programming starts ************/
int max(int a, int b) { return (a > b)? a : b; }
/* Determine the value only at needed position based on recursion and store the results in table*/
int findPosition(int N, int W, int b[N][W], int n, int w, Product* pt){
	int w1;
	if(n==0 || w <=0){
		return 0;
	}
	b[n-1][w] = findPosition(N, W, b, n-1, w, pt);
	w1 = w - pt[n-1].weight; 
	if(w1 >= 0){
		b[n-1][w1] = findPosition(N, W, b, n-1, w1, pt);
	}else{
		return b[n-1][w];
	}
	if(pt[n-1].weight <= w){
		return max(b[n-1][w], pt[n-1].profit + b[n-1][w1]);
	}else{
		return b[n-1][w];
	}
}
void findSelectedItems(int N, int W, int b[N][W], Product* pt){
	N = N-1;
	W = W-1;
	int position[N],i=0,j;
	while(N >=1 && W > 0){
		if(b[N][W] == b[N-1][W]){
			N = N-1;
		}else{
			position[i] = N -1;
			i++;
			W = W - pt[N-1].weight;
			N = N-1;
		}
	}
	printf("Selected Items:\n");
	int totalProfit = 0;
	int totalWeight = 0;

	for(j=0; j<i;j++){
		totalProfit += pt[position[j]].profit;
		totalWeight += pt[position[j]].weight;
		printf("\tProfit = %d Weight = %d\n", pt[position[j]].profit, pt[position[j]].weight);
	}
	printf("Total:\n\tProfit = %d weight = %d \n",totalProfit, totalWeight );
}

void refinedDp(int n, int cap, Product* pt){
	int b[n+1][cap+1];
	int i,j;
	for(i=0; i<=n; i++){
		for(j=0; j<=cap; j++){
			b[i][j] =0;
		}
	}
	b[n][cap] = findPosition(n + 1, cap + 1, b, n, cap, pt);
	printf("******************** Refinement in DP ******************** \n");
	printValues(n + 1,cap + 1,b);
	findSelectedItems(n+1, cap+1, b, pt);
}
/************** backtracking algorithm *****************/
void swap(Product* a, Product* b){
	Product temp;
	temp = *a;
	*a = *b;
	*b =temp;
}
void swapInt(int* a, int* b){
	int temp;
	temp = *a;
	*a = *b;
	*b =temp;
}
/* sort the knapsack values for backtracking*/
void exchangeSort(int* cost, Product* pt, int length){
	int i, j;
	for(i=1; i< length-1;i++){
		for(j=i+1; j<length; j++){
			if(cost[i] < cost[j]){
				swap(&pt[i], &pt[j]);
				swapInt(&cost[i], &cost[j]);
			}
		}
	}
}
/* sort Elements based on weight/profit benefits*/
void initialize(int n, int* arr){
	int i;
	for(i=0; i<=n; i++){
		arr[i] =0;
	}
}
void sortElements(int n, Product* pt){
	int i,cost[n];
	/*calculating cost values benefits*/
	for(i =1; i<n; i++){
		cost[i] = pt[i].profit/ pt[i].weight;
	}
	exchangeSort(cost, pt, n);
}
void copyArray(int n, int* a, int*b){
	int i;
	for(i =1;i<n;i++){
		a[i] = b[i];
	}
}
/* Upper Bound values calculated*/
double fracKnap(int i, int weight, int profit, Product* pt, int capacity, int n){
	double bound = profit;
	int j;
	double x[n];
	for(j=i; j<=n; j++){
		x[j] = 0;
	}
	while(weight < capacity && i < n){
		if(weight+pt[i].weight <= capacity){
			x[i] = 1;
			weight = weight + pt[i].weight;
			bound = bound + pt[i].profit;
		}else{
			x[i] = (capacity - weight)/(double)pt[i].weight;
			weight = capacity;
			bound = bound +  x[i] * (double)pt[i].profit;
		}
		i = i+1;
	}
	return bound;
}
/*Determine the node is promising based on bound value greater than max profit obtained so far*/
int promising(int i, int capacity,int profit, int weight,int n, int* maxprofit, Product* pt){
	if(weight >= capacity){
		return 0;
	}
	double bound;
	bound = fracKnap(i+1, weight, profit, pt, capacity, n);
	printf("\t%f", bound );
	return bound > (*maxprofit)? 1: 0; 
}
void knapsack(int start, int n, int profit, int weight, int capacity, int *maxprofit, Product* pt,int* bestset, int* include ){
	printf("\n\t\t%d\t%d", profit, weight);
	if(weight <= capacity && profit > (*maxprofit)){
		*maxprofit = profit;
		copyArray(n, bestset, include);
	}
	int isPromise = promising(start, capacity, profit, weight, n, maxprofit, pt);
	if(isPromise){
		include[start+1] = 1;
		knapsack(start+1, n, profit+pt[start+1].profit, weight+pt[start+1].weight, capacity, maxprofit, pt, bestset, include);
		include[start+1] = 0;
		knapsack(start+1, n, profit, weight, capacity, maxprofit, pt, bestset, include);
	}
}
/*helper function*/
void moveElementIndex(int n, Product* element, Product* pt){
	int i,j=0;
	for(i=1;i<n;i++){
		pt[i].profit = element[i-1].profit;
		pt[i].weight = element[i-1].weight; 
	}
}
void displayOutput(int n, int* bestset, Product* pt){
	int i;
	printf("\nSelected Items\n");
	int totalProfit = 0;
	int totalWeight = 0;
	for(i=1; i<=n; i++){
		if(bestset[i] == 1){
			totalProfit += pt[i].profit;
			totalWeight += pt[i].weight;
			printf("\tProfit = %d Weight = %d\n",pt[i].profit, pt[i].weight);
		}
	}
	printf("Total\n\tProfit=%d Weight=%d\n", totalProfit, totalWeight);
}
void backtrackCompute(int n, int capacity, Product* element){
	int maxprofit =0 ;
	int profit =0, weight =0, start =0;
	n = n+1;
	Product pt[n];
	moveElementIndex(n, element, pt);
	int bestset[n];
	int include[n];
	sortElements(n,pt);
	initialize(n,bestset);
	initialize(n,include);
	printf("\n******************** Backtracking Results ********************\n");
	printf("Stepwise \tProfit\tWeight\tBound \n");
	knapsack(start, n, profit, weight, capacity, &maxprofit, pt , bestset, include);
	displayOutput(n, bestset, pt);
}

int main(int argc, char const *argv[])
{
	int n,cap;
/* initialize random seed*/
	srand(time(NULL));
	n = generateRandom(NMAX, NMIN);
	printf("Number of Items = %d\n",n);
	Product pt[n];
	fillValues(n, pt);
	cap = calculateCapacity(n,pt);
	printf("Capacity = %d\n", cap);
	printf("Items Details\n" );
	printProduct(n,pt);
	printTotal(n,pt,cap);
	bruteForceKnapsack(n,pt,cap);
	backtrackCompute(n,cap,pt);
	refinedDp(n,cap,pt);
	return 0;
}