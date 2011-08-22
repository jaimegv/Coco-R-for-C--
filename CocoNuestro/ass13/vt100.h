/***********************************************************************/
/*                                                                     */
/*                             ASS para LINUX                          */
/*                                                                     */
/***********************************************************************/
/*                                                                     */ 
/*    Fichero: vt100.h    (1.2)                                        */
/*                                                                     */
/*    Funcion: Portar el programa "ass" version 1.0 a LINUX            */
/*            Portabiliza las funciones no ANSI C incluidas en         */
/*            la version 1.0                                           */
/*                                                                     */ 
/*    Autores:   Francisco Malpartida Candel   (version 1.1)           */
/*               Miguel Angel Vicente Puente   (version 1.2)           */
/*                                                                     */
/***********************************************************************/ 

#ifndef _VIC_CODE_
#define _VIC_CODE_          
                
int XX,YY;
#define clreol()       puts("\033[K")
#define clrscr()       puts("\033[2J")
#define gotoxy(x,y)    printf("\033[%c%c;%c%cH",(y)/10 +'0',(y)%10 +'0',(x)/10 +'0',(x)%10 +'0')
#define lowvideo()     printf("\033[0m")
#define highvideo()    printf("\033[1m")
#define inverso()	printf("\033[7m")
#define parpadeo()	printf("\033[5m")
#define invisible()	printf("\033[8m")
#define cursorup(n)  fprintf(stdout,"\033[%c%cA",(n)/10 + '0',(n)%10 + '0')
#define cursordown(n)  fprintf(stdout,"\033[%c%cB",(n)/10 + '0',(n)%10 + '0')
#define cursorleft(n)  fprintf(stdout,"\033[%c%cD",(n)/10 + '0',(n)%10 + '0')
#define cursorright(n)  fprintf(stdout,"\033[%c%cC",(n)/10 + '0',(n)%10 + '0')
#define limpia() for(XX=1;XX<=80;XX++) for(YY=1;YY<=25;YY++) {gotoxy(XX,YY);printf(" \n");} gotoxy(1,1)
#define randomize() srand(time(NULL))
#define kbhit() 0
#define max(a,b)   (a>b) ? a : b
#define getch  vic_getch


#endif /*vt100.h*/


