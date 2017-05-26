#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <limits.h>

#define MAX 10
#define MIN 5
#define INF -1

typedef struct Edge
{
	int src, dest, weight;
}Edge;

typedef struct elementSet
{
	int parent;
	int height;
}ElementSet;

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
/*Print the Spanning Tree*/
void printTree(int n, int* parent){
	int i;
	printf("minimum Spanning Tree Edges \n");
	for(i=1; i<n; i++){
		printf("%d - %d \n",parent[i], i );
	}
}
/* Populate values to generate undirected graph*/
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
void initialize(int n, int *a, int val){
	int i,j;
	for(i=0;i<n;i++){
		a[i] = val;
	}
}
/*Extract a minimum value from cost array */
int priorityMinQueue(int n, int* cost, int* spanningTree){
	int i, minimumIndex;
	int minimum = INT_MAX;
	for(i=0; i <n; i++){
		if(spanningTree[i] == INF && ( cost[i] < minimum )){
			minimum = cost[i];
			minimumIndex = i;
		}
	}
	return minimumIndex;
}
void prims(int n, int a[][n]){
	int i, firstMin, j;
	int cost[n];
	int parent[n];
	int spanningTree[n];

	initialize(n, spanningTree, INF);
	initialize(n,cost, INT_MAX);
	initialize(n,parent,0);
	parent[0] = -1;
	cost[0] = 0;

	for(i=0; i<n-1; i++){
		firstMin = priorityMinQueue(n,cost, spanningTree);
		/*assign the Minimum to spanning Tree as visited*/
		spanningTree[firstMin] = 1;
		for(j=0; j<n; j++){
			if(spanningTree[j] == -1 && a[firstMin][j]){
				if(a[firstMin][j] < cost[j]){
					parent[j] = firstMin;
					cost[j] = a[firstMin][j];
				}
			}
		}
	}

	printTree(n,parent);
}
/*Move the adjancency values to edge structure*/
void populateEdges(int n, Edge edge[n*n], int adj[][n]){
	int i,j, k=0, x=0;
	for(i=0; i<n; i++){
		for(j=0;j<=i;j++){
			if(i == j){
				edge[k].src  = i;
				edge[k].dest = j;
				edge[k].weight = 0;
			}else{
				edge[k].src = i;
				edge[k].dest = j;
				edge[k].weight = adj[i][j];
				k++;
				edge[k].src = j;
				edge[k].dest = i;
				edge[k].weight = adj[i][j];
			}
			k++;
		}
	}
}
int find3(ElementSet subsets[], int i)
{
	// Find root and make root as parent of i (path compression)
	int root = i;
	int node = i, parent;
    while(subsets[root].parent != root){
    	root = subsets[root].parent;
    }

    while(subsets[node].parent != root){
    	subsets[node].parent = root;
    }
    return root;
}

void Union3(ElementSet subsets[], int x, int y)
{
	int elementX, elementY;
    elementX = find3(subsets, x);
    elementY = find3(subsets, y);
 
    if (subsets[elementX].height > subsets[elementY].height){
        subsets[elementY].parent = elementX;
    }else if (subsets[elementX].height < subsets[elementY].height){
        subsets[elementX].parent = elementY;
    }
    else
    {
        subsets[elementY].parent = elementX;
        subsets[elementX].height++;
    }
}
void swap(Edge* a, Edge* b){
	Edge temp;
	temp = *a;
	*a = *b;
	*b =temp;
}
void exchangeSort(Edge* mstEdge, int length){
	int i, j;
	for(i=0; i< length-1;i++){
		for(j=i+1; j<length; j++){
			if(mstEdge[i].weight > mstEdge[j].weight){
				swap(&mstEdge[i], &mstEdge[j]);
			}
		}
	}
}

void findMST(int n, Edge edge[n]){
	int v = n;
	int j,x,y,i=0;
	Edge mst[v];
	int e=0 , k=0;
	/*sort the edges based on weights*/
	exchangeSort(edge, n);
	ElementSet subs[v];

	for(j =0; j< v; j++){
		subs[j].parent = j;
		subs[j].height = 0;
	}
	while(e < v-1){
		x = find3(subs, edge[e].src);
		y = find3(subs, edge[e].dest);
		/* Different elements in a set have to merge*/
		if(x != y){
			mst[k] = edge[e];
			k++;
			Union3(subs,x,y);
		}
		e++;
	} 
	printf("Minimum Spanning Tree edges\n");
	for(i=0; i<k; i++){
		printf(" %d - %d\n",mst[i].src, mst[i].dest);
	}
	return;
}
void kruskal(int n, int adj[][n]){
	int size = n*n;
	Edge edges[size];
	populateEdges(n, edges, adj);
	findMST(size, edges);
}
int main(){
	int mstType, n;
	printf("Select Minimum Spaning Tree Algorithm Type\n1 -> Prims \n2 -> Kruskal \n");
	scanf("%d",&mstType);

	/*MST algorthim selection check*/
	if(mstType < 1 || mstType > 3){
		printf("Please choose the MST Type listed\n");
		return 0;
	}
	/* initialize random seed*/
	srand(time(NULL));
	n = generateRandom();
	int adj[n][n];
	assignAdjValues(n, adj);
	switch(mstType){
	case 1: 
			printf("Number of vertices = %d\n",n );
			printMatrix(n, adj);
			prims(n, adj);
			break;
	case 2: 
			printf("Number of vertices = %d\n",n );
			printMatrix(n, adj);
			kruskal(n, adj);
			break;
	default: 
		printf("Please choose the Minimum Spaning Type listed\n");
	}
}