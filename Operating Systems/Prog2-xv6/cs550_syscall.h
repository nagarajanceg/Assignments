#define SIZE 100
typedef struct _linear_table{
  int sum; 
  char* name;
} LT;

LT st[] = {
	{
	.name="aaICO",
	.sum = 413
	},
	{
	.name="RwwzQ",
	.sum = 523
	},
	{
	.name="vPlkH",
	.sum = 485
	},
	{
	.name="nAAkW",
	.sum = 434
	},
	{
	.name="V4XHy",
	.sum = 419
	},
	{
	.name="DIJ1J",
	.sum = 338
	},
	{
	.name="uby8E",
	.sum = 461
	},
	{
	.name="HrYmw",
	.sum = 503
	},
	{
	.name="It9GD",
	.sum = 385
	},
	{
	.name="kBqyL",
	.sum = 483
	},
	{
	.name="uFvqh",
	.sum = 522
	},
	{
	.name="kCOzo",
	.sum = 486
	},
	{
	.name="BdglP",
	.sum = 457
	},
	{
	.name="vsu9T",
	.sum = 491
	},
	{
	.name="GhENr",
	.sum = 436
	},
	{
	.name="cNlc2",
	.sum = 434
	},
	{
	.name="AUX47",
	.sum = 345
	},
	{
	.name="OJjUF",
	.sum = 414
	},
	{
	.name="rwbCB",
	.sum = 464
	},
	{
	.name="U63O6",
	.sum = 323
	},
	{
	.name="buYn9",
	.sum = 471
	},
	{
	.name="pvRfc",
	.sum = 513
	},
	{
	.name="IbIsf",
	.sum = 461
	},
	{
	.name="RH7FR",
	.sum = 361
	},
	{
	.name="u5Ncu",
	.sum = 464
	},
	{
	.name="vA3kF",
	.sum = 411
	},
	{
	.name="ZyJFv",
	.sum = 473
	},
	{
	.name="a_kuY",
	.sum = 505
	},
	{
	.name="DF2ak",
	.sum = 392
	},
	{
	.name="bifXB",
	.sum = 459
	},
	{
	.name="OpuAe",
	.sum = 474
	},
	{
	.name="DNUxN",
	.sum = 429
	},
	{
	.name="ancIB",
	.sum = 445
	},
	{
	.name="fiuCL",
	.sum = 467
	},
	{
	.name="f85c4",
	.sum = 362
	},
	{
	.name="pKQyj",
	.sum = 495
	},
	{
	.name="sPh9f",
	.sum = 458
	},
	{
	.name="aSdV9",
	.sum = 423
	},
	{
	.name="hEwee",
	.sum = 494
	},
	{
	.name="m7xSo",
	.sum = 478
	},
	{
	.name="lpg5S",
	.sum = 459
	},
	{
	.name="J7GF1",
	.sum = 319
	},
	{
	.name="GYEGy",
	.sum = 421
	},
	{
	.name="UHuoA",
	.sum = 450
	},
	{
	.name="lfawm",
	.sum = 535
	},
	{
	.name="SkxVQ",
	.sum = 477
	},
	{
	.name="m7dk_",
	.sum = 466
	},
	{
	.name="C2FTH",
	.sum = 343
	},
	{
	.name="ZFvwG",
	.sum = 468
	},
	{
	.name="xMznMiMbd2ASBAopjUBD",
	.sum = 1763
	}
};
typedef struct _hash_map
{
	char* name;
	int sum;
	int key;
	struct _hash_map* next;
}HT;
void _hash_func(char*);
void put(char*, int, int);
void initialize();
int getdata(char* name, int key);
int calculate_sum(char* value);


