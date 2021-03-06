struct stat;
struct rtcdate;

// system calls
int fork(void);
int exit(void) __attribute__((noreturn));
int wait(void);
int pipe(int*);
int write(int, void*, int);
int read(int, void*, int);
int close(int);
int kill(int);
int exec(char*, char**);
int open(char*, int);
int mknod(char*, short, short);
int unlink(char*);
int fstat(int fd, struct stat*);
int link(char*, char*);
int mkdir(char*);
int chdir(char*);
int dup(int);
int getpid(void);
char* sbrk(int);
int sleep(int);
int uptime(void);
int date(struct rtcdate*);

//# Linear search syscall
int aaICO(void);
int RwwzQ(void);
int vPlkH(void);
int nAAkW(void);
int V4XHy(void);
int DIJ1J(void);
int uby8E(void);
int HrYmw(void);
int It9GD(void);
int kBqyL(void);
int uFvqh(void);
int kCOzo(void);
int BdglP(void);
int vsu9T(void);
int GhENr(void);
int cNlc2(void);
int AUX47(void);
int OJjUF(void);
int rwbCB(void);
int U63O6(void);
int buYn9(void);
int pvRfc(void);
int IbIsf(void);
int RH7FR(void);
int u5Ncu(void);
int vA3kF(void);
int ZyJFv(void);
int a_kuY(void);
int DF2ak(void);
int bifXB(void);
int OpuAe(void);
int DNUxN(void);
int ancIB(void);
int fiuCL(void);
int f85c4(void);
int pKQyj(void);
int sPh9f(void);
int aSdV9(void);
int hEwee(void);
int m7xSo(void);
int lpg5S(void);
int J7GF1(void);
int GYEGy(void);
int UHuoA(void);
int lfawm(void);
int SkxVQ(void);
int m7dk_(void);
int C2FTH(void);
int SkxVQ(void);
int xMznMiMbd2ASBAopjUBD(void);


//#hashing syscall
int ajJ2rUS36Ds7gCvYhWvRMQ4cM(void);
int lcQQ6(void);
int tG0cd(void);
int BQaye(void);
int vRL61(void);
int BfPAS(void);
int VWqcU(void);
int rNV_x(void);
int m9TVP(void);
int dN3rB(void);
int NEorw(void);
int iE45d(void);
int VFGu_(void);
int zqf7e(void);
int Ayr5H(void);
int y7dVW(void);
int RhaR2(void);
int GUT_3(void);
int mtwGL(void);
int Rz1qA(void);
int BDslj(void);
int DYcHF(void);
int XVI4N(void);
int RknTi(void);
int eraAb(void);
int K7plu(void);
int UPjQy(void);
int uFjK1(void);
int E8Jj9(void);
int viJpE(void);
int y8hmu(void);
int GhPNY(void);
int mEymb(void);
int UAi7z(void);
int g3fke(void);
int spiXC(void);
int sCY6P(void);
int lQ5p2(void);
int yFLZV(void);
int y7NBZ(void);
int JS_81(void);
int pHGgA(void);
int D3BT2(void);
int LYSUb(void);
int UHDeS(void);
int zQQm5(void);
int T4IiC(void);
int ZnCtG(void);
int TBDYx(void);
int k_jNM(void);

// ulib.c
int stat(char*, struct stat*);
char* strcpy(char*, char*);
void *memmove(void*, void*, int);
char* strchr(const char*, char c);
int strcmp(const char*, const char*);
void printf(int, char*, ...);
char* gets(char*, int max);
uint strlen(char*);
void* memset(void*, int, uint);
void* malloc(uint);
void free(void*);
int atoi(const char*);
