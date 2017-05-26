#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void lcs(char* a, char* b){
	int m,n,i,j,pos;
	m = strlen(a);
	n = strlen(b);
	printf("string1 = %s \nstring2 = %s\n",a,b);

	int lcsRes[m+1][n+1];
	for(i=0; i <= m; i++){
		for(j=0; j<=n; j++){
			/*1st row and column to fill zeros*/
			if(j==0 || i==0){
				lcsRes[i][j] = 0;
			}else if(a[i-1] == b[j-1]){
				/*Whenever two strings matches add 1 with diagonal value to current position*/
				lcsRes[i][j] = lcsRes[i-1][j-1] + 1;
			}else{
				if(lcsRes[i-1][j] >= lcsRes[i][j-1]){
					lcsRes[i][j] = lcsRes[i-1][j];
				}else{
					lcsRes[i][j] = lcsRes[i][j-1];
				}
			}
		}
	}
	
	pos = lcsRes[m][n];
	char result[pos+1];
	result[pos] ='\0';

	/* store the lcs in the result based on the values available in lcs computed matrix */
	for(i =m,j=n; i>0 && j>0;){
		if(a[i-1] == b[j-1]){
			pos = pos -1;
			result[pos] = a[i-1];
			i = i-1;
			j = j-1;
		}else if(lcsRes[i-1][j] > lcsRes[i][j-1]){
			i = i-1;
		}else{
			j = j-1;
		}
	}
	printf("LCS = %s\n",result );
}

int main(int argc, char* argv[]){
	if(argc < 3){
		printf(" Invalid Arguments ,run output file with two strings\n");
		return 0;
	}
	/* obtain the strings from arguement */
	lcs(argv[1],argv[2]);
	return 0;
}