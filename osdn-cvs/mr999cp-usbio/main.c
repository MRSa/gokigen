/*----------------------------*/
/* ROBOT ARM CONTROL PROGRAM  */
/*----------------------------*/
#include <stdio.h>
#include <usb.h>
#include <curses.h>
#include "io_usbio.h"

extern int mainLoop(usb_dev_handle *udev, int key);
void  showKeyHelp(void);

/********************************
 *
 *        MAIN   PROCEDURE
 *
 ********************************/
int main(int argc, char* argv[])
{
    int key;
    struct usb_device *dev = NULL;
    usb_dev_handle    *udev;

    /*-------------*/
    /* Device Open */
    /*-------------*/
    dev = find_morphy_usbio(dev);
    if (dev == NULL)
    {
        fprintf(stderr, "ERR>CANNOT FIND any Interface.\n");
        return (-1);
    }
    udev = open_morphy(dev);
    if (udev == NULL)
    {
        fprintf(stderr, "ERR>The interface was FAILED to open.\n");
        exit (2);
    }

    /*--------------------*/
    /*     MAIN  LOOP     */
    /*--------------------*/

    /* initialize libncurses */
    initscr();
    cbreak();
    noecho();

    showKeyHelp();
    do 
    {
        key = getch();
    } while (mainLoop(udev, key) >= 0);

    /* finish libncurses */
    endwin();

    /*---------------------*/
    /*   Stop ARM Motion   */
    /*---------------------*/
    output_to_usbio(udev, 0, 0xff);
    output_to_usbio(udev, 1, 0xff);

    /*---------------------*/
    /* Device Close & Exit */
    /*---------------------*/
    close_morphy(udev);

    return(0);
}


/*
 * 
 */
void  showKeyHelp(void)
{
    move(0, 0);
    printw("--------------");
    move(1, 0);
    printw(" MR-999CP CTL ");
    move(2, 0);
    printw("--------------");
    move(4, 0);
    printw("  <KEY>");
    move(5, 0);
    printw("    GRIPPER   :  'A':Open        'Z':CLOSE     ");
    move(6, 0);
    printw("    WRIST     :  'S':TURN RIGHT  'X':TURN LEFT ");
    move(7, 0);
    printw("    ELBOW     :  'D':UP          'C':DOWN      ");
    move(8, 0);
    printw("    SHOULDER  :  'F':UP          'V':DOWN      ");
    move(9, 0);
    printw("    BASE      :  'G':TURN RIGHT  'B':TURN LEFT ");
    move(11, 0);
    printw("    STOP      :  OTHER KEY");
    move(14, 0);
    printw("    EXIT PROG.:  'Q'");
    move(15, 0);

    return;
}
