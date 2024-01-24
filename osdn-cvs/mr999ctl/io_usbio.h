/*
 *
 */
struct usb_device *find_morphy_usbio(struct usb_device *dev);

struct usb_dev_handle *open_morphy(struct usb_device *dev);
void close_morphy(struct usb_dev_handle *udev);

void output_to_usbio(struct usb_dev_handle *udev, int PortNo, unsigned char OutData);
int input_from_usbio(struct usb_dev_handle *udev, int PortNo);

