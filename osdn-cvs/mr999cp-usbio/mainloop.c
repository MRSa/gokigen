#include <stdio.h>
#include <usb.h>
#include "io_usbio.h"

/*
 *
 */
int mainLoop(usb_dev_handle *udev, int key)
{
    char keyData;
    int ret;

    keyData = ((char) key & 0xff);

    ret = 0;
    switch (keyData)
    {

      case 'A':
      case 'a':
        /* GRIPPER : OPEN */
        output_to_usbio(udev, 0, (0xff - 1));
        break;

      case 'Z':
      case 'z':
        /* GRIPPER : CLOSE */
        output_to_usbio(udev, 0, (0xff - 2));
        break;

      case 'S':
      case 's':
        /* WRIST  : TURN LEFT */
        output_to_usbio(udev, 1, (0xff - 1));
        break;

      case 'X':
      case 'x':
        /* WRIST  : TURN RIGHT */
        output_to_usbio(udev, 1, (0xff - 2));
        break;

      case 'D':
      case 'd':
        /* ELBOW : UP  */
        output_to_usbio(udev, 0, (0xff - 8));
        break;

      case 'C':
      case 'c':
        /* ELBOW : DOWN  */
        output_to_usbio(udev, 0, (0xff - 4));
        break;

      case 'B':
      case 'b':
        /* BASE : TURN LEFT */
        output_to_usbio(udev, 0, (0xff - 16));
        break;

      case 'G':
      case 'g':
        /* BASE : TURN RIGHT */
        output_to_usbio(udev, 0, (0xff - 32));
        break;

      case 'F':
      case 'f':
        /* SHOULDER : UP  */
        output_to_usbio(udev, 0, (0xff - 64));
        break;

      case 'V':
      case 'v':
        /* SHOULDER : DOWN  */
        output_to_usbio(udev, 0, (0xff - 128));
        break;

      case '.':
      case ',':
        /* STOP */
        output_to_usbio(udev, 0, 0xff);
        output_to_usbio(udev, 1, 0xff);
        break;

      case 'q':
      case 'Q':
        /* EXIT  */
        ret = -1;
        break;

      default:
        /* STOP */
        output_to_usbio(udev, 0, 0xff);
        output_to_usbio(udev, 1, 0xff);
        break;
    }
    return (ret);
}
