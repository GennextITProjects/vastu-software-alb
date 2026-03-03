Add-Type -AssemblyName System.Drawing
$img = [System.Drawing.Image]::FromFile('D:\Gennext\vastu-software-alb\src\main\resources\icons\Master_Vastu_Logo.jpg')
$icon = new-object System.Drawing.Bitmap($img, 256, 256)
$icon.Save('D:\Gennext\vastu-software-alb\src\main\resources\icons\app_icon.ico', [System.Drawing.Imaging.ImageFormat]::Icon)
