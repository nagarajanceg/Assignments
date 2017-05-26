#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#define MIN 2 /* minimum dimension of matrix to be multiplied*/
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
void fillMatrix(int n, int firstInputMatrix[][n], int secInputMatrix[][n]){
	int i,j;
	/* initialize random seed*/
	srand(time(NULL));
	for(i=0; i<n ; i++){
		for(j=0; j<n; j++){
			firstInputMatrix[i][j] = rand() % 9 +1;	
			secInputMatrix[i][j] = rand() % 9 +1;
		}
	}
}

void intiailizeArray(int n, int array[][n]){
    memset(array,0,sizeof(int)*n*n);
}

void multiplyMatrix(int n, int firstInputMatrix[][n], int secInputMatrix[][n], int result[][n] ){
	int i,j,k;
	/*intiailize the result matrix with zeros*/
	intiailizeArray(n, result);
	for(i=0;i<n;i++){
		for(j=0;j<n;j++){
			for(k =0; k<n; k++){
				result[i][j] += firstInputMatrix[i][k] * secInputMatrix[k][j];
			}
		}
	}
}

void add(int len, int A[][len], int B[][len], int C[][len] ){
	int i, j;

    for (i = 0; i < len; i++) {
        for (j = 0; j < len; j++) {
            C[i][j] = A[i][j] + B[i][j];
        }
    }
}

void sub(int len, int A[][len], int B[][len], int C[][len] ) {
    int i, j;

    for (i = 0; i < len; i++) {
        for (j = 0; j < len; j++) {
            C[i][j] = A[i][j] - B[i][j];
        }
    }   
}
int round_of_power2(int x){
    x--;
    x |= x >> 1;
    x |= x >> 2;
    x |= x >> 4;
    x |= x >> 8;
    x |= x >> 16;
    x++;
    return x;
}

void assignSubMatrixValues(int len, int n,int LL[][len], int LR[][len], int RL[][len], int RR[][len], int stepMatrix[][n]){
    int i,j;
    for(i = 0; i < len; i++) {
        for(j = 0; j < len; j++) {
            LL[i][j] = stepMatrix[i][j];
            LR[i][j] = stepMatrix[i][j + len];
            RL[i][j] = stepMatrix[i + len][j];
            RR[i][j] = stepMatrix[i + len][j + len];
        }
    }
}
void reduceMatrix(int n, int firstInputMatrix[][n], int secInputMatrix[][n], int resMatrix[][n]){
	if(n < 2){
		// multiplyMatrix(n, firstInputMatrix, secInputMatrix, resMatrix);
        resMatrix[0][0] = firstInputMatrix[0][0] * secInputMatrix[0][0];
		return;
	}else if (n == 2){
        /*Calculate strassen formula for 2*2 matrix*/
        int m1 = (firstInputMatrix[0][0]) * (secInputMatrix[0][1] - secInputMatrix[1][1]);
        int m2 = (firstInputMatrix[0][0] + firstInputMatrix[0][1])*(secInputMatrix[1][1]);
        int m3 = (firstInputMatrix[1][0] + firstInputMatrix[1][1])*(secInputMatrix[0][0]);
        int m4 = firstInputMatrix[1][1]*(secInputMatrix[1][0] - secInputMatrix[0][0]);
        int m5 = (firstInputMatrix[0][0] + firstInputMatrix[1][1])*(secInputMatrix[0][0]+secInputMatrix[1][1]);
        int m6 =(firstInputMatrix[0][1] - firstInputMatrix[1][1])*(secInputMatrix[1][0] + secInputMatrix[1][1]);
        int m7 =(firstInputMatrix[0][0] - firstInputMatrix[1][0])*(secInputMatrix[0][0] + secInputMatrix[0][1]);
        resMatrix[0][0] = m5 + m4 - m2 + m6;
        resMatrix[0][1] = m1 + m2;
        resMatrix[1][0] = m3 + m4;
        resMatrix[1][1] = m1 + m5 - m3 - m7;
    }
    else{
		int len = n/2;
		int a11[len][len], a12[len][len], a21[len][len], a22[len][len];
		int b11[len][len], b12[len][len], b21[len][len], b22[len][len];
		int c11[len][len], c12[len][len], c21[len][len], c22[len][len];
		int p1[len][len], p2[len][len], p3[len][len], p4[len][len];
		int p5[len][len], p6[len][len], p7[len][len];
		int aMatrix[len][len], bMatrix[len][len];

		assignSubMatrixValues(len, n, a11, a12, a21, a22, firstInputMatrix);
        assignSubMatrixValues(len, n, b11, b12, b21, b22, secInputMatrix);
         
        // p1 = (a11+a22) * (b11+b22)
        add(len, b11, b22, bMatrix); 
        add(len, a11, a22, aMatrix ); 
        reduceMatrix(len, aMatrix, bMatrix, p1); 

        // p2 = (a21+a22) * (b11)
        add(len, a21, a22, aMatrix); 
        reduceMatrix(len, aMatrix, b11, p2); 

        // p3 = (a11) * (b12 - b22)
        sub(len, b12, b22, bMatrix); 
        reduceMatrix(len, a11, bMatrix, p3); 

        // p4 = (a22) * (b21 - b11)
        sub(len, b21, b11, bMatrix); 
        reduceMatrix(len, a22, bMatrix, p4); 

        // p5 = (a11+a12) * (b22)   
        add(len, a11, a12, aMatrix); 
        reduceMatrix(len, aMatrix, b22, p5); 

        // p6 = (a21-a11) * (b11+b12)
        sub(len, a21, a11, aMatrix); 
        add(len, b11, b12, bMatrix); 
        reduceMatrix(len, aMatrix, bMatrix, p6); 

        // p7 = (a12-a22) * (b21+b22)
        sub(len, a12, a22, aMatrix); 
        add(len, b21, b22, bMatrix); 
        reduceMatrix(len, aMatrix, bMatrix, p7); 

        // c12 = p3 + p5
        add(len, p3, p5, c12); 
        // c21 = p2 + p4
        add(len, p2, p4, c21); 

        // c11 = p1 + p4 - p5 + p7
        add(len, p1, p4, aMatrix); 
        add(len, aMatrix, p7, bMatrix);
        sub(len, bMatrix, p5, c11); 

        // c22 = p1 + p3 - p2 + p6
        add(len, p1, p3, aMatrix); 
        add(len, aMatrix, p6, bMatrix);
        sub(len, bMatrix, p2, c22); 
       
        int i,j;
        for (i = 0; i < len ; i++) {
            for (j = 0 ; j < len ; j++) {
                resMatrix[i][j] = c11[i][j];
                resMatrix[i][j + len] = c12[i][j];
                resMatrix[i + len][j] = c21[i][j];
                resMatrix[i + len][j + len] = c22[i][j];
            }
        }
	}
}

void strassenMulti(int n, int firstInputMatrix[][n], int secInputMatrix[][n], int resMatrix[][n]){
	int m = round_of_power2(n); 
	int i,j;
	int A[m][m], B[m][m], C[m][m];
	intiailizeArray(m, A);
	intiailizeArray(m, B);
	intiailizeArray(m, C);

    for( i=0; i<n; i++) {
        for ( j=0; j<n; j++) {
            A[i][j] = firstInputMatrix[i][j];
            B[i][j] = secInputMatrix[i][j];
        }
    }
	reduceMatrix(m, A, B, C);
	for( i=0; i<n; i++) {
        for ( j=0; j<n; j++) {
            resMatrix[i][j] = C[i][j];
        }
    }
}
int main()
{
	int n;
	printf("Enter the dimension of the matrix\n");
	scanf("%d",&n);
	int a[n][n], b[n][n], c[n][n];
	fillMatrix(n,a,b);
	printf("First Matrix\n");
	printMatrix(n,a);
	printf("Second Matrix\n");
	printMatrix(n,b);
	multiplyMatrix(n,a,b,c);
	printf("Normal matrix multiplication result\n");
	printMatrix(n, c);	
	printf("Strassen Multiplication result\n");
	strassenMulti(n,a,b,c);
	printMatrix(n,c);
	return 0;
}