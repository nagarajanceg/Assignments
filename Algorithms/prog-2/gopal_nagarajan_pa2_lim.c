#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#define UL unsigned long long int
#define THRESHOLD 3
#define MAX 400

void multiply(int , UL a[],UL b[], UL c[]);
int max(UL a, UL b ){
	return (a>b)?a:b;
}
void printArray(UL* arr, int n){
	int i;
	for(i=0; i<n; i++){
		printf("%llu",arr[i] );
	}
	printf("\n");
}
/*Generate n digit random numbers*/
UL generateInteger(int n, UL* arr){
	// srand(time(NULL));
	UL x;
	int i;
	arr[0] = rand() % 9 + 1;
	for(i=1;i<n;i++){
		arr[i] = rand() % 10;
		// printf("v%lu ",arr[i]);
	}
	return x;
}
void clear_numbers(int no_of_digits, UL * number){
  int i;
  for(i = 0; i < no_of_digits; i ++){
    number[i] = 0;
  }
}
void splitArray(UL* u,int start, int m, UL* res){
	int i,k=0;
	for(i=start;i<m;i++){
		res[k] = u[i];
		k++;
	}
}
void reverseArray(int start, int end, UL* res){
	int temp;
	while(start < end){
		temp = res[start];
		res[start] = res[end];
		res[end] = temp;
		start++;
		end--;
	}
}
void sumArray(int m, UL* a, UL* b, UL* res){
	int i, sum=0, carry=0,k=0;

	for(i=m-1;i>=0;i--){
		sum = a[i]+b[i]+carry;
		carry = sum/10;
		res[k] = sum%10;
		k++;
	}
		res[k] = carry;
		k++;
	reverseArray(0,k-1,res);
}


void subArray(int n, UL* r, UL* ares, UL* subRes){
	// UL ares[n+1],
	UL k=0;
	int i,up,down;
	clear_numbers(n+1, subRes);
	for(i=n-1;i>=0;i-- ){
		up = r[i];
		down = ares[i];
		if(up < down){
			if(i==0){
				// r[i] = r[i]-1;
			}else{
				up = up+10;
				r[i-1] = r[i-1]-1;	
			}
		}
		subRes[k++] = up - down;
	}
	/*fallback to handle second number has more no digits*/
	if(ares[0] >0){
		subRes[k++] = ares[0];
	}
	reverseArray(0,k-1,subRes);
}
void addZeroToFront(int oldSize, UL *new_list, int newSize){
	int i, inserted;
	for(i = newSize - 1, inserted = 0; i >= 0; i --, inserted++){
		if(inserted <= oldSize - 1){
			new_list[i] = new_list[i - 1];
		}else{
			new_list[i] = 0;
		}
	}
}

/*Divide the number by 3 parts algorithm*/
void largeMultipleModified(int n, UL* u, UL* v, UL* result){
	// printf("u = %lu v =%lu",u,v);
	// UL x,y,w,z,s,temp,r,l,a,b,c,d;
	int  m;
	
	// printf("no of digits = %d\n",n);
	/* Multiply happens when the numbers reaches threshold at recursion*/
	if(n <= 3){
		multiply(n-1,u,v,result);
	}else{
      	int i, carry = 0, sum;
		m = floor(n/3);
		int md = 2 * m;
		UL x[m],y[m],s[m];
		UL w[m], z[m],l[m];
		UL xys[m+1], wzl[m + 1];
		UL r[md + 2], a[md];
		UL xl[md], sw[md], b[md + 1];
		UL sz[md], yl[md], c[md + 1];
		UL d[md];
		UL abcd[md+2], ad[md + 1];
		UL rabcd[md + 2];
		UL A[2 * n], B[2 * n], RABCD[2 * n], C[2 * n], D[2* n];

        UL final_result[2 * n + 1];
        
        clear_numbers(m, x);
        clear_numbers(m, y);
        clear_numbers(n, s);
        clear_numbers(m, l);
        clear_numbers(m, w);
        clear_numbers(m, z);
        
        clear_numbers(m+1, xys);
        clear_numbers(m+1, wzl);
        clear_numbers(md + 2, r);
        clear_numbers(md, a);
        clear_numbers(md, xl);
        clear_numbers(md, sw);
        clear_numbers(md + 1, b);
        clear_numbers(md, sz);
        clear_numbers(md, yl);
        clear_numbers(md + 1, c);
        clear_numbers(md, d);
        clear_numbers(md + 2, abcd);
        clear_numbers(md + 1, ad);
        clear_numbers(md + 2, rabcd);
        clear_numbers(2 * n, A);
        clear_numbers(2 * n, B);
        clear_numbers(2 * n, RABCD);
        clear_numbers(2 * n, C);
        clear_numbers(2 * n, D);
        clear_numbers(2 * n + 1, final_result);

		splitArray(u,0,m,x);
		splitArray(u,m,md,s);
		splitArray(u,md,n,y);

      	splitArray(v,0,m,w);
		splitArray(v,m,2*m,l);
		splitArray(v,2*m,n,z);



      	for(i = m - 1; i >=0; i --){
      		sum = x[i] + s[i] + y[i] + carry;
      		carry = sum / 10;
      		xys[i+1]= sum % 10;
      	}
      	xys[0] = carry;

      	for(i = m - 1, carry = 0; i >=0; i --){
      		sum = w[i] + z[i] + l[i] + carry;
      		carry = sum / 10;
      		wzl[i+1]= sum % 10;
      	}
      	wzl[0] = carry;

      	largeMultipleModified(m + 1, xys, wzl, r);

      	largeMultipleModified(m, x, w, a);
		
      	largeMultipleModified(m, x, l, xl);
      	
      	largeMultipleModified(m, s, w, sw);

      	sumArray(md, xl, sw, b);

		largeMultipleModified(m, s, z, sz);

      	largeMultipleModified(m, y, l, yl);

      	sumArray(md, sz, yl, c);

      	largeMultipleModified(m, y, z, d);

      	sumArray(md,a,d,ad);

      	for(i = md, carry = 0; i >=0; i --){
      		sum = b[i] + c[i] + ad[i] + carry;
      		carry = sum / 10;
      		abcd[i+1] = sum % 10;
      	}
      	abcd[0] = carry;
		
      	subArray(md + 2, r , abcd, rabcd);

      	for(i = 0; i < 2 * n; i ++){
      		if(i < md){
      			A[i] = a[i];
      		}else{
      			A[i] = 0;
      		}
      	}

      	for(i = 0; i < 2 * n; i ++){
      		if(i < m - 1 || i >= n){
      			B[i] = 0;
      		}else{
      			B[i] = b[i + 1 - m];
      		}
      	}

      	for(i = 0; i < 2 * n; i ++){
      		if(i < md - 2 || i >= n + m){
      			RABCD[i] = 0;
      		}else{
      			RABCD[i] = rabcd[i + 2 - md];
      		}
      	}

      	for(i = 0; i < 2 * n; i ++){
      		if(i < md + m - 1 || i >= n + md){
      			C[i] = 0;
      		}else{
      			C[i] = c[i + 1 - md - m];
      		}
      	}

      	for(i = 0; i < 2 * n; i ++){
      		if(i >= 2 * n - md){
      			D[i] = d[i - 2 * n + md];
      		}else{
      			D[i] = 0;
      		}
      	}
      	for(i = 2 * n, carry = 0; i >= 0; i --){
      		sum = A[i] + B[i] + RABCD[i] + C[i] + D[i] + carry;
      		carry = sum / 10;
      		result[i] = sum % 10;
      	}
	}
}

/*Divide the number by 2 parts algorithm*/
void largeMultiple(int n, UL* u, UL* v,UL* result ){
	int m;
	UL* ures[n],*vres[n];

	if(n <=3){
		multiply(n-1,u,v,result);
	}else{
		m = floor(n/2);
		UL x[m],y[m],w[m],z[m];
		UL xy[m+1],wz[m+1];
		UL p[n], q[n], r[n+2];
		UL tempRes[n+2],tempRes1[n+2];
		UL tempRes3[2 * n],tempRes4[2 * n], tempRes5[2 * n], tempRes6[2 * n + 1];
		UL ans1[n+m], ans2[n+m];
		UL final_result[2 * n + 1];
		clear_numbers(n+2, tempRes1);
		clear_numbers(n+2, tempRes);
		clear_numbers(m+1, xy);
		clear_numbers(m+1, wz);
		clear_numbers(m, x);
		clear_numbers(m, y);
		clear_numbers(m, w);
		clear_numbers(m, z);
		clear_numbers(n, p);
		clear_numbers(n, q);
		clear_numbers(n + 2, r);
		clear_numbers(2 * n, tempRes3);
		clear_numbers(2 * n, tempRes4);
		clear_numbers(2 * n, tempRes5);
		clear_numbers(2 * n + 1, tempRes6);
		clear_numbers(2 * n + 1, final_result);

		splitArray(u,0,m,x);
		splitArray(u,m,n,y);
		splitArray(v,0,m,w);
		splitArray(v,m,n,z);
		
		sumArray(m,x,y,xy);
		
		
		sumArray(m,w,z,wz);
		
		
		largeMultiple(m,x,w,p);
		
		
		largeMultiple(m,y,z,q);
		

		largeMultiple(m+1,xy,wz,r);
		
		
		sumArray(n,p,q,tempRes);
		
		addZeroToFront(n+1, tempRes, n+2);
		subArray(n + 2,r,tempRes,tempRes1);
		int i ;
		for(i = 0; i < 2 * n; i ++){
			if(i < n){
				tempRes3[i] = p[i];
			}else{
				tempRes3[i] = 0;
			}
		}
		for(i = 0; i < 2 * n; i ++){
			if( i < m - 2 || i >= m + n){
				tempRes4[i] = 0;
			}else{
				tempRes4[i] = tempRes1[i + 2 - m];
			}
		}
		for(i = 0; i < 2 * n; i ++){
			if(i < n){
				tempRes5[i] = 0;
			}else{
				tempRes5[i] = q[i - n];
			}
		}
		int carry = 0, sum;
		for(i = 2 * n - 1; i >= 0; i --){
			sum = tempRes3[i] + tempRes4[i] + tempRes5[i] + carry;
			carry = sum/10;
			result[i] = sum%10;
		}
	}
}


void multiply(int n, UL a[],UL b[], UL mul[]){
    int c[MAX];
    int temp[MAX];
    long int r=0;
    long sum = 0;
    int i,j,k=0,x=0,y;
    for(i=n;i>=0;i--){
         r=0;
         for(j=n;j>=0;j--){
             temp[k++] = (b[i]*a[j] + r)%10;
             r = (b[i]*a[j]+r)/10;
         }
         temp[k++] = r;
         x++;
         for(y = 0;y<x;y++){
             temp[k++] = 0;
         }
    }
    k=0;r=0;
    for(i=0;i< 2*n+2;i++){
         sum =0;
         y=0;
         for(j=1;j<=n+1;j++){
             if(i <= n+j){
                 sum = sum + temp[y+i];
             }
             y += j + n + 1;
         }
         c[k++] = (sum+r) %10;
         r = (sum+r)/10;
    }
    c[k] = r;
    j=0;
    // int flag =0;
    for(i=k-1;i>=0;i--){
    	mul[j++]=c[i];
    }
}
int main(){
	int n;
	UL res;
	srand(time(NULL));
	printf("Enter the numbers of digits \n");
	scanf("%d",&n);
	int sz = 2*n;
	UL arr[n], brr[n] , crr[sz];
	UL  grr[sz];
	generateInteger(n,arr);
	generateInteger(n,brr);
	printf("Input Arrays genereated in terms :\n");
	printArray(arr, n);
	printArray(brr, n);
	
	largeMultiple(n,arr,brr,crr);
	printf("\nThe final output divided by 2 equal parts : ");
	printArray(crr, 2 * n);

	largeMultipleModified(n,arr,brr,grr);
	printf("\nThe final output divided by 3 equal parts: ");
	printArray(grr, 2 * n);

	return 0;
}