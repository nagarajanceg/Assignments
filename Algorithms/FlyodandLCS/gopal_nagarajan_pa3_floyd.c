#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX 100
#define MIN 5

int generateRandom(){
	return ((rand() %(MAX+1 - MIN)) +MIN);
}

void printMatrix(int n, int inputMatrix[][n]){
	int i,j;
	for(i=0; i<n ; i++){
		for(j=0; j<n; j++){
			 printf("%7d ",inputMatrix[i][j]);
		}
		 printf("\n");
	}
	printf("\n");
}

void assignAdjValues(int n, int a[][n]){
	int i,j,x;
	/* assign values in lower triangular matrix and make a copy to upper traingular part to preserve undirected */
	for(i =0; i<n; i++){
		for(j=0; j<=i; j++){
			if(i == j){
				a[i][j] = 0;
			}else{
				x = rand() %10 +1;
				a[i][j] = x;
				a[j][i] = x;
			}
		}
	}
}

/*Print the intermediate nodes available in shortest path*/
void minimumPath(int start, int end, int n, int p[][n]){
	if(p[start][end] !=0){
		minimumPath(start, p[start][end],n,p);
		printf("V%d -> ",p[start][end]);
		minimumPath(p[start][end],end,n,p);
		return;
	}
	return;
}
/*Print the expected ouput values with format*/
void output(int n, int a[][n], int p[][n]){
	int i,j;
	printf("Final Adjacency matrix\n");
	printMatrix(n,a);
	printf("shortest path intermediate list\n");
	printMatrix(n,p);
	printf("All pairs shortest path and length\n");
	for(i=0; i<n;i++){
		for(j=0; j<n; j++){
			if(i != j ){
				printf("path length = %d  ",a[i][j]);
				printf("V%d -> ",i );
				minimumPath(i,j,n,p);
				printf("V%d \n",j);	
			}
		}
	}
	printf("\n");
}
/*calculate the adjacency list using flyod algorithm*/
void flyodDistance(int n, int a[][n]){
	int i,j,k ;
	int p[n][n];
	for(i=0; i<n; i++){
		for(j=0; j<n; j++){
			p[i][j] = 0;
		}
	}
	for(k=0; k<n; k++){
		for(i=0; i<n; i++){
			for(j=0; j<n; j++){
				if(a[i][j] > a[i][k]+a[k][j]){
					a[i][j] = a[i][k] + a[k][j];
					p[i][j] = k;
				}
			}
		}
	}
	output(n,a,p);
}

int main(){
	/* initialize random seed*/
	srand(time(NULL));
	int n;
	n = generateRandom();
	n =1000;
	printf("Number of vertices = %d\n",n );
	int adj[n][n];
	assignAdjValues(n, adj);
	printf("Initial Adjacency matrix \n");
	printMatrix(n, adj);
	flyodDistance(n, adj);
	return 0;
}