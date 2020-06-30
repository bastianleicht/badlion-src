package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface WinUser extends StdCallLibrary, WinDef {
   WinDef.HWND HWND_BROADCAST = new WinDef.HWND(Pointer.createConstant('\uffff'));
   int FLASHW_STOP = 0;
   int FLASHW_CAPTION = 1;
   int FLASHW_TRAY = 2;
   int FLASHW_ALL = 3;
   int FLASHW_TIMER = 4;
   int FLASHW_TIMERNOFG = 12;
   int IMAGE_BITMAP = 0;
   int IMAGE_ICON = 1;
   int IMAGE_CURSOR = 2;
   int IMAGE_ENHMETAFILE = 3;
   int LR_DEFAULTCOLOR = 0;
   int LR_MONOCHROME = 1;
   int LR_COLOR = 2;
   int LR_COPYRETURNORG = 4;
   int LR_COPYDELETEORG = 8;
   int LR_LOADFROMFILE = 16;
   int LR_LOADTRANSPARENT = 32;
   int LR_DEFAULTSIZE = 64;
   int LR_VGACOLOR = 128;
   int LR_LOADMAP3DCOLORS = 4096;
   int LR_CREATEDIBSECTION = 8192;
   int LR_COPYFROMRESOURCE = 16384;
   int LR_SHARED = 32768;
   int GWL_EXSTYLE = -20;
   int GWL_STYLE = -16;
   int GWL_WNDPROC = -4;
   int GWL_HINSTANCE = -6;
   int GWL_ID = -12;
   int GWL_USERDATA = -21;
   int DWL_DLGPROC = 4;
   int DWL_MSGRESULT = 0;
   int DWL_USER = 8;
   int WS_MAXIMIZE = 16777216;
   int WS_VISIBLE = 268435456;
   int WS_MINIMIZE = 536870912;
   int WS_CHILD = 1073741824;
   int WS_POPUP = Integer.MIN_VALUE;
   int WS_EX_COMPOSITED = 536870912;
   int WS_EX_LAYERED = 524288;
   int WS_EX_TRANSPARENT = 32;
   int LWA_COLORKEY = 1;
   int LWA_ALPHA = 2;
   int ULW_COLORKEY = 1;
   int ULW_ALPHA = 2;
   int ULW_OPAQUE = 4;
   int AC_SRC_OVER = 0;
   int AC_SRC_ALPHA = 1;
   int AC_SRC_NO_PREMULT_ALPHA = 1;
   int AC_SRC_NO_ALPHA = 2;
   int VK_SHIFT = 16;
   int VK_LSHIFT = 160;
   int VK_RSHIFT = 161;
   int VK_CONTROL = 17;
   int VK_LCONTROL = 162;
   int VK_RCONTROL = 163;
   int VK_MENU = 18;
   int VK_LMENU = 164;
   int VK_RMENU = 165;
   int MOD_ALT = 1;
   int MOD_CONTROL = 2;
   int MOD_NOREPEAT = 16384;
   int MOD_SHIFT = 4;
   int MOD_WIN = 8;
   int WH_KEYBOARD = 2;
   int WH_MOUSE = 7;
   int WH_KEYBOARD_LL = 13;
   int WH_MOUSE_LL = 14;
   int WM_PAINT = 15;
   int WM_CLOSE = 16;
   int WM_QUIT = 18;
   int WM_SHOWWINDOW = 24;
   int WM_DRAWITEM = 43;
   int WM_KEYDOWN = 256;
   int WM_CHAR = 258;
   int WM_SYSCOMMAND = 274;
   int WM_MDIMAXIMIZE = 549;
   int WM_HOTKEY = 786;
   int WM_KEYUP = 257;
   int WM_SYSKEYDOWN = 260;
   int WM_SYSKEYUP = 261;
   int SM_CXSCREEN = 0;
   int SM_CYSCREEN = 1;
   int SM_CXVSCROLL = 2;
   int SM_CYHSCROLL = 3;
   int SM_CYCAPTION = 4;
   int SM_CXBORDER = 5;
   int SM_CYBORDER = 6;
   int SM_CXDLGFRAME = 7;
   int SM_CYDLGFRAME = 8;
   int SM_CYVTHUMB = 9;
   int SM_CXHTHUMB = 10;
   int SM_CXICON = 11;
   int SM_CYICON = 12;
   int SM_CXCURSOR = 13;
   int SM_CYCURSOR = 14;
   int SM_CYMENU = 15;
   int SM_CXFULLSCREEN = 16;
   int SM_CYFULLSCREEN = 17;
   int SM_CYKANJIWINDOW = 18;
   int SM_MOUSEPRESENT = 19;
   int SM_CYVSCROLL = 20;
   int SM_CXHSCROLL = 21;
   int SM_DEBUG = 22;
   int SM_SWAPBUTTON = 23;
   int SM_RESERVED1 = 24;
   int SM_RESERVED2 = 25;
   int SM_RESERVED3 = 26;
   int SM_RESERVED4 = 27;
   int SM_CXMIN = 28;
   int SM_CYMIN = 29;
   int SM_CXSIZE = 30;
   int SM_CYSIZE = 31;
   int SM_CXFRAME = 32;
   int SM_CYFRAME = 33;
   int SM_CXMINTRACK = 34;
   int SM_CYMINTRACK = 35;
   int SM_CXDOUBLECLK = 36;
   int SM_CYDOUBLECLK = 37;
   int SM_CXICONSPACING = 38;
   int SM_CYICONSPACING = 39;
   int SM_MENUDROPALIGNMENT = 40;
   int SM_PENWINDOWS = 41;
   int SM_DBCSENABLED = 42;
   int SM_CMOUSEBUTTONS = 43;
   int SM_CXFIXEDFRAME = 7;
   int SM_CYFIXEDFRAME = 8;
   int SM_CXSIZEFRAME = 32;
   int SM_CYSIZEFRAME = 33;
   int SM_SECURE = 44;
   int SM_CXEDGE = 45;
   int SM_CYEDGE = 46;
   int SM_CXMINSPACING = 47;
   int SM_CYMINSPACING = 48;
   int SM_CXSMICON = 49;
   int SM_CYSMICON = 50;
   int SM_CYSMCAPTION = 51;
   int SM_CXSMSIZE = 52;
   int SM_CYSMSIZE = 53;
   int SM_CXMENUSIZE = 54;
   int SM_CYMENUSIZE = 55;
   int SM_ARRANGE = 56;
   int SM_CXMINIMIZED = 57;
   int SM_CYMINIMIZED = 58;
   int SM_CXMAXTRACK = 59;
   int SM_CYMAXTRACK = 60;
   int SM_CXMAXIMIZED = 61;
   int SM_CYMAXIMIZED = 62;
   int SM_NETWORK = 63;
   int SM_CLEANBOOT = 67;
   int SM_CXDRAG = 68;
   int SM_CYDRAG = 69;
   int SM_SHOWSOUNDS = 70;
   int SM_CXMENUCHECK = 71;
   int SM_CYMENUCHECK = 72;
   int SM_SLOWMACHINE = 73;
   int SM_MIDEASTENABLED = 74;
   int SM_MOUSEWHEELPRESENT = 75;
   int SM_XVIRTUALSCREEN = 76;
   int SM_YVIRTUALSCREEN = 77;
   int SM_CXVIRTUALSCREEN = 78;
   int SM_CYVIRTUALSCREEN = 79;
   int SM_CMONITORS = 80;
   int SM_SAMEDISPLAYFORMAT = 81;
   int SM_IMMENABLED = 82;
   int SM_CXFOCUSBORDER = 83;
   int SM_CYFOCUSBORDER = 84;
   int SM_TABLETPC = 86;
   int SM_MEDIACENTER = 87;
   int SM_STARTER = 88;
   int SM_SERVERR2 = 89;
   int SM_MOUSEHORIZONTALWHEELPRESENT = 91;
   int SM_CXPADDEDBORDER = 92;
   int SM_REMOTESESSION = 4096;
   int SM_SHUTTINGDOWN = 8192;
   int SM_REMOTECONTROL = 8193;
   int SM_CARETBLINKINGENABLED = 8194;
   int SW_HIDE = 0;
   int SW_SHOWNORMAL = 1;
   int SW_NORMAL = 1;
   int SW_SHOWMINIMIZED = 2;
   int SW_SHOWMAXIMIZED = 3;
   int SW_MAXIMIZE = 3;
   int SW_SHOWNOACTIVATE = 4;
   int SW_SHOW = 5;
   int SW_MINIMIZE = 6;
   int SW_SHOWMINNOACTIVE = 7;
   int SW_SHOWNA = 8;
   int SW_RESTORE = 9;
   int SW_SHOWDEFAULT = 10;
   int SW_FORCEMINIMIZE = 11;
   int SW_MAX = 11;
   int RDW_INVALIDATE = 1;
   int RDW_INTERNALPAINT = 2;
   int RDW_ERASE = 4;
   int RDW_VALIDATE = 8;
   int RDW_NOINTERNALPAINT = 16;
   int RDW_NOERASE = 32;
   int RDW_NOCHILDREN = 64;
   int RDW_ALLCHILDREN = 128;
   int RDW_UPDATENOW = 256;
   int RDW_ERASENOW = 512;
   int RDW_FRAME = 1024;
   int RDW_NOFRAME = 2048;
   int GW_HWNDFIRST = 0;
   int GW_HWNDLAST = 1;
   int GW_HWNDNEXT = 2;
   int GW_HWNDPREV = 3;
   int GW_OWNER = 4;
   int GW_CHILD = 5;
   int GW_ENABLEDPOPUP = 6;
   int SWP_NOZORDER = 4;
   int SC_MINIMIZE = 61472;
   int SC_MAXIMIZE = 61488;

   public static class BLENDFUNCTION extends Structure {
      public byte BlendOp = 0;
      public byte BlendFlags = 0;
      public byte SourceConstantAlpha;
      public byte AlphaFormat;
   }

   public static class FLASHWINFO extends Structure {
      public int cbSize;
      public WinNT.HANDLE hWnd;
      public int dwFlags;
      public int uCount;
      public int dwTimeout;
   }

   public static class GUITHREADINFO extends Structure {
      public int cbSize = this.size();
      public int flags;
      public WinDef.HWND hwndActive;
      public WinDef.HWND hwndFocus;
      public WinDef.HWND hwndCapture;
      public WinDef.HWND hwndMenuOwner;
      public WinDef.HWND hwndMoveSize;
      public WinDef.HWND hwndCaret;
      public WinDef.RECT rcCaret;
   }

   public static class HARDWAREINPUT extends Structure {
      public WinDef.DWORD uMsg;
      public WinDef.WORD wParamL;
      public WinDef.WORD wParamH;

      public HARDWAREINPUT() {
      }

      public HARDWAREINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends WinUser.HARDWAREINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   public static class HHOOK extends WinNT.HANDLE {
   }

   public interface HOOKPROC extends StdCallLibrary.StdCallCallback {
   }

   public static class INPUT extends Structure {
      public static final int INPUT_MOUSE = 0;
      public static final int INPUT_KEYBOARD = 1;
      public static final int INPUT_HARDWARE = 2;
      public WinDef.DWORD type;
      public WinUser.INPUT.INPUT_UNION input = new WinUser.INPUT.INPUT_UNION();

      public INPUT() {
      }

      public INPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends WinUser.INPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }

      public static class INPUT_UNION extends Union {
         public WinUser.MOUSEINPUT mi;
         public WinUser.KEYBDINPUT ki;
         public WinUser.HARDWAREINPUT hi;

         public INPUT_UNION() {
         }

         public INPUT_UNION(Pointer memory) {
            super(memory);
            this.read();
         }
      }
   }

   public static class KBDLLHOOKSTRUCT extends Structure {
      public int vkCode;
      public int scanCode;
      public int flags;
      public int time;
      public BaseTSD.ULONG_PTR dwExtraInfo;
   }

   public static class KEYBDINPUT extends Structure {
      public static final int KEYEVENTF_EXTENDEDKEY = 1;
      public static final int KEYEVENTF_KEYUP = 2;
      public static final int KEYEVENTF_UNICODE = 4;
      public static final int KEYEVENTF_SCANCODE = 8;
      public WinDef.WORD wVk;
      public WinDef.WORD wScan;
      public WinDef.DWORD dwFlags;
      public WinDef.DWORD time;
      public BaseTSD.ULONG_PTR dwExtraInfo;

      public KEYBDINPUT() {
      }

      public KEYBDINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends WinUser.KEYBDINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   public interface LowLevelKeyboardProc extends WinUser.HOOKPROC {
      WinDef.LRESULT callback(int var1, WinDef.WPARAM var2, WinUser.KBDLLHOOKSTRUCT var3);
   }

   public static class MOUSEINPUT extends Structure {
      public WinDef.LONG dx;
      public WinDef.LONG dy;
      public WinDef.DWORD mouseData;
      public WinDef.DWORD dwFlags;
      public WinDef.DWORD time;
      public BaseTSD.ULONG_PTR dwExtraInfo;

      public MOUSEINPUT() {
      }

      public MOUSEINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends WinUser.MOUSEINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   public static class MSG extends Structure {
      public WinDef.HWND hWnd;
      public int message;
      public WinDef.WPARAM wParam;
      public WinDef.LPARAM lParam;
      public int time;
      public WinUser.POINT pt;
   }

   public static class POINT extends Structure {
      public int x;
      public int y;

      public POINT() {
      }

      public POINT(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }

   public static class SIZE extends Structure {
      public int cx;
      public int cy;

      public SIZE() {
      }

      public SIZE(int w, int h) {
         this.cx = w;
         this.cy = h;
      }
   }

   public static class WINDOWINFO extends Structure {
      public int cbSize = this.size();
      public WinDef.RECT rcWindow;
      public WinDef.RECT rcClient;
      public int dwStyle;
      public int dwExStyle;
      public int dwWindowStatus;
      public int cxWindowBorders;
      public int cyWindowBorders;
      public short atomWindowType;
      public short wCreatorVersion;
   }

   public interface WNDENUMPROC extends StdCallLibrary.StdCallCallback {
      boolean callback(WinDef.HWND var1, Pointer var2);
   }
}
