#include <stdio.h>
#include <string.h>
#include <usb.h>

/*------------------------------------------------------------
 *  USB-IO Accssessor  (using libusb)
 *------------------------------------------------------------
 */

/**********  USB-IO  USB ID DEFINITIONs **********/
 #define USB_VENDOR  0x12ed
 #define USB_PRODUCT 0x1003


/*-----------------------*/
/* Initialize USB device */
/*-----------------------*/
struct usb_bus *init_morphy()
{
    usb_init();
    usb_find_busses();
    usb_find_devices();

    return(usb_get_busses());
}

/*------------------------------------*/
/*  Check USB Vendor ID & Product ID  */
/*------------------------------------*/
int check_morphy_usbio(struct usb_device *dev)
{
    if ((dev->descriptor.idVendor == USB_VENDOR)&&(dev->descriptor.idProduct == USB_PRODUCT))
    {
        /*  find a USB-IO device */
        return (1);
    }
    return (0);
}


/*-----------------------*/
/*   Find Morphy-USBIO   */
/*-----------------------*/
struct usb_device *find_morphy_usbio(struct usb_device *dev)
{
    struct usb_bus *bus;

    bus = init_morphy();
    for (; bus != NULL; bus = bus->next)
    {
        for (dev = bus->devices; dev != NULL; dev = dev->next)
        {
            if (check_morphy_usbio(dev) != 0)
            {
                return (dev);
            }
        }
    }
    return (NULL);
}

/*---------------------*/
/*     Open USB-IO     */
/*---------------------*/
struct usb_dev_handle *open_morphy(struct usb_device *dev)
{
    struct usb_dev_handle *udev = NULL;

    udev = usb_open(dev);
    if (udev == NULL)
    {
        fprintf(stderr, "ERR>usb_open() (%s)\n", usb_strerror());
        exit(1);
    }

    if (usb_set_configuration(udev,dev->config->bConfigurationValue)  < 0)
    {
        if (usb_detach_kernel_driver_np(udev, dev->config->interface->altsetting->bInterfaceNumber) < 0)
        {
            fprintf(stderr, "usb_set_configuration Error.\n");
            fprintf(stderr, "usb_detach_kernel_driver_np Error.(%s)\n",usb_strerror());
        }
    }

    if (usb_claim_interface(udev, dev->config->interface->altsetting->bInterfaceNumber) < 0)
    {
        if (usb_detach_kernel_driver_np(udev, dev->config->interface->altsetting->bInterfaceNumber) < 0)
        {
            fprintf(stderr, "usb_claim_interface Error.\n");
            fprintf(stderr, "usb_detach_kernel_driver_np Error.(%s)\n",usb_strerror());
        }
    }

    if (usb_claim_interface(udev, dev->config->interface->altsetting->bInterfaceNumber) < 0)
    {
        fprintf(stderr, "usb_claim_interface Error.(%s)\n",usb_strerror());
    }

    return(udev);
}

/*---------------------*/
/*     Close USB-IO    */
/*---------------------*/
void close_morphy(struct usb_dev_handle *udev)
{
    if (usb_close(udev) < 0)
    {
        fprintf(stderr, "ERR>usb_close() (%s)\n",usb_strerror());
    }
}

/*---------------------*/
/*  Output to USB-IO   */
/*---------------------*/
void output_to_usbio(struct usb_dev_handle *udev, int PortNo, unsigned char OutData)
{
    unsigned char ctl[8];

    ctl[0] = 0x01+(PortNo&1);
    ctl[1] = OutData;
    ctl[2] = ctl[3] = ctl[4] = ctl[5] = ctl[6] = ctl[7] = 0;
    usb_control_msg(udev, 0x21, 0x09, 0x00, 0x00, ctl, 8, 5000);
}

/*---------------------*/
/*  Input from USB-IO  */
/*---------------------*/
int input_from_usbio(struct usb_dev_handle *udev, int PortNo)
{
    int i,ret;
    unsigned char ctl[8];
    unsigned char indat[8];
    static int n = 0;

    n++;

    /* CMD:Read */
    ctl[0] = 0x03+(PortNo&1);
    ctl[1] = ctl[2] = ctl[3] = ctl[4] = ctl[5] = ctl[6] = 0;
    ctl[7] = n;

    ret=usb_control_msg(udev,0x21,0x09,0x00,0x00,ctl,8,5000);
    if (ret < 0)
    {
        fprintf(stderr, "Read-CmdError\n");
        return (-1);
    }

    i = 0;
    do
    { 
        ret = usb_interrupt_read(udev, 1, indat, 8, 5000);
        if (ret < 0)
        {
            fprintf(stderr, "Int-Read Error\n");
            return (-2);
        }

        i++;
        if (i > 100)
        { 
            fprintf(stderr, "Int-Read Error\n");
            return (-3);
        }
    } while (ctl[7] != indat[7]);

    ret = usb_interrupt_read(udev, 1, indat, 8, 5000);

    return (indat[1]);
}
